package SeleniumTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;


public class FirstTest {

    ChromeDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        //1st approach: Set chrome driver as a system property
        //System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");

        // 2nd approach: Use the WebDriverManager library
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(); //create new driver object



        //Headless implementation - complete it
        /*ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu");*/

        //Implicit Wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

        //Explicit Wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //Thread.sleep(5000);//bad practice for wait
        driver.manage().window().maximize();//manage the window - maximize it
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(25));
    }

    @Test
    public void loginTest() {

        driver.get("http://training.skillo-bg.com/"); //getting the url

        //WebElement loginButton = driver.findElement(By.id("nav-link-login"));
        WebElement loginButton = driver.findElement(By.xpath("//a[@id='nav-link-login']")); //better approach to find an element
        loginButton.click();

        WebElement usernameOrEmailField = driver.findElement(By.xpath("//input[@id='defaultLoginFormUsername']"));
        usernameOrEmailField.sendKeys("test567");

        WebElement passwordField = driver.findElement(By.xpath("//input[@id='defaultLoginFormPassword']"));
        passwordField.sendKeys("test567");

        WebElement signInButton = driver.findElement(By.xpath("//button[@id='sign-in-button']"));
        signInButton.click();
    }

    @Test
    public void dropDownTest() {
        driver.get("https://www.mobile.bg/pcgi/mobile.cgi");
        WebElement cookieConsentButton = driver.findElement(By.xpath("//div[@class='fc-dialog-container']//button[@class='fc-button fc-cta-consent fc-primary-button']//p[@class='fc-button-label']"));
        cookieConsentButton.click();
        Select dropDownMark = new Select(driver.findElement(By.xpath("//select[@name='marka']")));
        dropDownMark.selectByVisibleText("Mitsubishi");

        Select dropDownModel = new Select(driver.findElement(By.xpath("//select[@name='model']")));
        dropDownModel.selectByVisibleText("Lancer");

        WebElement searchButton = driver.findElement(By.xpath("//input[@id='button2']"));
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        //searchButton.click();

        WebElement resultContainer= driver.findElement(By.xpath("//table[@class='tablereset'][2]"));
        Assert.assertTrue(resultContainer.isDisplayed());
    }

    @AfterMethod

    public void tearDown() {
        driver.quit();
    }


}
