package SeleniumTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class HerokuAppTests {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        actions = new Actions(driver);

    }

    @AfterMethod
    public void cleanUp() {
        //driver.close();
        driver.quit();
    }

    @Test
    public void addRemoveElements() {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");

        List<WebElement> elementsContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertTrue(elementsContainer.isEmpty());

        WebElement addElementsButton = driver.findElement(By.xpath("//button[@onclick='addElement()']"));
        for (int i = 0; i < 10; i++) {
            addElementsButton.click();
        }

        elementsContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertEquals(elementsContainer.size(), 10);

        for (WebElement element : elementsContainer) {
            element.click();
        }

        elementsContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertTrue(elementsContainer.isEmpty());
    }

    @Test
    public void basicAuth() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth");
        Thread.sleep(1500);

        WebElement text = driver.findElement(By.xpath("//div[@class='example']/p"));
        Assert.assertEquals(text.getText(), "Congratulations! You must have the proper credentials.");
    }

    @Test
    public void dragAndDrop() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/drag_and_drop");
        actions = new Actions(driver);

        WebElement elementA = driver.findElement(By.xpath("//div[@id='column-a']"));
        WebElement elementB = driver.findElement(By.xpath("//div[@id='column-b']"));
        WebElement elementAHeader = driver.findElement(By.xpath("//div[@id='column-a']/header"));
        WebElement elementBHeader = driver.findElement(By.xpath("//div[@id='column-b']/header"));

        //custom drag and drop
        //actions.moveToElement(elementA).clickAndHold(elementA).moveToElement(elementB).release(elementB).build().perform();

        actions.dragAndDrop(elementA, elementB).perform();

        //don't work as expected
        Assert.assertEquals(elementAHeader.getText(), "B");
        Assert.assertEquals(elementBHeader.getText(), "A");
    }

    @Test
    public void contextMenu() {
        driver.get("https://the-internet.herokuapp.com/context_menu");
        WebElement contextBox = driver.findElement(By.id("hot-spot"));
        actions.contextClick(contextBox).perform();
        Alert alert = driver.switchTo().alert();

        String alertText = alert.getText();
        Assert.assertEquals(alertText, "You selected a context menu");
        alert.dismiss();
    }

    @Test
    public void checkBoxes() {
        driver.get("https://the-internet.herokuapp.com/checkboxes");
        WebElement checkBox1 = driver.findElement(By.xpath("//form[@id='checkboxes']/input[1]"));
        WebElement checkBox2 = driver.findElement(By.xpath("//form[@id='checkboxes']/input[2]"));

        boolean checkBox1State = checkBox1.isSelected();
        boolean checkBox2State = checkBox2.isSelected();

        if (checkBox1State) {
            checkBox1.click();
        }
        Assert.assertEquals(checkBox1State, checkBox1.isSelected());

        /*if (checkBox1.isSelected())
        {
            checkBox1.click();
            Assert.assertTrue(!checkBox1.isSelected());
        }
        else
        {
            checkBox1.click();
            Assert.assertTrue(checkBox1.isSelected());
        }

        if (checkBox2.isSelected())
        {
            checkBox2.click();
            Assert.assertTrue(!checkBox2.isSelected());
        }
        else
        {
            checkBox2.click();
            Assert.assertTrue(checkBox2.isSelected());
        }*/
    }

    @Test
    public void redirectLink() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/redirector");
        WebElement actionLink = driver.findElement(By.xpath("//a[@id='redirect']"));
        actionLink.click();
        Thread.sleep(2000);
        Assert.assertEquals(driver.getCurrentUrl(), "https://the-internet.herokuapp.com/status_codes");
    }

    @Test
    public void multipleWindows() {
        driver.get("https://the-internet.herokuapp.com/windows");
        Assert.assertTrue(driver.getWindowHandles().size() == 1);

        WebElement clickHereActionLink = driver.findElement(By.xpath("//div[@class='example']//a"));
        clickHereActionLink.click();

        Assert.assertTrue(driver.getWindowHandles().size() == 2);

        for (String win : driver.getWindowHandles()) {
            driver.switchTo().window(win);
        }

        Assert.assertEquals(driver.getCurrentUrl(), "https://the-internet.herokuapp.com/windows/new");
    }

    @Test
    public void hover() {
        driver.get("https://the-internet.herokuapp.com/hovers");

        WebElement userName;
        List<WebElement> elementList = driver.findElements(By.xpath("//div[@class='figure']"));
        int i = 1;
        for (WebElement element : elementList) {
            actions.moveToElement(element).perform();
            userName = element.findElement(By.xpath(".//h5"));
            Assert.assertEquals(userName.getText(), "name: user" + i);
            i++;
        }

        WebElement element1Hover = driver.findElement(By.xpath("//div[@class='figure'][1]"));
        actions.moveToElement(element1Hover).perform();
        WebElement viewProfileButton = driver.findElement(By.xpath("//div[@class='figure'][1]//a"));
        viewProfileButton.click();
        Assert.assertEquals(driver.getCurrentUrl(), "https://the-internet.herokuapp.com/users/1");
    }



}

