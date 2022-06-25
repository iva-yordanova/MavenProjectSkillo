package SeleniumTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;
import java.util.List;


public class HerokuAppTests {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor js;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        //implicit wait - wait certain time before throwing no such element
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        //explicit wait - add custom conditions and should be set
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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

    /*@Test
    public void dynamicContent(){
        driver.get("https://the-internet.herokuapp.com/dynamic_content");

        List<WebElement> pList = driver.findElements(By.xpath("//div[@class='large-10 columns']"));
        for (WebElement text:pList){
            text.getText();
            System.out.println(text);
        }
        driver.navigate().refresh();
save the elements, click here, assert that the first two are static, assert that the third is dynamic

    }*/

    @Test
    public void floatingMenu() {
        driver.get("https://the-internet.herokuapp.com/floating_menu");
        //WebElement floatingMenuContainer  = driver.findElement(By.xpath("//div[@id='menu']"));

        //asset floating elements is there when opening the page
        WebElement homeButtonMenu = driver.findElement(By.xpath("//*[@id='menu']//a[text()='Home']"));
        Assert.assertTrue(homeButtonMenu.isDisplayed());

        //scroll the page down
        js.executeScript("window.scrollBy(0,-2000)");
        Assert.assertTrue(homeButtonMenu.isDisplayed());

        //scroll the page up
        js.executeScript("window.scrollBy(0,1000)");
        //Assert.assertTrue(homeButtonMenu.isDisplayed());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='menu']//a[text()='Home']")));
        Assert.assertTrue(homeButtonMenu.isDisplayed());

        //click on the button
        js.executeScript("arguments[0].click()", homeButtonMenu);
        Assert.assertEquals(driver.getCurrentUrl(), "https://the-internet.herokuapp.com/floating_menu#home");
    }

    @Test
    public void dynamicControls() {
        driver.get("https://the-internet.herokuapp.com/dynamic_controls");

        //assert the dynamic checkbox is present after loading the page
        WebElement checkBox = driver.findElement(By.id("checkbox"));
        Assert.assertTrue(checkBox.isDisplayed());

        //click the remove button
        WebElement removeButton = driver.findElement(By.xpath("//button[text()='Remove']"));
        removeButton.click();

        //wait until the animation for the removing the checkbox is gone
        WebElement loadingAnimation = driver.findElement(By.xpath("//div[@id='loading']"));
        wait.until(ExpectedConditions.invisibilityOf(loadingAnimation));

        //Assert that the checkbox is not displayed
        Assert.assertFalse(checkBox.isDisplayed());
        wait.until(ExpectedConditions.invisibilityOf(checkBox));
        Assert.assertEquals(driver.findElement(By.id("message")).getText(), "It's gone!");

        //fluent wait example
        /*Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(38))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchFieldException.class);*/
    }

    @Test
    public void dynamicLoading() {
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");

        //not good practice. We should use webelement
        By startButton = By.xpath("//div[@id='start']/button");
        By helloWorldText = By.xpath("//div[@id='finish']");

        WebElement startButtonWebElement = driver.findElement(startButton);
        startButtonWebElement.click();

        WebElement helloWorldTextWebElement = driver.findElement(helloWorldText);


    }

    @Test
    public void iFrames() {
        driver.get("https://the-internet.herokuapp.com/iframe");

        //step into the frame in which the web element is located
        driver.switchTo().frame("mce_0_ifr");
        //now we can find and save the element
        WebElement textElement = driver.findElement(By.xpath("//*[@id='tinymce']//p"));
        textElement.clear();
        textElement.sendKeys("some text");

        //switch back to the main document or first frame
        driver.switchTo().defaultContent();
        //now we can find and save the element
        WebElement headerText = driver.findElement(By.xpath("//div[@class='example']/h3"));
    }

    @Test
    public void nestedFrames() {
        driver.get("https://the-internet.herokuapp.com/nested_frames");

        driver.switchTo().frame("frame-top").switchTo().frame("frame-left");
        WebElement leftFrameBodyText = driver.findElement(By.xpath("//body"));
        Assert.assertEquals(leftFrameBodyText.getText(), "LEFT");

        //We can use defaultContent or parentFrame. When we use defaultContent we should switch to top and then to middle
        /*driver.switchTo().defaultContent();
        driver.switchTo().frame("frame-top").switchTo().frame("frame-middle");*/

        //Switch to parent frame and then to the middle
        driver.switchTo().parentFrame().switchTo().frame("frame-middle");
        WebElement middleFrameBodyText = driver.findElement(By.xpath("//body"));
        Assert.assertEquals(middleFrameBodyText.getText(), "MIDDLE");

    }
}
