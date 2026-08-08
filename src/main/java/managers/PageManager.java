package managers;

import org.openqa.selenium.WebDriver;
import pages.LandingPage;

public class PageManager {

    private final WebDriver driver;

    // Lazily-initialized page objects
    private LandingPage landingPage;

    public PageManager(WebDriver driver) {
        this.driver = driver;
    }

    // ---- Page getters: created once, reused thereafter ----

    public LandingPage landingPage() {
        if (landingPage == null) {
            landingPage = new LandingPage(driver);
        }
        return landingPage;
    }

}


