# Appium Mobile Automation Framework

A Java + Appium 2 + TestNG mobile test automation framework built against
[Sauce Labs' **My Demo App** (Android)](https://github.com/saucelabs/my-demo-app-android) —
an open-source e-commerce demo app published specifically for Appium practice.

Built as a portfolio/SDET demonstration project: real Page Object Model
structure, thread-safe driver management, data-driven negative testing,
a full E2E purchase flow, ExtentReports, and a CI pipeline that actually
boots an emulator and runs the suite.

---

## 1. Project Overview

The suite covers:

- **Smoke**: app launch, valid login
- **Regression**: 11 data-driven negative login scenarios, Android back-button
  navigation, auth-gating of the product catalog
- **E2E**: full purchase journey — login → browse → product detail → add to
  cart → checkout (address → payment → overview) → place order → confirmation
  → continue shopping → logout

This is a **deliberately scoped subset**, not every scenario in a full
mobile-test-strategy checklist. What's real and runnable here: Android,
UiAutomator2, one demo app, the flows above. What's **stubbed for
extension, not implemented**: iOS execution (the driver factory supports
it, but nothing has been run against a real iOS target), deep links,
push notifications, DB/API correlation, and device-farm parallel runs.
See [§13 Honest scope notes](#13-honest-scope-notes).

---

## 2. Project Structure

```text
appium-mobile-automation/
├── pom.xml
├── testng.xml
├── README.md
├── .gitignore
├── .github/workflows/appium-ci.yml
│
├── src/main/java/
│   ├── config/       ConfigReader.java        # layered config: -D > env var > properties file
│   ├── driver/       DriverFactory.java        # ThreadLocal Appium driver, Android + iOS
│   ├── pages/        Page objects (POM)        # one class per screen
│   ├── utils/        WaitUtils, GestureUtils, ScreenshotUtils
│   └── constants/    AppConstants.java         # shared timeouts / app identifiers
│
├── src/test/java/
│   ├── base/         BaseTest.java             # per-method driver lifecycle
│   ├── listeners/    TestListener.java         # ExtentReports + screenshot-on-fail
│   └── tests/        LaunchTest, LoginTest, NavigationTest, E2EPurchaseTest
│
├── src/test/resources/
│   ├── config.properties
│   ├── log4j2.xml
│   └── testdata/login_data.json
│
├── reports/       # ExtentReports HTML output (generated)
├── screenshots/   # failure screenshots (generated)
└── logs/          # rolling log4j2 file logs (generated)
```

**Package responsibilities:**

| Package | Responsibility |
|---|---|
| `config` | Resolves any setting from `-D` flag → env var → `config.properties`, in that order |
| `driver` | Builds/owns the Appium session per thread; no test ever calls `new AndroidDriver()` directly |
| `pages` | One class per screen; owns its locators and screen-specific actions; tests never see a raw `By` |
| `utils` | Cross-cutting helpers: waits, gestures, screenshots — reused across every page/test |
| `constants` | Shared timeouts and app package/activity, read once |
| `base` | `BaseTest` gives every test class driver setup/teardown for free |
| `listeners` | TestNG listener wiring logging + ExtentReports + failure screenshots |
| `tests` | Actual `@Test` methods — thin, business-flow-shaped, POM-only |

---

## 3. Prerequisites

Install:

- **Java 17+** (`java -version`)
- **Maven 3.9+** (`mvn -version`)
- **Node.js 18+** (for Appium itself)
- **Appium 2.x** (`npm install -g appium`)
- **Appium UiAutomator2 driver** (`appium driver install uiautomator2`)
- **Android Studio + Android SDK** (for an emulator, or `adb` for a real device)
- **Appium Inspector** (desktop app) — for locator inspection, see §7

---

## 4. Environment Setup

```bash
# macOS/Linux example — adjust paths for your machine
export JAVA_HOME=$(/usr/libexec/java_home -v 17)      # macOS
export ANDROID_HOME=$HOME/Library/Android/sdk          # macOS
export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator
```

On Windows, set `JAVA_HOME` and `ANDROID_HOME` as system environment
variables and add `%ANDROID_HOME%\platform-tools` and
`%ANDROID_HOME%\emulator` to `PATH`.

---

## 5. Appium Setup

```bash
npm install -g appium
appium driver install uiautomator2
appium --version        # confirm Appium 2.x
appium                  # starts the server on http://127.0.0.1:4723
```

---

## 6. Device Setup

```bash
adb devices                                   # confirm a device/emulator is attached
emulator -avd <your_avd_name>                 # start an emulator, or use Android Studio's Device Manager
```

---

## 7. Application Setup — Sauce Labs My Demo App

1. Download the latest Android APK from the
   [My Demo App releases page](https://github.com/saucelabs/my-demo-app-android/releases)
   and place it at `apps/mda-<version>.apk` (this path is gitignored — download
   it locally rather than committing a binary).
2. `src/test/resources/config.properties` already points at
   `appPackage=com.saucelabs.mydemoapp.android` and its splash activity, and
   at `app=./apps/mda-2.2.0.apk` — update the filename to match what you
   downloaded.

### Inspecting the app with Appium Inspector

Before trusting any locator in this repo (or adding a new one), verify it
against the actual running app:

1. Start Appium (`appium`) and your emulator/device.
2. Open **Appium Inspector**, set the same capabilities as
   `DriverFactory`/`config.properties` uses (platformName, automationName,
   deviceName, app or appPackage/appActivity), and click **Start Session**.
3. Click any element in the mirrored screen. The right-hand panel shows:
   - **Accessibility ID** (`content-desc` on Android) — preferred, most stable
   - **Resource ID** — preferred when accessibility ID isn't set
   - **Class name**
   - **Text**
   - A generated **XPath** — last resort, only when nothing above is stable
   - On iOS, the equivalent **iOS predicate string** appears instead

The locators in `src/main/java/pages/*.java` are based on the
accessibility-id values Sauce Labs publishes for My Demo App, but **app
versions change** — re-verify with Inspector against the exact APK you run,
especially before wiring this into CI against a newer release.

---

## 8. Running Tests

```bash
mvn clean test                                          # full suite (testng.xml)
mvn clean test -Dgroups=smoke                            # smoke only
mvn clean test -Dgroups=regression                       # regression only
mvn clean test -Dplatform=android -Denv=qa                # explicit platform/env
mvn clean test -Dapp=./apps/mda-2.2.0.apk                 # override the APK path
mvn clean test -DsuiteXmlFile=testng.xml                  # explicit suite file
```

Any key in `config.properties` can be overridden the same way, e.g.
`-DdeviceName=Pixel_7_API_33` or `-DappiumServerUrl=http://127.0.0.1:4723`.

---

## 9. Reports

- **ExtentReports**: `reports/ExtentReport_<timestamp>.html` — open in any browser.
  Includes pass/fail status, groups, and embedded failure screenshots.
- **Screenshots**: `screenshots/<TestName>_<timestamp>.png`, captured automatically
  on any test failure via `TestListener`.
- **Logs**: `logs/automation.log` (rolling, log4j2) — every run appends here;
  passwords and tokens are never logged.

---

## 10. Data-Driven Testing

`LoginTest.invalidLoginIsRejected` is parameterised from
`src/test/resources/testdata/login_data.json` via a TestNG `@DataProvider`,
covering: invalid username, invalid password, both invalid, empty username,
empty password, both empty, malformed email, leading/trailing spaces,
special characters, an overlong username, and a locked account. Add a new
case to the JSON file — no code change needed.

---

## 11. CI/CD

`.github/workflows/appium-ci.yml` runs on every push/PR to `main`:

```text
Checkout → Java 17 → Node.js → Install Appium + UiAutomator2 driver
   → Download My Demo App APK → Boot Android emulator (via
     reactivecircus/android-emulator-runner) → Start Appium server
   → mvn test -Dgroups=smoke → Upload ExtentReport/screenshots/logs as artifacts
```

The job fails the build if any smoke test fails, since smoke tests gate
whether the app is even worth regression-testing further.

---

## 12. Troubleshooting

| Problem | Likely cause / fix |
|---|---|
| `Unable to find config.properties` | Not running from the project root, or `src/test/resources` isn't on the test classpath — run `mvn clean test` from the repo root |
| Appium server connection refused | Appium isn't running, or `appiumServerUrl` doesn't match the port it's on — check with `appium --log-timestamp` |
| Device not detected (`adb devices` empty) | Emulator not booted, USB debugging not enabled on a real device, or `ANDROID_HOME` not set |
| App not installed / `INSTALL_FAILED_*` | Wrong `app` path, insufficient storage on the emulator, or an existing conflicting install — try `fullReset=true` once |
| `NoSuchElementException` | Locator is stale for this app version — re-inspect with Appium Inspector (§7) rather than guessing |
| Test hangs waiting for an element | Usually a genuine timing issue — check `explicitWaitSeconds`, and confirm the screen actually reached the expected state (screenshot in the report) |
| `newCommandTimeout` session drops mid-debug | Increase `newCommandTimeout` in `config.properties` while stepping through manually |
| Wrong package/activity | Re-check `appPackage`/`appActivity` against `adb shell dumpsys window | grep mCurrentFocus` while the app is open |
| Emulator "offline" in CI | Cold-boot flakiness on hosted runners — the CI job already waits after starting Appium; consider bumping `sleep` or emulator boot timeout if it recurs |

---

## 13. Honest scope notes

This framework intentionally does **not** claim to implement the entire
original 43-section specification with full working code — that would be
tens of thousands of lines and mostly untested guesswork without a real
device farm, a backend, and a production build of the app. What's here is
real and runs. Extension points, in priority order if this project grows:

- **iOS**: `DriverFactory` already builds an `XCUITestOptions` session;
  needs My Demo App's iOS build + a matching `pages` variant (`@iOSXCUITFindBy`)
  or a cross-platform locator strategy.
- **API layer**: REST Assured is on the classpath but unused — My Demo App
  is UI-only with no public backend to hit independently.
- **DB validation**: not applicable without backend access.
- **Parallel/cross-device execution**: the ThreadLocal driver already
  supports it; `testng.xml` would need `parallel="tests"` and a
  device-per-thread capability matrix.
- **Permissions, orientation, deep links, notifications**: real Appium APIs
  exist for all of these (`context.grantPermission`, `driver.rotate(...)`,
  `mobile: deepLink`); not wired up here because they need scenarios specific
  to a target app that has those flows.
