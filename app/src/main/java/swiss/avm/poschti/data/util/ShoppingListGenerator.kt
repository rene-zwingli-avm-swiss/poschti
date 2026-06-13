package swiss.avm.poschti.data.util

import swiss.avm.poschti.data.local.entity.ProductEntity
import swiss.avm.poschti.data.local.entity.ShoppingItemEntity
import swiss.avm.poschti.data.local.relation.EntryWithRecipe
import swiss.avm.poschti.data.model.Category
import swiss.avm.poschti.data.model.MeasureUnit

/**
 * Erzeugt aus aktiven Alltagsprodukten und den Rezepten eines Wochenplans eine
 * konsolidierte Einkaufsliste.
 *
 * Konsolidierungsregel (MVP, vgl. Spec §10): Positionen mit gleichem Namen
 * (case-insensitive) UND gleicher Einheit werden zusammengefasst. Mengen werden
 * nur summiert, wenn beide eine Menge haben; sonst bleibt die Menge offen (null).
 * Unterschiedliche Einheiten ergeben getrennte Positionen.
 */
object ShoppingListGenerator {

    private data class Key(val name: String, val unit: MeasureUnit)

    private class Acc(
        var name: String,
        var unit: MeasureUnit,
        var quantity: Double?,
        var category: Category,
        var productId: Long?,
        var hadNullQuantity: Boolean,
    )

    fun generate(
        activeStaples: List<ProductEntity>,
        entries: List<EntryWithRecipe>,
    ): List<ShoppingItemEntity> {
        val acc = LinkedHashMap<Key, Acc>()

        fun add(
            name: String,
            unit: MeasureUnit,
            quantity: Double?,
            category: Category,
            productId: Long?,
        ) {
            val cleanName = name.trim()
            if (cleanName.isEmpty()) return
            val key = Key(cleanName.lowercase(), unit)
            val existing = acc[key]
            if (existing == null) {
                acc[key] = Acc(
                    name = cleanName,
                    unit = unit,
                    quantity = quantity,
                    category = category,
                    productId = productId,
                    hadNullQuantity = quantity == null,
                )
            } else {
                if (quantity == null) {
                    existing.hadNullQuantity = true
                } else if (existing.quantity != null) {
                    existing.quantity = existing.quantity!! + quantity
                } else {
                    existing.hadNullQuantity = true
                }
                if (existing.productId == null && productId != null) existing.productId = productId
            }
        }

        // 1. Alltagsprodukte
        for (p in activeStaples) {
            add(
                name = p.name,
                unit = p.defaultUnit,
                quantity = p.stapleQuantity,
                category = p.category,
                productId = p.id,
            )
        }

        // 2. Rezeptzutaten (auf geplante Portionen skaliert)
        for (entry in entries) {
            val recipe = entry.recipe.recipe
            val factor = if (recipe.defaultServings > 0) {
                entry.entry.servings.toDouble() / recipe.defaultServings
            } else {
                1.0
            }
            for (ing in entry.recipe.ingredients) {
                val name = ing.name.ifBlank { ing.rawText }
                val scaledQty = ing.quantity?.let { it * factor }
                add(
                    name = name,
                    unit = ing.unit,
                    quantity = scaledQty,
                    category = Category.SONSTIGES,
                    productId = ing.productId,
                )
            }
        }

        return acc.values.map {
            ShoppingItemEntity(
                shoppingListId = 0,
                productId = it.productId,
                name = it.name,
                // Wenn irgendeine Teilmenge unbekannt war, lassen wir die Menge offen.
                quantity = if (it.hadNullQuantity) null else it.quantity,
                unit = it.unit,
                category = it.category,
                checked = false,
                manuallyAdded = false,
            )
        }
    }
}
