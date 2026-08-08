package Absract;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Abstract {

    WebDriver driver;

    public Abstract(WebDriver driver) {

        this.driver = driver;
    }

    @FindBy(css = "[routerlink*='cart']")
    WebElement cartButton;

    public void waitForElementToAppear(WebElement ele, Duration sec){
        WebDriverWait wait = new WebDriverWait(driver, sec);
        wait.until(ExpectedConditions.visibilityOf(ele));
    }

    public void waitForElementToDisappear(WebElement ele, Duration sec){
        WebDriverWait wait = new WebDriverWait(driver, sec);
        wait.until(ExpectedConditions.visibilityOf(ele));
    }
}
