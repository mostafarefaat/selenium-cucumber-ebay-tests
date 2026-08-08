package Pages;

import Absract.Abstract;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class LandingPage extends Abstract {

    WebDriver driver;

    public LandingPage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    // Locators via PageFactory
    @FindBy(css = "input#gh-ac, input[placeholder='Search for anything']")
    private WebElement searchBox;

    @FindBy(css = "a.gh-la, [class*='logo']")
    private WebElement ebayLogo;

    @FindBy(css = "input#gh-btn, button[type='submit']")
    private WebElement searchButton;

    @FindBy(xpath = "//*[@id='srp-river-results' " +
            "and contains(concat(' ', normalize-space(@class), ' '), ' srp-river-results ') " +
            "and contains(concat(' ', normalize-space(@class), ' '), ' clearfix ')]" +
            "//a[contains(@class,'s-card__link') ]//div[@role='heading']")
    private List<WebElement> searchResults;

    @FindBy(xpath = "//*[@id='srp-river-results' " +
            "and contains(concat(' ', normalize-space(@class), ' '), ' srp-river-results ') " +
            "and contains(concat(' ', normalize-space(@class), ' '), ' clearfix ')]" +
            "//div[contains(@class,'s-card__subtitle')]//span[contains(text(),'Manual')]")
    private List<WebElement> manualSubtitleSpans;

    @FindBy(xpath = "//*[@id='srp-river-results' " +
            "and contains(concat(' ', normalize-space(@class), ' '), ' srp-river-results ') " +
            "and contains(concat(' ', normalize-space(@class), ' '), ' clearfix ')]" +
            "//div[contains(@class,'s-card__subtitle')]//span[contains(text(),'Automatic')]")
    private List<WebElement> automaticSubtitleSpans;

    @FindBy(xpath = "//span[contains(@class,'cbx x-refine__multi-select-cbx') and contains(text(),'Manual')]")
    private WebElement manualFilterCheckBox;

    @FindBy(xpath = "//span[contains(@class,'cbx x-refine__multi-select-cbx') and contains(text(),'Automatic')]")
    private WebElement automaticFilterCheckBox;


    public void goTo(){
        driver.get("https://www.ebay.com/");
    }

    // ---- Individual validation checks ----

    public boolean isUrlCorrect() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.titleContains("eBay"));

        return Objects.requireNonNull(driver.getCurrentUrl()).contains("ebay.com");
    }

    public boolean isTitleCorrect() {
        String title = driver.getTitle();
        return title != null && title.toLowerCase().contains("ebay");
    }

    public boolean isSearchBoxDisplayed() {
        try {
            waitForElementToAppear(searchBox, Duration.ofSeconds(10));
            return searchBox.isDisplayed();

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLogoDisplayed() {
        try {
            waitForElementToAppear(ebayLogo, Duration.ofSeconds(10));
            return ebayLogo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ---- Combined single-call validation ----

    public boolean isLandingPageLoaded() {
        return isUrlCorrect() && isTitleCorrect() && isSearchBoxDisplayed() && isLogoDisplayed();
    }

    public void searchForItem(String product) {
        searchBox.click();
        searchBox.sendKeys(product);
    }

    public void clickSearchBtn() {
        searchButton.click();
    }

    public boolean areResultsDisplayed() {
        try {
            waitForElementToAppear(searchResults.get(1), Duration.ofSeconds(20));
            return searchResults.get(1).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getResultsCount() {
        return searchResults.size();
    }

    public void filterByTransmission(String type) {
        if (Objects.equals(type.toLowerCase(), "manual")) {
            manualFilterCheckBox.click();
        } else {
            automaticFilterCheckBox.click();
        }
    }

    public boolean isTransmissionFilterApplied(String transmission) {

        int totalCards =  getResultsCount();
        int filteredCount = transmission.equalsIgnoreCase("manual") ?
                manualSubtitleSpans.size(): automaticSubtitleSpans.size();
        System.out.println(filteredCount + " of " + totalCards + " results show 'Manual'");

        return totalCards > 0 && filteredCount == totalCards;

    }

}
