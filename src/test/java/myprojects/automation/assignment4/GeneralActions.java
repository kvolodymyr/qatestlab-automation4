package myprojects.automation.assignment4;


import myprojects.automation.assignment4.model.ProductData;
import myprojects.automation.assignment4.utils.Properties;
import myprojects.automation.assignment4.utils.logging.CustomReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private WebDriverWait wait;

    public GeneralActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 15);
    }

    /**
     * Logs in to Admin Panel.
     * @param login
     * @param password
     */
    public void login(String login, String password) {
        CustomReporter.log("Login as user - " + login);
        driver.navigate().to(Properties.getBaseAdminUrl());

        driver
            .findElement(By.id("email"))
            .sendKeys(login);

        driver
            .findElement(By.id("passwd"))
            .sendKeys(password);

        driver
            .findElement(By.name("submitLogin"))
            .click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("main")));
    }

    /**
     * Implement product creation scenario.
     * @param product
     */
    public void createProduct(ProductData product) {
        CustomReporter.log(String.format("Product %s, Price: %s, QTY: %s, Weight: %s", product.getName(), product.getPrice(), product.getQty(), product.getWeight()));

        WebElement element;

        element = waitForContentLoad(By.id("form_step1_name_1"));
        element.sendKeys(product.getName());

        element = waitForContentLoad(By.id("form_step6_reference"));
        element.sendKeys(product.getKey() );

        // Q: how to clear the input field?
        // element.clear() - exception java.lang.NullPointerException
        // Additional info if this solution doesn't work for someone: make sure that
        // element.GetAttribute("value") really has a value before calling element.clear()
        // (wait for this value to be non empty). It sometimes happens when testing AngularJS
        // inputs with ngModel directive.
        // TechSupport: Проверили Ваш код - работает без исключений. Проверяли таким образом:
        // WebElement element = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("form_step1_qty_0_shortcut")));
        // element.clear();
        // element.sendKeys(Integer.toString(product.getQty()));
        element = waitForContentLoad(By.id("form_step1_qty_0_shortcut"));
        element.sendKeys(Integer.toString(product.getQty()));
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Integer.toString(product.getQty()));

        element = waitForContentLoad(By.id("form_step1_price_shortcut"));
        scrollTo(element);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), product.getPrice());

        element = driver.findElement(By.id("add_feature_button"));
        scrollTo(element);
        element.click();

        Select slElement = new Select(waitForContentLoad(By.id("form_step1_features_0_feature")));
        slElement.selectByValue("4");

        element = waitForContentLoad(By.id("form_step1_features_0_custom_value_1"));
        element.sendKeys(product.getWeight());

        element = driver.findElement(By.className("switch-input"));
        element.click();
        wait.until(ExpectedConditions.textToBe(By.className("growl-message"), "Настройки обновлены."));
        driver.findElement(By.cssSelector(".growl-close")).click();

        // Save with go to to catalog and check the Success Message and return to the catalog
        element = waitForClickable(By.id("submit")); // product_form_save_go_to_catalog_btn
        element.click();
        // element = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".growl-message")));
        // Assert.assertEquals(element.getText(), "Настройки обновлены.");
        wait.until(ExpectedConditions.textToBe(By.className("growl-message"), "Настройки обновлены."));
        driver.findElement(By.cssSelector(".growl-close")).click();

        element = waitForClickable(By.id("product_form_save_go_to_catalog_btn"));
        element.click();

        waitForContentLoad(By.id("page-header-desc-configuration-add"));
    }

    // all-product-link
    /**
     * Check the product
     * @param product
     */
    public void checkProduct(ProductData product) {
        WebElement element;

        driver.findElement(By.cssSelector(".all-product-link")).click();

        element = waitForContentLoad(By.linkText(product.getName()));
        scrollTo(element);
        element.click();

        element = waitForContentLoad(By.cssSelector(".breadcrumb li:last-child span[itemprop='name']"));
        CustomReporter.log("check the breadcrumb name - " + element.getText());
        Assert.assertEquals(element.getText(), product.getName());

        element = waitForContentLoad(By.cssSelector("h1[itemprop='name']"));
        CustomReporter.log("check the name - " + element.getText());
        Assert.assertEquals(element.getText(), product.getName().toUpperCase());

        element = waitForContentLoad(By.cssSelector(".current-price span"));
        CustomReporter.log("check the price - " + element.getAttribute("content"));
        // localization issue
        String price = product.getPrice();
        if(element.getAttribute("content").indexOf(",") == -1) {
            price = price.replace(',', '.');
        }
        Assert.assertEquals(element.getAttribute("content"), price);

        element = waitForContentLoad(By.cssSelector(".product-quantities span"));
        CustomReporter.log("check the QTY - " + element.getText());
        Assert.assertEquals(element.getText(), product.getQty() + " Товары");

        element = waitForContentLoad(By.cssSelector("#product-details > section > dl > dt"));
        CustomReporter.log("check the weight label - " + element.getText());
        Assert.assertEquals(element.getText(), "Weight");

        element = waitForContentLoad(By.cssSelector("#product-details > section > dl > dd"));
        CustomReporter.log("check the weight - " + element.getText());
        Assert.assertEquals(element.getText(), product.getWeight());
    }

    /**
     * Check the product
     * @param product
     */
    public void checkProductInAdmin(ProductData product) {
        WebElement element;

        goToCatalogGoods();

        element = driver.findElement(By.name("products_filter_reset"));
        if(element.isDisplayed()) { element.click(); }

        element = waitForContentLoad(By.name("filter_column_name"));
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), product.getName());

        // activate button.products_filter_submit - ((JavascriptExecutor)webDriver).executeScript("window.focus();");, // driver.findElement(By.cssSelector("table.product")).click(); // activate button.products_filter_submit
        driver.findElement(By.name("filter_column_reference")).sendKeys("");
        element = waitForClickable(By.name("products_filter_submit"));
        element.click();

        //  Error setting arguments for script (WARNING: The server did not provide any stacktrace information)
        element = waitForClickable(By.linkText(product.getName()));
        scrollTo(element);
        element.click();

        element = waitForContentLoad(By.id("form_step1_name_1"));
        CustomReporter.log("check the name - " + element.getAttribute("value"));
        Assert.assertEquals(element.getAttribute("value"), product.getName());

        element = driver.findElement(By.id("form_step6_reference"));
        CustomReporter.log("check the key - " + element.getAttribute("value"));
        Assert.assertEquals(element.getAttribute("value"), product.getKey());

        element = driver.findElement(By.id("form_step1_qty_0_shortcut"));
        scrollTo(element);
        CustomReporter.log("check the qty - " + element.getAttribute("value"));
        Assert.assertEquals(element.getAttribute("value"), Integer.toString(product.getQty()));

        element = driver.findElement(By.id("form_step1_price_shortcut"));
        scrollTo(element);
        CustomReporter.log("check the price - " + element.getAttribute("value"));
        Assert.assertEquals(element.getAttribute("value").indexOf(product.getPrice()), 0);

        element = driver.findElement(By.id("form_step1_features_0_custom_value_1"));
        scrollTo(element);
        CustomReporter.log("check the weight - " + element.getAttribute("value"));
        Assert.assertEquals(element.getAttribute("value"), product.getWeight());

    }

    /**
     * Open the shop
     */
    public void open() {
        driver.navigate().to(myprojects.automation.assignment4.utils.Properties.getBaseUrl());
        waitForContentLoad(By.id("main"));
    }

    /**
     * Open the Admin-panel Base Page
     */
    public void openAdmin() {
        driver.navigate().to(myprojects.automation.assignment4.utils.Properties.getBaseAdminUrl());
        waitForContentLoad(By.id("main"));
    }

    /**
     * Navigate to Catalog -> Goods page (this page opens by default)
     */
    public void goToCatalogGoods() {
        driver.findElement(By.id("subtab-AdminCatalog")).click();
    }

    /**
     * Open the Create New Product Form
     */
    public void goToCreateNewProduct() {
        WebElement element;

        goToCatalogGoods();

        waitForContentLoad(By.id("page-header-desc-configuration-add")).click();
    }

    /**
     * Wait until page loader disappears from the page
     */
    public WebElement waitForContentLoad(By locator) {
        // return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        // implement generic method to wait until page content is loaded
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait until element is clickable
     */
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Scroll Visibility Scope of the Page To Element
     * @param element
     */
    public void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
