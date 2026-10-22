# Setup, build and demonstration guide

This file is the practical checklist: get the project running, then record the video
without surprises.

---

## Part A — Getting the project running (30–60 min, mostly downloading)

### A1. Open the project

1. Copy the `smartpantry` folder somewhere sensible (e.g. `Documents/AndroidProjects/`).
2. In Android Studio: **File ▸ Open** → select the `smartpantry` folder itself
   (the one containing `settings.gradle`). Click **OK**, then **Trust Project**.
3. Android Studio will start a Gradle sync. The first sync downloads Gradle 8.9,
   AGP 8.7.3 and the AndroidX libraries — a few hundred MB. Let it finish.

### A2. If the sync complains

| Message | Fix |
|---|---|
| `SDK location not found` | Create `local.properties` in the project root with `sdk.dir=/path/to/Android/Sdk` |
| `Unsupported class file major version` / JDK error | **File ▸ Settings ▸ Build, Execution, Deployment ▸ Build Tools ▸ Gradle** → set *Gradle JDK* to **17** (Embedded JDK 17 is fine) |
| `platform 35 not found` | **Tools ▸ SDK Manager** → check **Android 15 (API 35)** under SDK Platforms and install |
| Gradle tries to download forever | Check the proxy/firewall; the wrapper needs `services.gradle.org` and `dl.google.com` |

### A3. Run it

1. **Device Manager** → create a virtual device. Pixel 6 with **API 35** (or API 34) is a
   good demo size. Start it.
2. Press **Run ▶** (`Shift+F10`). The app installs as **Smart Pantry**.

First launch shows the empty-pantry screen with the explanation. That is correct
behaviour, not a bug — the brief requires feedback for the zero-results state.

### A4. Sanity checklist before recording

- [ ] Pantry screen opens without crashing
- [ ] Settings ▸ **Load demo pantry** → 14 items appear on the Pantry screen
- [ ] Recipes tab shows **8** under "You can cook these now" and **4** under "Almost there"
- [ ] Tap a recipe → ingredients show ✓ or ✗ and the method is numbered
- [ ] Kill the app from the recents list, reopen it → the pantry is still there
- [ ] Add an ingredient with an empty name → the field shows a red error and nothing saves
- [ ] Rotate the emulator once while on the Pantry screen → no crash (`Ctrl+←`/`Ctrl+→`)

---

## Part B — The video (5–7 minutes)

Record at 1080p, narrate throughout, and keep to this timing. Total ≈ 6:15.

### 0:00–1:00 — GitHub walkthrough (~1 min)

* Open the **public** repository in a browser.
* Click **Commits** and scroll through the history while narrating the build-up:
  project skeleton → database helper → seeding → adapter → screens → matching logic →
  tests → README.
* Point out that the commits are spread across several days
  (`git log --pretty=format:"%h %ad %s" --date=short` shows this nicely).
* Mention `README.md` describes the app and the database choice.

> Do **not** show the assignment brief, your report or any other student's work on screen.

### 1:00–3:30 — Live demonstration (~2.5 min)

Run the app on the emulator, in this order:

1. **Empty state** — "On first run the pantry is empty, and instead of a blank screen the
   app explains what to do."
2. **Create** — Add *Banana*, 3, piece, expiry 4 days from now. It appears in the list and a
   toast confirms it.
3. **Read** — Point out the item count, the expiry badge and the banner counting items
   expiring soon.
4. **Update** — Tap *Banana*, change quantity to 6, save. The row updates immediately.
5. **Delete** — Swipe/tap the bin icon → confirm dialog → the row disappears.
6. **Validation** — Press **Add ingredient**, leave the name empty and type `-5` as the
   quantity, then Save. Two red errors appear and nothing is saved. Cancel.
7. **Strict matching** — Settings ▸ **Load demo pantry**. Recipes tab: **8 cookable**,
   **4 almost there**. Open one recipe to show the ingredient ticks.
8. **The clincher** — Back to Pantry, delete **Cheese**, return to Recipes:
   *Cheesy Garlic Bread*, *Mac and Cheese*, *Cheese Omelette* and *Grilled Cheese Sandwich*
   disappear from the suggestions. Add cheese back (200 g) and they return.
