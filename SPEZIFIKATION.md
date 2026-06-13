# Poschti – Spezifikation (Android)

*Wocheneinkauf-Helfer: Alltagsprodukte + Rezeptplanung → Einkaufsliste.*

**Version:** 0.1 (Entwurf)
**Datum:** 2026-06-13
**Status:** Zur Abstimmung

---

## 1. Ziel und Zweck

Eine Android-App, die den wöchentlichen Lebensmitteleinkauf bei Migros
unterstützt. Die App stellt eine Einkaufsliste zusammen aus:

1. **Alltagsprodukten** – Dinge, die immer vorhanden sein sollen (z.B. Milch,
   Brot, Butter).
2. **Rezeptbasierten Produkten** – Zutaten aus Rezepten, die für die Woche
   geplant werden.

Der Benutzer plant eine Woche per Auswahl von Rezepten. Aus den Alltagsprodukten
und den Zutaten der geplanten Rezepte entsteht automatisch eine konsolidierte
Einkaufsliste. Diese Liste kann im Laden eingeblendet werden (Bildschirm bleibt
dabei aktiv), und bereits eingekaufte Produkte können abgestrichen werden.

---

## 2. Begriffe

| Begriff             | Bedeutung                                                              |
|---------------------|------------------------------------------------------------------------|
| **Produkt**         | Ein einkaufbarer Artikel (eigener Katalog oder Migros-Artikel).        |
| **Alltagsprodukt**  | Produkt, das standardmässig immer auf der Liste landen soll.           |
| **Rezept**          | Eigene oder importierte Rezeptur mit Name und Zutatenliste.            |
| **Zutat**           | Ein Produkt mit Menge/Einheit im Kontext eines Rezepts.                |
| **Wochenplan**      | Auswahl von Rezepten (und Mengen) für eine bestimmte Kalenderwoche.    |
| **Einkaufsliste**   | Konsolidierte, abstreichbare Liste aus Alltagsprodukten + Wochenplan.  |
| **Einkaufsmodus**   | Anzeige der Liste im Laden mit aktivem Bildschirm und Abstreichfunktion.|

---

## 3. Funktionale Anforderungen

### 3.1 Produktkatalog (Hybrid: Migros-API + eigener Katalog)

- **FR-1** Der Benutzer kann Produkte über die Migros-Produktsuche suchen und
  in seinen Katalog übernehmen (Name, Kategorie, optional Bild/Preis/Migros-ID).
- **FR-2** Der Benutzer kann **eigene Produkte** manuell anlegen (Name,
  Kategorie, Standardeinheit), unabhängig von Migros.
- **FR-3** Produkte werden Kategorien zugeordnet (z.B. Früchte/Gemüse,
  Milchprodukte, Tiefkühl …), damit die Einkaufsliste nach Ladenbereichen
  sortierbar ist.
- **FR-4** Fällt die Migros-API aus, bleibt die App mit dem lokalen Katalog voll
  funktionsfähig (eigene Produkte + bereits gespeicherte Migros-Produkte).

### 3.2 Alltagsprodukte

