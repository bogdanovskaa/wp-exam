package mk.ukim.finki.wp.exam.example.selenium;

import lombok.Getter;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

@Getter
public class AbstractPage {

    protected WebDriver driver;

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
    }

    static void get(WebDriver driver, String relativeUrl) {
        String url = System.getProperty("geb.build.baseUrl", "http://localhost:9999") + relativeUrl;
        driver.get(url);
    }

    static void assertRelativeUrl(WebDriver driver, String relativeUrl) {
        String url = System.getProperty("geb.build.baseUrl", "http://localhost:9999") + relativeUrl;
        String current = driver.getCurrentUrl();
        Assert.assertEquals("Current url is not " + relativeUrl, url, current);
    }

}
