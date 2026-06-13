package swiss.avm.poschti.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import swiss.avm.poschti.data.model.Category
import swiss.avm.poschti.data.model.MeasureUnit
import swiss.avm.poschti.data.model.ProductSource

/**
 * Ein einkaufbares Produkt im Katalog – entweder selbst erfasst (OWN) oder
 * aus Migros übernommen (MIGROS).
 */
@Entity(
    tableName = "products",
    indices = [Index(value = ["name"]), Index(value = ["migrosId"], unique = true)],
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: Category = Category.SONSTIGES,
    val defaultUnit: MeasureUnit = MeasureUnit.STUECK,
    val source: ProductSource = ProductSource.OWN,
    /** Migros-Artikel-ID (nur bei source == MIGROS). */
    val migrosId: String? = null,
    val imageUrl: String? = null,
    /** Alltagsprodukt: soll standardmässig immer auf der Liste landen. */
    val isStaple: Boolean = false,
    /** Standardmenge für Alltagsprodukte. */
    val stapleQuantity: Double? = null,
    /** Aktives Alltagsprodukt? (Erlaubt temporäres Deaktivieren ohne Löschen.) */
    val stapleActive: Boolean = true,
)