- **FR-5** Der Benutzer kann Produkte als „Alltagsprodukt" markieren.
- **FR-6** Optional je Alltagsprodukt: Standardmenge und ein einfacher Rhythmus
  (jede Woche / jede zweite Woche). *(MVP: nur „jede Woche".)*
- **FR-7** Beim Generieren der Einkaufsliste werden alle aktiven
  Alltagsprodukte automatisch aufgenommen.

### 3.3 Rezepte

- **FR-8 Eigene Rezepte:** Der Benutzer kann Rezepte mit eigenem Namen und
  einer Zutatenliste (Produkt + Menge + Einheit) erstellen, bearbeiten, löschen.
- **FR-9 Importierte Rezepte (halbautomatisch):** Der Benutzer gibt eine URL von
  **fooby.ch** oder **swissmilk.ch** ein. Die App lädt die Seite, extrahiert
  Name und Zutaten (primär über `schema.org/Recipe`-JSON-LD), und zeigt sie zur
  **Prüfung/Korrektur** an, bevor das Rezept gespeichert wird.
- **FR-10** Beim Import wird der Quell-Link gespeichert und im Rezept angezeigt
  (Verweis auf die Originalquelle, kein Re-Publishing der Inhalte).
- **FR-11** Optional: Rezepte aus Migusto (über die Migros-API) suchen und
  importieren – analog zum Web-Import. *(Nice-to-have, nicht MVP.)*
- **FR-12** Zutaten eines importierten Rezepts werden – wo möglich – auf
  Produkte im Katalog gemappt (Matching per Name; bei Unklarheit fragt die App).
- **FR-13** Rezepte haben eine Standard-Portionenzahl; beim Planen kann die
  Portionenzahl skaliert werden (Mengen rechnen entsprechend hoch/runter).

### 3.4 Wochenplanung

- **FR-14** Der Benutzer wählt eine Kalenderwoche und ordnet ihr Rezepte zu
  (z.B. pro Wochentag oder einfach als Liste – siehe offene Punkte).
- **FR-15** Pro geplantem Rezept kann die Portionenzahl angepasst werden.
- **FR-16** Der Benutzer sieht eine Übersicht des Wochenplans.
- **FR-17** Wochenpläne werden gespeichert und sind später wieder abrufbar.

### 3.5 Einkaufsliste generieren

- **FR-18** Aus „aktive Alltagsprodukte" + „Zutaten aller Rezepte des
  Wochenplans" wird eine **konsolidierte** Liste erzeugt.
- **FR-19** Gleiche Produkte werden zusammengefasst und Mengen – sofern
  Einheiten kompatibel sind – summiert (z.B. 200 g + 300 g = 500 g). Bei
  inkompatiblen Einheiten werden Positionen getrennt aufgeführt.
- **FR-20** Die Liste ist nach Kategorie/Ladenbereich gruppiert und sortiert.
- **FR-21** Der Benutzer kann vor dem Einkauf manuell Positionen ergänzen,
  bearbeiten oder entfernen.

### 3.6 Einkaufsmodus (im Laden)

- **FR-22** Die Einkaufsliste kann als Einkaufsansicht eingeblendet werden.
- **FR-23** Solange diese Ansicht aktiv ist, bleibt der **Bildschirm an**
  (kein Auto-Sleep). Beim Verlassen wird das Verhalten zurückgesetzt.
- **FR-24** Einzelne Positionen können **abgestrichen** (als „im Warenkorb"
  markiert) werden; abgestrichene Positionen werden visuell abgesetzt
  (z.B. durchgestrichen, ans Ende gruppiert).
- **FR-25** Abstreichen ist umkehrbar (versehentliches Antippen rückgängig).
- **FR-26** Optionaler Fortschritt: „X von Y erledigt". *(Nice-to-have.)*

### 3.7 Synchronisation (Google Drive)

- **FR-27** Der Benutzer kann sich mit seinem Google-Konto anmelden.
- **FR-28** App-Daten (Katalog, Rezepte, Wochenpläne, Einstellungen) werden in
  den **App-Data-Folder** von Google Drive gesichert und von dort wieder
  hergestellt – damit über mehrere Geräte / nach Neuinstallation synchron.
- **FR-29** Die App funktioniert auch **ohne** Anmeldung vollständig offline;
  Sync ist optional.
- **FR-30** Konflikte (gleiche Daten auf zwei Geräten geändert) werden über
  „letzte Änderung gewinnt" je Datensatz aufgelöst. *(MVP-Strategie; siehe
  offene Punkte.)*

---

## 4. Datenquellen – Details und Risiken

### 4.1 Migros (Produktdaten)
- **Basis:** Inoffizieller `migros-api-wrapper` (aliyss/migros-api-wrapper).
  In der Android-App werden die dahinterliegenden HTTP-Endpunkte direkt
  angesprochen (kein Node.js zur Laufzeit nötig).
- **Verwendung:** Produktsuche und Verfügbarkeit über Gast-Token.
- **Risiko:** Nicht offiziell unterstützt, Endpunkte ändern sich ggf. ohne
  Vorankündigung. → **Mitigation:** Hybrid-Architektur mit eigenem Katalog als
  Fallback (FR-4); API-Zugriff in einer austauschbaren Schicht kapseln.

### 4.2 fooby.ch / swissmilk.ch (Rezepte)
- **Verfahren:** Seite laden, `schema.org/Recipe`-JSON-LD extrahieren
  (Name, `recipeIngredient`, Portionen). Fallback: HTML-Parsing.
- **Benutzer bestätigt** die extrahierten Zutaten vor dem Speichern (FR-9).
- **Rechtlich:** Nur für den persönlichen Gebrauch; gespeichert wird die
  Zutatenliste + Quell-Link, keine Veröffentlichung der Rezepttexte. Siehe §8.

---

## 5. Datenmodell (Entwurf)

```
Product
  id, name, category, defaultUnit, source (OWN|MIGROS),
  migrosId?, imageUrl?, isStaple (Alltagsprodukt), stapleQty?, active

Recipe
  id, name, source (OWN|FOOBY|SWISSMILK|MIGUSTO), sourceUrl?,
  defaultServings, createdAt, updatedAt

RecipeIngredient
  id, recipeId, productId?, rawText, quantity, unit

WeekPlan
  id, isoYear, isoWeek, createdAt, updatedAt

WeekPlanEntry
  id, weekPlanId, recipeId, servings, dayOfWeek?

ShoppingList
  id, weekPlanId?, generatedAt

ShoppingItem
  id, shoppingListId, productId?, name, quantity, unit,
  category, checked (abgestrichen), manuallyAdded
```

---

## 6. Empfohlener Tech-Stack

