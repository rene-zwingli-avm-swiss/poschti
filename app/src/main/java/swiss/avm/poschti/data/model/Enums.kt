package swiss.avm.poschti.data.model

/**
 * Ladenbereich / Produktkategorie – bestimmt die Gruppierung und Sortierung
 * der Einkaufsliste (Reihenfolge entspricht etwa dem Weg durch den Laden).
 */
enum class Category(val displayName: String) {
    FRUECHTE_GEMUESE("Früchte & Gemüse"),
    BROT_BACKWAREN("Brot & Backwaren"),
    MILCHPRODUKTE("Milchprodukte & Eier"),
    FLEISCH_FISCH("Fleisch & Fisch"),
    TIEFKUEHL("Tiefkühl"),
    VORRAT("Vorrat & Konserven"),
    GETRAENKE("Getränke"),
    SNACKS_SUESS("Snacks & Süsses"),
    HAUSHALT("Haushalt & Hygiene"),
    SONSTIGES("Sonstiges");
}

/** Mengeneinheit einer Zutat bzw. eines Einkaufslisten-Eintrags. */
enum class MeasureUnit(val displayName: String) {
    STUECK("Stk"),
    GRAMM("g"),
    KILOGRAMM("kg"),
    MILLILITER("ml"),
    LITER("l"),
    PACKUNG("Pack"),
    ESSLOEFFEL("EL"),
    TEELOEFFEL("TL"),
    PRISE("Prise"),
    NACH_BEDARF("n. Bedarf");
}

/** Herkunft eines Produkts. */
enum class ProductSource {
    OWN,
    MIGROS;
}

/** Herkunft eines Rezepts. */
enum class RecipeSource {
    OWN,
    FOOBY,
    SWISSMILK,
    MIGUSTO;
}
