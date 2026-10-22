# Smart Pantry Manager

A Java Android application that suggests recipes based **strictly** on the ingredients a
user already has at home, in order to cut food waste.

Built for **Mobile App Development 700 — Practical Assignment** (Richfield Graduate
Institute of Technology).

---

## 1. What the app does

| Feature | Where |
|---|---|
| Add, edit and delete pantry ingredients (name, quantity, unit, expiry date) | Pantry List → Add/Edit screen |
| Pantry list bound to SQLite through a RecyclerView + custom adapter | Pantry screen |
| 20 seeded recipes (name, ingredient list, method, servings) | Seeded on first run |
| Suggested Recipes screen using the strict-matching rule | Recipes tab |
| Recipe detail with full ingredient list, method and per-ingredient availability | Tap any suggested recipe |
| Settings (expiry alerts, "Almost there" list, demo data) | Settings tab |
| Clear feedback when zero recipes match | Recipes tab empty state |

**Five screens** — `MainActivity`, `AddEditIngredientActivity`, `SuggestedRecipesActivity`,
`RecipeDetailActivity`, `SettingsActivity` — each a separate Activity, connected by Intents
and a bottom navigation bar.

## 2. The strict-matching rule (core logic)

> A recipe is suggested **only** when every one of its ingredients is in the pantry, in at
> least the required quantity.

Implemented in `logic/RecipeMatcher.java`, which is deliberately **Android-free** so the rule
can be tested on the JVM. The pipeline is:

1. **Normalise the name** (`IngredientNameNormalizer`) — lower-case, strip punctuation and
   preparation words ("fresh", "chopped", "cloves"), convert plurals to singular
   ("tomatoes" → "tomato"), and map regional synonyms ("mielies"/"mealies" → "corn",
   "aubergine" → "eggplant").
2. **Check units are comparable** (`UnitConverter`) — mass (g/kg/mg), volume (ml/L/cup/tbsp)
   and countable units are separate families, so 200 g of cheese is never matched against
   2 pieces of cheese. Products that differ are kept distinct: `tomato` ≠ `tinned tomato`.
3. **Convert and add up** — all matching pantry entries are summed in a base unit, so
   0.5 kg of butter plus 60 g of butter satisfies a 100 g requirement.
4. **Decide** — satisfied only when the total is ≥ the required amount (with a small
   epsilon for floating-point noise). `MatchResult.isFullMatch()` then requires *every*
   ingredient to be satisfied.

Optional extra: `RecipeMatcher.almostMatches()` returns recipes missing exactly one
ingredient, shown in a clearly separated "Almost there" section. Strict matches are never
mixed with it.

## 3. Database choice — SQLite (SQLiteOpenHelper)

Chosen because:

* It is the on-device persistence approach covered in the module's persistent-data chapter,
  so the implementation can be explained and defended line by line.
* The app is single-user and entirely local — there is no data that needs to be shared
  between devices, so a cloud database (Firebase/PostgreSQL) would add network failure
  modes and a backend to maintain for no user benefit.
* It works offline, needs no account, no API key and no internet permission, which also
  keeps the assignment clear of anything resembling a location/map feature.
* SQL gives full CRUD with real foreign keys and transactions, so the "data must persist
  after the app is closed" requirement is trivially demonstrable.

### Data model

```
recipes (1) ────< recipe_ingredients
_id                _id
name               recipe_id  (FK, ON DELETE CASCADE)
description        name
servings           quantity
steps              unit

pantry_items
_id
name
quantity
unit
expiry_date
```

`pantry_items` is intentionally **not** a foreign key to the recipes: a recipe describes
what is *needed*, the pantry describes what is *owned*, and the comparison happens in
Java. See `docs/diagrams/er_diagram.png`.

## 4. Project structure

```
app/src/main/
├── AndroidManifest.xml
├── java/za/co/richfield/smartpantry/
│   ├── MainActivity.java               Pantry list + bottom navigation
│   ├── AddEditIngredientActivity.java  Create / update form with validation
│   ├── SuggestedRecipesActivity.java   Strict matches (+ optional "Almost there")
│   ├── RecipeDetailActivity.java       Ingredients and method
│   ├── SettingsActivity.java           Preferences and demo data
│   ├── data/
│   │   ├── DatabaseHelper.java         SQLiteOpenHelper: schema, CRUD, seeding
│   │   └── DatabaseSeeder.java         20 recipes + demo pantry
│   ├── logic/
│   │   ├── RecipeMatcher.java          ★ the strict-matching rule
│   │   ├── IngredientNameNormalizer.java
│   │   └── UnitConverter.java
│   ├── ui/
│   │   ├── PantryAdapter.java          RecyclerView.Adapter for pantry rows
│   │   └── MatchAdapter.java           Two view types: section header + recipe card
│   ├── model/                          PantryItem, Recipe, RecipeIngredient,
│   │                                   MatchResult, IngredientCheck
│   └── util/                           DateUtils, AppPreferences
└── res/
    ├── layout/                        5 activity layouts + 3 item layouts
    ├── values/                        strings, colors, themes (Times-safe, Material 3)
    ├── drawable/                      vector icons and badge backgrounds
    └── menu/menu_bottom_nav.xml
```

