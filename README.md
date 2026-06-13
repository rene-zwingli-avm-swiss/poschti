# Poschti

Android-App zur Unterstützung des wöchentlichen Lebensmitteleinkaufs bei Migros.
Stellt eine Einkaufsliste zusammen aus **Alltagsprodukten** (immer vorhanden)
und **Zutaten geplanter Rezepte** (eigene sowie importiert von fooby.ch /
swissmilk.ch). Im Laden lässt sich die Liste im **Einkaufsmodus** anzeigen
(Bildschirm bleibt aktiv) und Positionen abstreichen.

Die vollständige Spezifikation steht in [SPEZIFIKATION.md](SPEZIFIKATION.md).

## Tech-Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Room** (lokale SQLite-Datenbank), **KSP**
- **Coroutines / Flow**, MVVM + Repository
- **Navigation Compose**
- **Jsoup** (für den späteren Rezeptimport, Phase 3)
- Min. SDK 26 (Android 8.0), Target/Compile SDK 35

## Voraussetzungen

- **Android Studio** (aktuelle Version) inkl. Android SDK 35
- JDK 17+ (Android Studio bringt ein passendes JDK mit)

## Projekt starten

1. Repo klonen
   ```
   git clone https://github.com/rene-zwingli-avm-swiss/poschti.git
   ```
2. In **Android Studio** über *Open* den Projektordner öffnen.
3. Gradle-Sync abwarten (lädt Abhängigkeiten und das Android SDK 35, falls nötig).
4. Auf Emulator oder Gerät ausführen (Run ▶).

Alternativ via Kommandozeile (benötigt lokales Android SDK; Pfad in
`local.properties` via `sdk.dir=...`):
```
./gradlew assembleDebug
```

## Projektstruktur

```
app/src/main/java/swiss/avm/poschti/
├─ PoschtiApplication.kt        App-Singletons (DB, Repository)
├─ MainActivity.kt              Einstiegspunkt (Compose)
├─ data/
│  ├─ model/                    Enums (Kategorie, Einheit, Quellen)
│  ├─ local/                    Room: Entities, DAOs, Database, Converters
│  └─ repository/               PoschtiRepository (zentrale Datenschicht)
└─ ui/
   ├─ theme/                    Material-3-Theme
   ├─ navigation/               Bottom-Navigation-Ziele
   ├─ products/                 Produkte & Alltagsprodukte (funktional)
   ├─ recipes/                  Rezepte (funktional)
   └─ PoschtiApp.kt             NavHost + Scaffold
```

## Stand (Phase 1)

Umgesetzt:
- Projektgerüst, Datenmodell (alle Entities gemäss Spec)
- **Produkte**: anlegen, löschen, als Alltagsprodukt markieren
- **Rezepte**: eigene Rezepte mit Name, Portionen und Zutaten anlegen/löschen
- Navigation mit Platzhaltern für **Woche** und **Liste**

Als Nächstes (Phase 2): Wochenplanung, Listengenerierung mit Konsolidierung,
Einkaufsmodus (Bildschirm an, Abstreichen). Siehe Roadmap in der Spezifikation.