| Bereich            | Empfehlung                                                |
|--------------------|-----------------------------------------------------------|
| Sprache            | **Kotlin**                                                |
| UI                 | **Jetpack Compose** (Material 3)                          |
| Architektur        | MVVM + Repository-Pattern, UDF (StateFlow)                |
| Lokale DB          | **Room** (SQLite)                                         |
| Netzwerk           | **Retrofit + OkHttp**, Kotlinx-Serialization              |
| Asynchron          | **Coroutines / Flow**                                     |
| Bildschirm an      | `keepScreenOn`-Modifier bzw. `FLAG_KEEP_SCREEN_ON`        |
| Google-Sync        | Google Sign-In + Drive REST API (`appDataFolder`-Scope)   |
| HTML/JSON-LD-Parse | OkHttp + Jsoup (für JSON-LD-Extraktion)                   |
| Min. SDK           | API 26 (Android 8.0) als Vorschlag                        |

---

## 7. Nicht-funktionale Anforderungen

- **NFR-1 Offline-First:** Kernfunktionen (Katalog, Rezepte, Planung, Liste,
  Einkaufsmodus) müssen ohne Internet funktionieren. Migros-Suche und Sync sind
  online-abhängig und degradieren sauber.
- **NFR-2 Robustheit:** Externe Datenquellen (Migros/Rezepte) dürfen die App nie
  zum Absturz bringen; Fehler werden abgefangen und verständlich gemeldet.
- **NFR-3 Bedienbarkeit im Laden:** grosse Tap-Flächen, lesbar bei
  Sonnenlicht/Einhandbedienung; Einkaufsmodus mit minimaler Ablenkung.
- **NFR-4 Performance:** Listengenerierung und Anzeige < 1 s bei üblicher
  Datenmenge.
- **NFR-5 Wartbarkeit:** Externe APIs hinter Interfaces kapseln (austauschbar).

---

## 8. Datenschutz & Recht

- Keine Weitergabe von Personendaten an Dritte; Daten liegen lokal bzw. im
  privaten Google-Drive des Benutzers.
- Rezeptimport nur für den **persönlichen Gebrauch**; Quellen (fooby.ch,
  swissmilk.ch, Migusto) werden verlinkt, Inhalte nicht weiterveröffentlicht.
- Nutzung der inoffiziellen Migros-API geschieht auf eigenes Risiko und ohne
  Unterstützung durch Migros; Nutzungsbedingungen von migros.ch sind zu beachten.
- Google-Login: nur minimal nötiger Scope (`drive.appdata`), keine weiteren
  Drive-Dateien des Benutzers.

---

## 9. MVP-Abgrenzung

**Im MVP enthalten:**
- Eigener Produktkatalog + Alltagsprodukte
- Eigene Rezepte
- Rezeptimport (halbautomatisch) von fooby.ch / swissmilk.ch
- Wochenplanung (Rezeptauswahl + Portionen)
- Listengenerierung mit Konsolidierung
- Einkaufsmodus (Bildschirm an, Abstreichen)
- Lokale Speicherung (Room)

**Nach dem MVP:**
- Migros-Produktsuche (inoffizielle API)
- Google-Drive-Sync
- Migusto-Rezeptimport
- Alltagsprodukt-Rhythmus (jede zweite Woche), Fortschrittsanzeige

---

## 10. Offene Punkte / Entscheidungen

1. **Wochenstruktur:** Rezepte fix pro Wochentag (Mo–So) zuordnen, oder freie
   Rezeptliste pro Woche ohne Tagesbezug? *(Empfehlung MVP: freie Liste,
   Wochentag optional.)*
2. **Einheiten/Konsolidierung:** Wie weit soll umgerechnet werden (g↔kg, ml↔l,
   „Stk", „Prise")? *(Empfehlung MVP: nur exakte Einheitengleichheit summieren,
   Rest getrennt.)*
3. **Produkt-Matching bei Import:** automatisches Namens-Matching vs. immer
   manuelle Zuordnung. *(Empfehlung: Vorschlag + Bestätigung.)*
4. **Sync-Konflikte:** „letzte Änderung gewinnt" ausreichend, oder feinere
   Merge-Logik nötig?
5. **Mehrere Haushaltsmitglieder gleichzeitig?** (Geteilter Drive-Ordner)
   – aktuell nicht vorgesehen.

---

## 11. Grobe Umsetzungsphasen

1. **Phase 1 – Fundament:** Projekt-Setup (Kotlin/Compose/Room), Datenmodell,
   Produktkatalog + Alltagsprodukte, eigene Rezepte.
2. **Phase 2 – Planung & Liste:** Wochenplanung, Listengenerierung mit
   Konsolidierung, Einkaufsmodus (Bildschirm an, Abstreichen).
3. **Phase 3 – Import:** halbautomatischer Rezeptimport (fooby/swissmilk).
4. **Phase 4 – Migros-Integration:** Produktsuche über inoffizielle API.
5. **Phase 5 – Sync:** Google-Sign-In + Drive-`appDataFolder`-Sync.
```
