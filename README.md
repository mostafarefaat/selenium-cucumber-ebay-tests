# eBay Automation Framework

A **Selenium + Cucumber (BDD) + TestNG** test automation framework built in Java, using the **Page Object Model (POM)** design pattern. This framework automates end-to-end user flows on [eBay.com](https://www.ebay.com), including product search, results validation, and filter application.

---

## 📌 Table of Contents

- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Naming Conventions](#-naming-conventions)
- [Design Pattern](#-design-pattern)
- [Prerequisites](#-prerequisites)
- [Setup Instructions](#-setup-instructions)
- [Running Tests](#-running-tests)
- [Configuration](#-configuration)
- [Reporting](#-reporting)
- [Retry Mechanism](#-retry-mechanism)
- [Writing New Tests](#-writing-new-tests)
- [Contributing](#-contributing)

---

## 🛠 Tech Stack

| Tool / Library | Purpose |
|---|---|
| **Java 25** | Core language |
| **Selenium WebDriver 4.37.0** | Browser automation |
| **Cucumber (Java + TestNG) 7.33.0** | BDD test scripting (Gherkin) |
| **TestNG 7.11.0** | Test execution engine & assertions |
| **WebDriverManager 6.3.2** | Automatic browser driver management |
| **Maven** | Build & dependency management |
| **Jackson Databind** | JSON data parsing (test data) |
| **Apache Commons IO** | File utilities (screenshots) |

---

## 📁 Project Structure

```
ebayAutomationFramework/
│
├── .idea/                      # IDE config (git-ignored in most setups)
├── .mvn/                       # Maven wrapper files
│
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── abstracts/
│   │       │   └── Abstract.java
│   │       ├── managers/
│   │       │   └── PageManager.java
│   │       ├── pages/
│   │       │   ├── LandingPage.java
│   │       │   └── (other page classes...)
│   │       └── resources/
│   │           └── GlobalData.properties
│   │
│   └── test/
│       ├── java/
│       │   ├── cucumber/
│       │   │   └── searchSteps.java
│       │   ├── runner/
│       │   │   └── TestRunner.java
│       │   └── testcomponents/
│       │       └── BaseTest.java
│       │
│       └── resources/
│           └── features/
│               └── search-flow.feature
│
├── target/                     # Build output (git-ignored)
│   ├── classes/
│   ├── generated-sources/
│   ├── generated-test-sources/
│   ├── test-classes/
│   ├── cucumber.html           # Test execution report
│   └── cucumber.json
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🏷 Naming Conventions

This framework follows standard Java naming conventions.

### Packages
> **Best practice:** Java package names should be **all lowercase**, with no underscores (e.g. `pages`, `managers`, `testcomponents`, `cucumber`).
>
> ✅ All packages in this project (`abstracts`, `managers`, `pages`, `resources`, `cucumber`, `runner`, `testcomponents`) are all-lowercase, fully aligned with convention.

| Convention | Example |
|---|---|
| Packages | `pages`, `managers`, `cucumber`, `testcomponents` |
| Classes | `PascalCase` → `LandingPage`, `PageManager`, `BaseTest` |
| Methods | `camelCase`, verb-first → `isLandingPageLoaded()`, `searchForItem()`, `filterByTransmission()` |
| Boolean methods | Prefix with `is` / `has` / `are` → `isUrlCorrect()`, `areResultsDisplayed()` |
| Constants | `UPPER_SNAKE_CASE` → `MAX_RETRY_COUNT` |
| Locator fields | `camelCase`, descriptive of the element → `searchBox`, `manualFilterCheckBox` |
| Feature files | `kebab-case.feature`, named after the flow → `search-flow.feature` |
| Step definition files | `camelCase` + `Steps` suffix, one per feature area → `searchSteps.java`, `checkoutSteps.java` |
| Page Object files | `<PageName>Page.java` → `LandingPage.java`, `CartPage.java`, `CheckOutPage.java` |

### Gherkin step naming
- Steps should read as **plain English actions**, not implementation details:
  - ✅ `When I search for "mazda mx-5"`
  - ❌ `When I click searchBox and sendKeys "mazda mx-5" and click searchButton`
- Use **parameterized steps** (`{string}`, `{int}`) instead of duplicating near-identical steps for different values.

---

## 🧩 Design Pattern

This framework uses the **Page Object Model (POM)**, coordinated through a lightweight **Page Manager**:

- Each web page/screen has a corresponding `Page` class (e.g. `LandingPage`) containing:
  - `@FindBy` locators
  - Action methods (`searchForItem()`, `clickSearchBtn()`)
  - Validation methods (`isLandingPageLoaded()`, `areResultsDisplayed()`)
- `PageManager` is a simple factory that lazily creates and returns page objects, so step definitions never call `new LandingPage(driver)` directly:
  ```java
  pageManager.landingPage().searchForItem("mazda mx-5");
  ```
- `BaseTest` owns the `WebDriver` lifecycle (`launchApplication()` / `tearDown()`) and is extended by the Cucumber `searchSteps` class.
- **No `ThreadLocal`/singleton complexity** — this framework runs sequentially (no parallel execution), so `PageManager` and `driver` are plain instance fields, created fresh once per scenario via Cucumber's `@Before` hook.

```
searchSteps (extends BaseTest)
        │
        ├── @Before → launchApplication() → creates driver + pageManager
        │
        ├── pageManager.landingPage() → lazily builds LandingPage
        │
        └── @After → tearDown() → quits driver
```

---

## ✅ Prerequisites

- **Java 17+** installed (project currently targets Java 25)
- **Maven 3.8+**
- **Google Chrome** (or Firefox/Edge — configurable) installed locally
- An IDE (IntelliJ IDEA recommended)

---

## ⚙️ Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/ebayAutomationFramework.git
   cd ebayAutomationFramework
   ```

2. **Install dependencies**
   ```bash
   mvn clean install -DskipTests
   ```

3. **Configure the browser** (see [Configuration](#-configuration) below)

4. **Run a sanity check**
   ```bash
   mvn test
   ```

---

## ▶️ Running Tests

### Via Maven (CLI / CI pipelines)
```bash
mvn test
```

### Via IntelliJ
Right-click `runner/TestRunner.java` → **Run 'TestRunner'**

### Run a specific feature file
Update the `features` path in `@CucumberOptions` on `TestRunner.java`, or use tags (see below).

### Run by tag (recommended for larger suites)
Add tags to your `.feature` file:
```gherkin
@search @regression
Scenario Outline: Search for a product and filter by transmission
```
Then filter via Maven:
```bash
mvn test -Dcucumber.filter.tags="@search"
```

---

## 🔧 Configuration

Browser and environment settings are controlled via:
```
src/main/java/resources/GlobalData.properties
```

Example:
```properties
browser=chrome
```

Supported values: `chrome`, `firefox`, `edge`, or any of these with `headless` appended (e.g. `chromeheadless`).

**Override at runtime** without editing the file:
```bash
mvn test -Dbrowser=firefox
```

---

## 📊 Reporting

This framework uses **Cucumber's native HTML/JSON reporting**, configured in `TestRunner.java`:

```java
plugin = {
    "pretty",
    "html:target/cucumber.html",
    "json:target/cucumber.json"
}
```

### Viewing the report
After a run, open:
```
target/cucumber.html
```

- Pass/fail status per step
- Full stack traces on failure
- **Screenshots on failure**, embedded automatically via a Cucumber `@After` hook (collapsed under the **"N hooks"** section per scenario — click to expand)

> ⚠️ `target/` is regenerated on every run and typically excluded from version control. Copy `cucumber.html` elsewhere if you need to preserve a specific run's results.

---

## 🔁 Retry Mechanism

Flaky UI scenarios can be automatically retried using a TestNG `IRetryAnalyzer`, wired in globally via `IAnnotationTransformer` (no per-test annotation needed — applies to every Cucumber scenario automatically).

- Retry count is configurable in `testcomponents/RetryAnalyzer.java`:
  ```java
  private static final int MAX_RETRY_COUNT = 2; // total attempts = 3
  ```
- Each retry re-runs the **entire scenario** from `@Before` to `@After` (fresh browser session) — there is no mid-scenario resume.

> Note: if `RetryAnalyzer.java` isn't present yet in `testcomponents/`, add it before relying on this section — currently only `BaseTest.java` lives there.

---

## ✍️ Writing New Tests

1. **Add/extend a `.feature` file** under `src/test/resources/features/`, using plain-English Gherkin steps.
2. **Implement matching step definitions** in `cucumber/searchSteps.java` (or a new dedicated `<feature>Steps.java` class for a new feature area).
3. **Add or extend a Page Object** under `pages/` for any new page/screen involved.
4. **Register any new page** as a lazy getter in `managers/PageManager.java`.
5. Run the scenario and confirm it passes **and** fails correctly (comment out a step temporarily to sanity-check your assertions aren't silently passing).

### Example — adding a new page
```java
// managers/PageManager.java
public CartPage cartPage() {
    if (cartPage == null) {
        cartPage = new CartPage(driver);
    }
    return cartPage;
}
```

---

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/<short-description>`
2. Follow the [naming conventions](#-naming-conventions) above
3. Ensure `mvn test` passes locally before opening a PR
4. Keep step definitions thin — business logic and locators belong in Page Objects, not in step definition classes
5. Open a PR with a clear description of the scenario(s) added/changed

---

## 📄 License

Specify your license here (e.g. MIT, Apache 2.0), or remove this section if the repository is private/internal.
