package myprojects.automation.assignment4.tests;

import com.google.common.base.Function;
import myprojects.automation.assignment4.BaseTest;
import myprojects.automation.assignment4.model.ProductData;
import myprojects.automation.assignment4.utils.logging.CustomReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Random;

public class CreateProductTest extends BaseTest {
    private ProductData product = ProductData.generate();
    @DataProvider(name = "Authentication")
    public static Object[][] credentials() {
        return new Object[][] { { "webinar.test@gmail.com", "Xcg7299bnSmMuRLp9ITw" }};
    }


    // implement test for product creation
    @Test(dataProvider = "Authentication")
    public void createNewProduct(String login, String password) {
        actions.login(login, password);

        actions.goToCreateNewProduct();

        actions.createProduct(product);
    }

    // implement logic to check product visibility on website
    @Test(dependsOnMethods = "createNewProduct")
    public void checkProduct() {
        actions.open();

        actions.checkProduct(product);
    }
}
