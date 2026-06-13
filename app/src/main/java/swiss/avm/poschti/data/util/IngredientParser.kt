package swiss.avm.poschti.data.util

import swiss.avm.poschti.data.model.MeasureUnit

/** Ergebnis des Parsens einer Zutaten-Textzeile. */
data class ParsedIngredient(
    val quantity: Double?,
    val unit: MeasureUnit,
    val name: String,
)

/**
 * Zerlegt eine Zutaten-Zeile wie "200 g Mehl", "2 Zwiebeln", "1.5 dl Rahm"
 * oder "1/2 TL Salz" in Menge, Einheit und Name.
 *
 * Bewusst tolerant: Erkennt es keine Menge/Einheit, landet alles im Namen
 * (Menge = null, Einheit = NACH_BEDARF). Reicht für die MVP-Konsolidierung
 * (nur exakt gleiche Einheit + gleicher Name werden summiert).
 */
object IngredientParser {

    private val unitAliases: Map<String, MeasureUnit> = buildMap {
        put("stk", MeasureUnit.STUECK); put("stück", MeasureUnit.STUECK); put("stueck", MeasureUnit.STUECK); put("x", MeasureUnit.STUECK)
        put("g", MeasureUnit.GRAMM); put("gr", MeasureUnit.GRAMM); put("gramm", MeasureUnit.GRAMM)
        put("kg", MeasureUnit.KILOGRAMM); put("kilo", MeasureUnit.KILOGRAMM)
        put("ml", MeasureUnit.MILLILITER)
        put("l", MeasureUnit.LITER); put("liter", MeasureUnit.LITER)
        put("pack", MeasureUnit.PACKUNG); put("packung", MeasureUnit.PACKUNG); put("päckli", MeasureUnit.PACKUNG)
        put("el", MeasureUnit.ESSLOEFFEL); put("essl", MeasureUnit.ESSLOEFFEL); put("esslöffel", MeasureUnit.ESSLOEFFEL)
        put("tl", MeasureUnit.TEELOEFFEL); put("teel", MeasureUnit.TEELOEFFEL); put("teelöffel", MeasureUnit.TEELOEFFEL)
        put("prise", MeasureUnit.PRISE); put("prisen", MeasureUnit.PRISE)
    }

    fun parse(line: String): ParsedIngredient {
        val text = line.trim().replace(Regex("\\s+"), " ")
        if (text.isEmpty()) return ParsedIngredient(null, MeasureUnit.NACH_BEDARF, "")

        val tokens = text.split(" ")
        val quantity = parseQuantity(tokens[0])

        if (quantity == null) {
            return ParsedIngredient(null, MeasureUnit.NACH_BEDARF, text)
        }

        // Menge erkannt: prüfen, ob das nächste Token eine Einheit ist.
        var index = 1
        var unit = MeasureUnit.STUECK
        if (tokens.size > 1) {
            val candidate = tokens[1].lowercase().trimEnd('.')
            val mappedUnit = unitAliases[candidate]
            if (mappedUnit != null) {
                unit = mappedUnit
                index = 2
            }
        }

        val name = tokens.drop(index).joinToString(" ").trim()
        return ParsedIngredient(
            quantity = quantity,
            unit = unit,
            name = name.ifEmpty { text },
        )
    }

    private fun parseQuantity(token: String): Double? {
        val t = token.replace(',', '.')
        // Bruch wie 1/2
        if (t.contains('/')) {
            val parts = t.split('/')
            if (parts.size == 2) {
                val num = parts[0].toDoubleOrNull()
                val den = parts[1].toDoubleOrNull()
                if (num != null && den != null && den != 0.0) return num / den
            }
            return null
        }
        return t.toDoubleOrNull()
    }
}