## 5. Setup and run instructions

**Requirements:** Android Studio (Ladybug or newer), JDK 17, Android SDK 35.

> **Fastest route:** if you only want to see the app running, install
> `submission/SmartPantry-debug.apk` on an Android phone (copy it across and tap it, or
> run `adb install SmartPantry-debug.apk`). Scroll to "Run without Android Studio" below.
> To edit the code or record the demonstration, open the project in Android Studio.

1. Open Android Studio → **File ▸ Open** and select this folder (`smartpantry/`).
   Do **not** use "Import project" — it is already a standard Gradle project.
2. Let Gradle sync finish. The project uses the Gradle wrapper (Gradle 8.9) and
   Android Gradle Plugin 8.7.3, so no manual Gradle install is needed.
3. If the SDK path is not detected, add a `local.properties` file in the project root:
   ```
   sdk.dir=/path/to/Android/Sdk
   ```
   (Windows example: `sdk.dir=C\:\\Users\\you\\AppData\\Local\\Android\\Sdk`)
4. Start an emulator (API 24 or higher; API 34/35 recommended) and press **Run ▶**.

**In the app:**

* First launch shows an empty pantry with an explanation — this is intended.
* Go to **Settings ▸ Load demo pantry** to insert 14 everyday ingredients instantly
  (this is the fastest way to demonstrate matching during the video).
* Return to **Recipes**: 8 recipes appear as strict matches and 4 under "Almost there".
* Delete **Cheese** from the pantry and return to Recipes: the cheesy recipes disappear.
  Adding a **Baking powder** entry makes *Fluffy Pancakes* appear.

### Running the unit tests

```bash
# From the project root
./gradlew testDebugUnitTest          # macOS / Linux
gradlew.bat testDebugUnitTest        # Windows

# Or in Android Studio: right-click app/src/test ▸ Run 'Tests in za.co.richfield.smartpantry'
```

24 tests cover the matcher, the name normaliser, the unit converter and the shipped
seed data (including a test that removing cheese really does remove the cheesy recipes
from the suggestions).

## 6. Repository contents

The repository root is this `smartpantry` folder. It is one part of a larger delivery
folder, which looks like this (see `../README.md`):

```
SmartPantry-Manager/
├── README.md                  index for the whole delivery
├── SmartPantry-debug.apk      the built app, ready to install
├── Studentnumber_Surname_MobileAppDev700_Assignment.docx
└── smartpantry/               <- this repository (open this in Android Studio)
    ├── app/…                  Java sources, layouts, unit tests
    └── docs/
        ├── diagrams/          screen_flow.png and er_diagram.png used in the report
        ├── build_report.py    regenerates the .docx into the delivery folder root
        ├── SETUP_AND_DEMO.md  build, run and video demonstration script
        └── GIT_COMMIT_PLAN.md ten meaningful, spaced-out commits
```

Run `docs/build_report.py` to regenerate the written report; it writes the .docx to the
root of the delivery folder.

## 7. Notes for the marker

* Written entirely in Java — every source file under `app/src/main/java` is `.java`.
* No Google Maps, mapping SDK, GPS or location services. The app requests **no system
  permissions** — no location, internet, storage or camera access. (The merged manifest
  contains one AndroidX-generated internal broadcast permission, which is not a
  user-facing or privacy permission.)
* Data persists across app restarts because every screen reloads from SQLite in
  `onResume()` rather than holding a list in memory.
* Input validation prevents empty names, non-numeric or negative quantities, a missing
  unit and expiry dates in the past.

## 8. References

* Android Developers — *Meet Android Studio* (project view, `kotlin+java` source group).
* Android Developers — *Save data using SQLite* and *SQLiteOpenHelper*.
* Android Developers — *Create dynamic lists with RecyclerView*.
* Google Material Components for Android — Material 3 components and themes.
* JUnit 4 documentation.

---

## 9. Run without Android Studio (quick check)

The debug APK in `submission/SmartPantry-debug.apk` is already built and signed with the
standard Android debug key, so it installs on any Android 7.0 (API 24) or newer device:

```bash
adb install -r SmartPantry-debug.apk     # with a phone connected over USB
```

Or copy the file to the phone and tap it (allow "Install unknown apps" for your file
manager first). The app appears in the launcher as **Smart Pantry**.

Note that this APK is a *build output*, not the source: any code change requires
re-running the Gradle build, which is why the Android Studio route is the one to use while
developing and recording.
