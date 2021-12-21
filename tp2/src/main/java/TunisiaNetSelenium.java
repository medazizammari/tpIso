
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.util.ArrayList;

public class TunisiaNetSelenium {

    private WebDriver driver;

    private void findElementAndClick(By selector) {
        WebElement element = driver.findElement(selector);
        element.click();
    }


    private void typeTextOnElement(By selector, String input) {
        WebElement element = this.driver.findElement(selector);
        element.sendKeys(input);
    }

    /**
     * Submit after typing text on element
     *
     */
    private void typeTextOnElement(By selector, String input, boolean submit) {
        WebElement element = this.driver.findElement(selector);
        element.sendKeys(input);
        if (submit)
            element.submit();
    }


    private void findNthOptionAndCheck(By optionSelector, By checkboxSelector, int n) throws NotFoundException {
        ArrayList<WebElement> elements = (ArrayList<WebElement>) this.driver.findElements(optionSelector);
        if (elements.size() < n)
            throw new NotFoundException("Element not found");
        WebElement element = elements.get(n - 1);
        element.findElement(checkboxSelector).click();
    }


    public void initialise() {
        WebDriverManager.firefoxdriver().setup();
        this.driver = new FirefoxDriver();
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(12));
        this.driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
        this.driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(12));
    }


    private void login(String email, String password) {
        try {
            Thread.sleep(2000);
            this.findElementAndClick(By.cssSelector(".user-info"));
            this.findElementAndClick(By.cssSelector("a[title='Identifiez-vous']"));
            this.typeTextOnElement(By.name("email"), email);
            this.typeTextOnElement(By.name("password"), password);
            this.findElementAndClick(By.id("submit-login"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void searchArticle(String articleName) {
        this.typeTextOnElement(By.id("search_query_top"), articleName, true);
    }

    private void beginCheckout() {
        this.findElementAndClick(By.cssSelector(".product-title a"));
        this.findElementAndClick(By.cssSelector(".btn.btn-primary.add-to-cart"));
        this.findElementAndClick(By.linkText("Commander"));
        this.findElementAndClick(By.cssSelector(".checkout.cart-detailed-actions.card-block"));
    }


    private void fillAddresses(String company, int vat, String address1, String address2, String postalCode, String city, int phone) {
        try {
            this.typeTextOnElement(By.name("company"), company);
            this.typeTextOnElement(By.name("vat_number"), String.valueOf(vat));
            this.typeTextOnElement(By.name("address1"), address1);
            this.typeTextOnElement(By.name("address2"), address2);
            this.typeTextOnElement(By.name("postcode"), postalCode);
            this.typeTextOnElement(By.name("city"), city);
            this.typeTextOnElement(By.name("phone"), String.valueOf(phone));
        } catch (NoSuchElementException e) {
            System.out.println("Address already exists, continuing...");
        }
        this.findElementAndClick(By.name("confirm-addresses")); // continue
    }

    private void fillDelivery(int deliveryOption, String message) {
        try {
            this.findNthOptionAndCheck(By.cssSelector(".row.delivery-option"), By.tagName("input"), deliveryOption);
            if (message != null) {
                this.typeTextOnElement(By.id("delivery_message"), message);
            }
            this.findElementAndClick(By.name("confirmDeliveryOption"));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void fillPayment(int paymentMethod) {
        try {
            this.findNthOptionAndCheck(By.cssSelector(".payment-option"), By.tagName("input"), paymentMethod); // Selecting payment method
            this.findElementAndClick(By.cssSelector("#conditions-to-approve input")); // Accepting conditions
            this.findElementAndClick(By.cssSelector("#payment-confirmation button")); // Finishing checkout
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void shutDriver() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.driver.quit();
    }


    public void run() {
        this.initialise();
        this.driver.get("https://www.tunisianet.com.tn/");
        this.login("achourali@test.tn", "achouralitest");
        this.searchArticle("PC portable MacBook M1 13.3");
        this.beginCheckout();
        this.fillAddresses("foo", 0, "foo", "foo", "1150", "foo", 99999999);
        this.fillDelivery(2, "foo");
        this.fillPayment(1);
        this.shutDriver();
    }

    public static void main(String[] args) {

        TunisiaNetSelenium tunisiaNetSelenium = new TunisiaNetSelenium();
        tunisiaNetSelenium.run();
    }
}