9. **Persistence** — Open the recents menu, swipe the app away, relaunch. The pantry is
   unchanged.

### 3:30–6:00 — Concept explanation (~2.5 min)

Choose **three** and keep the code on screen while you talk:

**a) Activity lifecycle** — open `MainActivity.java`, point at `onCreate()`
(one-time setup: binding, adapter, listeners) and `onResume()` (reload the pantry from
SQLite every time the screen becomes visible, which is why edits made elsewhere always
show up). Mention that `onDestroy()` clears the binding reference.

**b) Database end-to-end** — open `DatabaseHelper.java`: point at `onCreate()` building the
three tables and calling `seedRecipes()`, the `insert/query/update/delete` methods, the
`FOREIGN KEY ... ON DELETE CASCADE` on `recipe_ingredients`, and the `try/finally` cursor
closing.

**c) RecyclerView + Adapter** — open `PantryAdapter.java`: `onCreateViewHolder` inflates
`item_pantry.xml`, `onBindViewHolder` pushes a `PantryItem` into the row's TextViews,
`getItemCount` tells the list how many rows exist, and `notifyDataSetChanged()` after
`submitItems()` is what redraws it.

**d) Intents** — open `MainActivity.onEditItem()` and
`AddEditIngredientActivity.createIntent(...)`: the id travels as an `EXTRA_ITEM_ID` long
extra; the receiving activity reads it with `getLongExtra`. `NO_ID` means "create new".
Mention `startActivityForResult` is not needed here because `onResume()` reloads the data.

**e) Strict-matching algorithm** — open `RecipeMatcher.checkIngredient()`: normalise the
name, reject incompatible units, sum the pantry quantities in a base unit, then compare
with `>=` and an epsilon. Then `MatchResult.isFullMatch()` requires the missing list to be
empty — that is the single line that enforces "strict".

### 6:00–6:30 — Database justification (~30 sec)

"We used SQLite through SQLiteOpenHelper. The app is single-user and completely local, so
a cloud database would add network dependency and a backend for no benefit. SQLite gives
us real CRUD, transactions and foreign keys, it works offline, and it needs no permissions
or API keys — which also keeps the app free of any location or map service. And because it
is on-device, persistence after the app is closed is guaranteed and easy to demonstrate."

### 6:30–6:45 — Close

"GitHub history, working CRUD, the strict rule demonstrated live, and the code explained.
Thank you."

### Recording tips

* OBS: 1920×1080, 30 fps, MP4/H.264, CRF ~28 → a 6-minute recording is roughly 20–40 MB.
* Record the emulator window only, and increase the emulator text size
  (Settings ▸ Display ▸ Font size ▸ Large) so text is legible at 1080p.
* Do a full silent rehearsal first; check the running time with a stopwatch. Under 5:00
  or over 7:00 is penalised.
* Keep the mouse still while talking — moving cursors make the video hard to follow.

---

## Part C — Packaging the submission

```
Studentnumber_Surname_MobileAppDev700_Assignment.zip
├── smartpantry/                     ← the project, WITHOUT build/ and .gradle/
├── Studentnumber_Surname_MobileAppDev700_Assignment.docx   (or .pdf)
└── Studentnumber_Surname_MobileAppDev700_Assignment.mp4
```

Build the ZIP from the project root so the exclusions apply:

```bash
zip -r Studentnumber_Surname_MobileAppDev700_Assignment.zip \
    smartpantry \
    Studentnumber_Surname_MobileAppDev700_Assignment.docx \
    Studentnumber_Surname_MobileAppDev700_Assignment.mp4 \
    -x "smartpantry/build/*" \
       "smartpantry/app/build/*" \
       "smartpantry/.gradle/*" \
       "smartpantry/.idea/*" \
       "smartpantry/local.properties"
```

Then check the size — it must stay under **50 MB**:

```bash
du -h Studentnumber_Surname_MobileAppDev700_Assignment.zip
```

`gradle/wrapper/gradle-wrapper.jar` must be included (about 43 KB) or the marker cannot
build the project with the wrapper.
