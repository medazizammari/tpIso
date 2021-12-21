import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TestTodomvc {
    private WebDriver driver;
    private WebDriverWait webDriverWait;
    private Actions actions;


    private WebElement waitUntilFindElement(By locator) {
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }


    private WebElement getItemCheckbox(String itemName) {
        WebElement checkbox = waitUntilFindElement(By.xpath(String.format("//label[text()='%s']/preceding-sibling::input",itemName)));
        return checkbox;

    }

    @BeforeEach()
    public void setup() {
        WebDriverManager.firefoxdriver().setup();
        this.driver = new FirefoxDriver();
        this.webDriverWait = new WebDriverWait(driver, 3);
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(12));
        this.driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
        this.driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(12));
        actions = new Actions(driver);
    }



    @ParameterizedTest()
    @ValueSource(strings = {

            "AngularJS",
            "Vue.js",
            "React"
    })
    public void verifyTodoListCreatedSuccessfully_with_params(String technologyName) throws Exception {
        this.driver.navigate().to("https://todomvc.com/");

        this.openTechnologyApp(technologyName);
        this.addNewTodoItem("meet a friend");
        this.addNewTodoItem("buy meat");
        addNewTodoItem("clean the car");
        getItemCheckbox("clean the car").click();
        assertLeftItems(2);
    }


    private void openTechnologyApp(String technologyName) {
        WebElement technologyLink = waitUntilFindElement(By.linkText(technologyName));
        technologyLink.click();
    }


    private void addNewTodoItem(String itemName) {
        WebElement todoInput = waitUntilFindElement(By.xpath("//input[@placeholder='What needs to be done?']"));
        todoInput.sendKeys(itemName);
        actions.click(todoInput).sendKeys(Keys.ENTER).perform();
    }

    private void assertLeftItems(int numLeftItems) {

        WebElement footer = waitUntilFindElement(By.xpath("//footer/*/span | //footer/span"));
        boolean isAssertionTrue ;
        try {
            isAssertionTrue = webDriverWait.until(ExpectedConditions.textToBePresentInElement(footer, String.format("%d items left", numLeftItems)));
        }
        catch(TimeoutException e) {
            isAssertionTrue = false ;
        }
        Assertions.assertTrue(isAssertionTrue);
    }

    @AfterEach()
    public void afterTest() {
        driver.quit();
    }
}
