package TestComponents;

import Managers.PageManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {

    protected WebDriver driver;
    public PageManager pageManager;

    private WebDriver initializeDriver() throws IOException {

        //Load Properties File and Extract browser Name
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"//src//main//java//Resources//GlobalData.properties");
        properties.load(fis);

        String browser = System.getProperty("browser") != null ? System.getProperty("browser") : properties.getProperty("browser");

        if (browser.contains("chrome") ){
            System.out.println("Launching Chrome browser...");
            System.out.println("Browser from properties file = " + browser);
            WebDriverManager.chromedriver().setup();
            //Set Chrome Options if needed
            ChromeOptions options = new ChromeOptions();

            if(browser.contains("headless")){
                options.addArguments("headless");
            }

            driver = new ChromeDriver(options);

        }

        else if (browser.contains("firefox")) {
            System.out.println("Launching Firefox browser...");
            System.out.println("Browser from properties file = " + browser);
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            driver = new FirefoxDriver(options);
        }

        else if (browser.contains("edge")){
            System.out.println("Launching Edge browser...");
            System.out.println("Browser from properties file = " + browser);
            WebDriverManager.edgedriver().setup();

            EdgeOptions options = new EdgeOptions();
            options.addArguments("--remote-allow-origins=*");

            driver = new EdgeDriver(options);
        }

        else {
            throw new RuntimeException("❌ Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        return driver;

    }

    public String getScreenShot(String testCaseName, WebDriver driver) throws IOException {

        String timestamp = String.valueOf(System.currentTimeMillis());

        // Ensure screenshots directory exists
        File screenshotsDir = new File(
                System.getProperty("user.dir") + "/reports/screenshots"
        );
        if (!screenshotsDir.exists()) {
            boolean result  = screenshotsDir.mkdirs();
        }

        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        String fileName = testCaseName + "_" + timestamp + ".png";
        File destinationFile = new File(screenshotsDir, fileName);

        FileUtils.copyFile(source, destinationFile);

        return "screenshots/" + fileName;
    }

    @BeforeMethod (alwaysRun = true)
    protected void launchApplication() throws IOException {
        driver = initializeDriver();
        pageManager = new PageManager(driver);
    }

    @AfterMethod (alwaysRun = true)
    protected void tearDown() {
        if (driver != null) {
            driver.quit();  // closes browser + ends WebDriver session
            System.out.println("Driver closed successfully!");
        }
    }
}
