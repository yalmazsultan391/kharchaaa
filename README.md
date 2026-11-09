# Kharchaaa v3.0 — Every rupee, accounted for.

Kharchaaa is an expense tracker. The original app was built by **Muhammad Abdullah Khalid (ByteBuddy Devs)** — all credit belongs to him. Version 3.0 is a fresh, standalone rebuild (no code copied from the original).

## Features

- **Dashboard** — Total Balance / Total Income / Total Expense / Lists cards, income vs. expense bar chart (last 6 months, canvas), this month's expense category donut, and Insights (top spending category, this month vs. last month).
- **Budgets** — a budget per list with a progress bar (% used; amber above 80%, red with a warning above 100%).
- **Categories** — Food, Rent, Utilities, Transport, Shopping, Health, Education, Shop, Salary, Other — every entry has a category and an icon.
- **Entry editing** — entries can be edited or deleted; every entry has a date.
- **History** — text search and filters by list, category, month, and income/expense; PDF export, JSON export/import, and **CSV export**.
- **PIN lock** — optional 4-digit PIN; when set, the app asks for it on launch.
- **Themes** — default Royal Navy & Gold. The theme picker offers Royal Navy & Gold, Emerald & Gold, Midnight Violet, and Light.
- **Refresh animation** — Receipt / Scale / Off.
- **Migration** — data from the v2.1.0 web app (`kharchaaa-data-v2`) is imported automatically on first launch (budget 0 = no budget set, categories assigned by entry type).
- **Hide/show balance** (eye button).

## What's new in v3.0

- Dashboard with charts and insights
- Categories for every entry
- Budget progress bar with over-budget warning
- Entry editing
- History search
- CSV export
- PIN lock
- Emerald & Gold theme (default theme: Royal Navy & Gold)

## From the original Android app (v1.9)

Lists (name, colour, quick chips), income/expense entries, hide/show balance, History filters, PDF download, monthly totals, pull-to-refresh animations (Receipt/Scale), delete with confirmation.

## Run it

No build step — it is a static site:

```bash
python3 -m http.server
# then open http://localhost:8000 in a browser
```

Or deploy on Vercel: `index.html` is at the repo root; framework "Other", no build command.

## Data

All data is stored in the browser's `localStorage` (`kharchaaa-data-v3` key). For a backup, export JSON from the History tab.

## Credit

- Developer: **Muhammad Abdullah Khalid**
- Team: ByteBuddy Devs
- Support: abdullahjoyiaofficial@gmail.com
- Version: 3.0

---

## Repository layout

- `index.html` — the v3.0 web app (static site). Vercel: framework "Other", no build command, root directory.
- `android/` — the Android (WebView) project that wraps the same web app into an APK.

## Android app (APK) — built by GitHub Actions

`android/` is a Gradle project (Kotlin, AGP 8.5.2, compileSdk 34, minSdk 24, targetSdk 34, package `com.bytebuddy.kharchaaa`, versionCode 11, versionName 3.0 — versionCode is the original APK v1.9's 10 + 1). `MainActivity` is a thin WebView shell that loads `app/src/main/assets/index.html` (a copy of the web app). Its only difference from the root `index.html` is a small guarded hook: when `window.AndroidBridge` exists, PDF/JSON/CSV downloads go through the native bridge and are saved in Downloads/Kharchaaa on the phone; on the plain website the bridge is absent and normal browser downloads are used. The launcher icon is the original v1.9 APK's icon, reused unchanged.

The APK is **not** built on a local machine — there is no JDK/Android SDK here. Push to `main` (or run the workflow manually) and GitHub Actions builds it: see `.github/workflows/android.yml` ("Build APK" — JDK 17, Android SDK, Gradle 8.7, `gradle :app:assembleDebug`). The finished debug APK is uploaded as the `Kharchaaa-v3.0-APK` artifact — download it from the Actions run.

Note on the Gradle wrapper: `android/gradle/wrapper/gradle-wrapper.properties` (Gradle 8.7) is included, but the wrapper JAR (`gradle-wrapper.jar`) is not — so `./gradlew` will not work from this repo as-is. The Actions workflow does not depend on the wrapper: it installs Gradle 8.7 via `gradle/actions/setup-gradle` and runs plain `gradle`. To build locally, install Gradle 8.7 (or run `gradle wrapper` once to generate the wrapper files), set `ANDROID_HOME`, and run `gradle :app:assembleDebug` from `android/`.
