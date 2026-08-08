package cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import testcomponents.BaseTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;

public class searchSteps extends BaseTest {

    @Before
    public void setUp() throws Exception {
        launchApplication();   // inherited from BaseTest — starts driver, creates pageManager
    }

    @After
    public void cleanUp(Scenario scenario) {
        if (scenario.isFailed() && driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
        }
        tearDown();             // inherited from BaseTest — quits driver
    }

    @Given("I navigate to the eBay home page")
    public void i_navigate_to_the_ebay_home_page() {
        pageManager.landingPage().goTo();
    }

    @Then("I should land on the eBay main page")
    public void i_should_land_on_the_ebay_main_page() {
        Assert.assertTrue(pageManager.landingPage().isLandingPageLoaded(),
                "Ebay page did not load as expected");
    }

    @When("I search for {string}")
    public void i_search_for(String searchTerm) {
        pageManager.landingPage().searchForItem(searchTerm);
        pageManager.landingPage().clickSearchBtn();
    }

    @Then("search results should be displayed")
    public void search_results_should_be_displayed() {
        Assert.assertTrue(pageManager.landingPage().areResultsDisplayed(),
                "No search results were displayed");
    }

    @Then("I log the number of search results")
    public void i_log_the_number_of_search_results() {
        int count = pageManager.landingPage().getResultsCount();
        System.out.println("Number of search results found: " + count);
        Assert.assertTrue(count > 0, "Expected results count to be greater than 0");
    }

    @When("I filter results by Transmission {string}")
    public void i_filter_results_by_transmission(String transmission) {
        pageManager.landingPage().filterByTransmission(transmission);
    }

    @Then("the filtered results should reflect the {string} transmission")
    public void the_filtered_results_should_reflect_the_transmission(String transmission) {
        Assert.assertTrue(pageManager.landingPage().isTransmissionFilterApplied(transmission),
                "Transmission filter '" + transmission + "' was not applied correctly");
    }
}