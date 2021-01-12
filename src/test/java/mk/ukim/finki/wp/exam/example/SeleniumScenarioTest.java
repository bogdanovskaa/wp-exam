package mk.ukim.finki.wp.exam.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import mk.ukim.finki.wp.exam.example.model.Category;
import mk.ukim.finki.wp.exam.example.model.Product;
import mk.ukim.finki.wp.exam.example.model.Role;
import mk.ukim.finki.wp.exam.example.selenium.AbstractPage;
import mk.ukim.finki.wp.exam.example.selenium.AddOrEditProduct;
import mk.ukim.finki.wp.exam.example.selenium.ItemsPage;
import mk.ukim.finki.wp.exam.example.selenium.LoginPage;
import mk.ukim.finki.wp.exam.example.service.CategoryService;
import mk.ukim.finki.wp.exam.example.service.ProductService;
import mk.ukim.finki.wp.exam.example.service.UserService;
import mk.ukim.finki.wp.exam.util.CodeExtractor;
import mk.ukim.finki.wp.exam.util.ExamAssert;
import mk.ukim.finki.wp.exam.util.SubmissionHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SeleniumScenarioTest {

    static {
        SubmissionHelper.exam = "wp-kol-test";
        SubmissionHelper.index = "TODO";
    }

    @Autowired
    UserService userService;


    @Autowired
    CategoryService categoryService;


    @Autowired
    ProductService productService;


    @Test
    public void test1_list() {
        SubmissionHelper.startTest("test-list-20");
        List<Product> products = this.productService.listAllProducts();
        int itemNum = products.size();

        ItemsPage productsPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        productsPage.assertItems(itemNum);

        SubmissionHelper.endTest();
    }

    @Test
    public void test2_filter() {
        SubmissionHelper.startTest("test-filter-20");
        ExamAssert.assertEquals("without filter", 10, this.productService.listProductsByNameAndCategory(null, null).size());
        ExamAssert.assertEquals("by name only", 2, this.productService.listProductsByNameAndCategory("uct 1", null).size());
        ExamAssert.assertEquals("by category only", 10, this.productService.listProductsByNameAndCategory(null, 1L).size());
        ExamAssert.assertEquals("by name and category", 2, this.productService.listProductsByNameAndCategory("uct 1", 1L).size());
        SubmissionHelper.endTest();
    }

    @Test
    public void test3_create() {
        SubmissionHelper.startTest("test-create-20");
        List<Category> categories = this.categoryService.listAll();
        List<Product> products = this.productService.listAllProducts();

        int itemNum = products.size();

        String[] productCategories = new String[]{
                categories.get(0).getId().toString(),
                categories.get(categories.size() - 1).getId().toString()
        };

        ItemsPage productsPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            productsPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
        }
        productsPage = AddOrEditProduct.add(this.driver, PRODUCTS_ADD_URL, "test", "100", "5", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertItems(itemNum + 1);

        SubmissionHelper.endTest();
    }

    @Test
    public void test4_edit() {
        SubmissionHelper.startTest("test-edit-20");
        List<Category> categories = this.categoryService.listAll();
        List<Product> products = this.productService.listAllProducts();

        int itemNum = products.size();

        String[] productCategories = new String[]{
                categories.get(0).getId().toString(),
                categories.get(categories.size() - 1).getId().toString()
        };

        ItemsPage productsPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            productsPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
            productsPage = ItemsPage.to(this.driver);
        }
        productsPage = AddOrEditProduct.update(this.driver, productsPage.getEditButtons().get(itemNum - 1), "test1", "200", "4", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        ExamAssert.assertEquals("The updated product name is not as expected.", "test1", productsPage.getProductRows().get(itemNum).findElements(By.tagName("td")).get(0).getText().trim());
        productsPage.assertItems(itemNum);
        SubmissionHelper.endTest();
    }

    @Test
    public void test5_delete() {
        SubmissionHelper.startTest("test-delete-10");
        List<Product> products = this.productService.listAllProducts();
        int itemNum = products.size();

        ItemsPage productsPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            productsPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
            productsPage = ItemsPage.to(this.driver);
        }
        productsPage.getDeleteButtons().get(itemNum - 1).click();
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertItems(itemNum - 1);
        SubmissionHelper.endTest();
    }

    @Test
    public void test6_security_urls() {
        SubmissionHelper.startTest("test-security-20");
        List<Product> products = this.productService.listAllProducts();
        int itemNum = products.size();
        String editUrl = "/products/" + products.get(0).getId() + "/edit";

        ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");

        AbstractPage.get(this.driver, PRODUCTS_URL);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);
        AbstractPage.get(this.driver, PRODUCTS_ADD_URL);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);
        AbstractPage.get(this.driver, editUrl);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);
        AbstractPage.get(this.driver, "/random");
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);

        LoginPage loginPage = LoginPage.openLogin(this.driver);
        LoginPage.doLogin(this.driver, loginPage, admin, admin);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);


        AbstractPage.get(this.driver, PRODUCTS_URL);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);

        AbstractPage.get(this.driver, PRODUCTS_ADD_URL);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_ADD_URL);

        AbstractPage.get(this.driver, editUrl);
        AbstractPage.assertRelativeUrl(this.driver, editUrl);

        LoginPage.logout(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");

        SubmissionHelper.endTest();
    }

    @Test
    public void test7_security_buttons() {
        List<Product> products = this.productService.listAllProducts();
        int itemNum = products.size();

        ItemsPage productsPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        productsPage.assertButtons(0, 0, 0);

        LoginPage loginPage = LoginPage.openLogin(this.driver);
        productsPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        productsPage.assertButtons(itemNum, itemNum, 1);
    }




    private HtmlUnitDriver driver;

    private static String admin = "testAdmin";

    private static boolean dataInitialized = false;


    @BeforeEach
    private void setup() {
        this.driver = new HtmlUnitDriver(true);
        initData();
    }

    @AfterEach
    public void destroy() {
        if (this.driver != null) {
            this.driver.close();
        }
    }


    private void initData() {
        if (!dataInitialized) {
            userService.create(admin, admin, Role.ROLE_ADMIN);
            dataInitialized = true;
        }
    }


    @AfterAll
    public static void finializeAndSubmit() throws JsonProcessingException {
        CodeExtractor.submitSourcesAndLogs();
    }


    public static final String PRODUCTS_URL = "/products";
    public static final String PRODUCTS_ADD_URL = "/products/add";
    public static final String LOGIN_URL = "/login";
}
