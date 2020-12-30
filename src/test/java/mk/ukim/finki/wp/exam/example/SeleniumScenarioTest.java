package mk.ukim.finki.wp.exam.example;

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
import mk.ukim.finki.wp.exam.util.ExamAssert;
import mk.ukim.finki.wp.exam.util.SubmissionHelper;
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
        SubmissionHelper.session = "2020-kol2";
        SubmissionHelper.index = "201234";
    }


    public static final String PRODUCTS_URL = "/products";
    public static final String PRODUCTS_ADD_URL = "/products/add";
    public static final String LOGIN_URL = "/login";

    @Autowired
    UserService userService;


    @Autowired
    CategoryService categoryService;


    @Autowired
    ProductService productService;


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

    @Test
    public void testServiceInit() {
        ExamAssert.assertEquals("products not initialized", 10, this.productService.listAllProducts().size());
    }

    @Test
    public void testServiceSearch() {
        ExamAssert.assertEquals("by name and category null", 2, this.productService.listProductsByNameAndCategory("uct 1", null).size());
        ExamAssert.assertEquals("by category 1L", 10, this.productService.listProductsByNameAndCategory(null, 1L).size());
        ExamAssert.assertEquals("by name and category 1L", 2, this.productService.listProductsByNameAndCategory("uct 1", 1L).size());
    }

    @Test
    public void testScenarioNoSecurity() throws Exception {
        List<Category> categories = this.categoryService.listAll();
        List<Product> products = this.productService.listAllProducts();

        int itemNum = products.size();

        String[] productCategories = new String[]{
                categories.get(0).getId().toString(),
                categories.get(categories.size() - 1).getId().toString()
        };

        ItemsPage productsPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        productsPage.assertElemts(itemNum, itemNum, itemNum, 1);

        productsPage = AddOrEditProduct.add(this.driver, PRODUCTS_ADD_URL, "test", "100", "5", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum + 1, itemNum + 1, itemNum + 1, 1);

        productsPage = AddOrEditProduct.add(this.driver, PRODUCTS_ADD_URL, "other test", "100", "5", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum + 2, itemNum + 2, itemNum + 2, 1);

        productsPage.getDeleteButtons().get(itemNum + 1).click();
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum + 1, itemNum + 1, itemNum + 1, 1);

        productsPage = AddOrEditProduct.update(this.driver, productsPage.getEditButtons().get(itemNum), "test1", "200", "4", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        ExamAssert.assertEquals("The updated product name is not as expected.", "test1", productsPage.getProductRows().get(itemNum).findElements(By.tagName("td")).get(0).getText().trim());
        productsPage.assertElemts(itemNum + 1, itemNum + 1, itemNum + 1, 1);


        productsPage.getDeleteButtons().get(itemNum).click();
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum, itemNum, itemNum, 1);
    }


    @Test
    public void testSecurityScenario() throws Exception {

        List<Category> categories = this.categoryService.listAll();
        List<Product> products = this.productService.listAllProducts();

        int itemNum = products.size();

        String[] productCategories = new String[]{
                categories.get(0).getId().toString(),
                categories.get(categories.size() - 1).getId().toString()
        };

        ItemsPage productsPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        productsPage.assertElemts(itemNum, 0, 0, 0);


        LoginPage loginPage = LoginPage.openLogin(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);

        productsPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum, itemNum, itemNum, 1);

        productsPage = AddOrEditProduct.add(this.driver, PRODUCTS_ADD_URL, "test", "100", "5", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum + 1, itemNum + 1, itemNum + 1, 1);

        productsPage = AddOrEditProduct.add(this.driver, PRODUCTS_ADD_URL, "other test", "100", "5", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum + 2, itemNum + 2, itemNum + 2, 1);

        productsPage.getDeleteButtons().get(itemNum + 1).click();
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum + 1, itemNum + 1, itemNum + 1, 1);

        productsPage = AddOrEditProduct.update(this.driver, productsPage.getEditButtons().get(itemNum), "test1", "200", "4", productCategories);
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        ExamAssert.assertEquals("The updated product name is not as expected.", "test1", productsPage.getProductRows().get(itemNum).getText().trim());
        productsPage.assertElemts(itemNum + 1, itemNum + 1, itemNum + 1, 1);

        productsPage.getDeleteButtons().get(itemNum).click();
        AbstractPage.assertRelativeUrl(this.driver, PRODUCTS_URL);
        productsPage.assertElemts(itemNum, itemNum, itemNum, 1);

        LoginPage.logout(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        productsPage.assertElemts(itemNum, 0, 0, 0);
    }

    @Test
    public void testProductsFilter() throws Exception {
        List<Category> categories = this.categoryService.listAll();
        List<Product> products = this.productService.listAllProducts();

        int itemNum = products.size();

        String[] productCategories = new String[]{
                categories.get(0).getId().toString(),
                categories.get(categories.size() - 1).getId().toString()
        };

        ItemsPage productsPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");
        productsPage = productsPage.filter("uct 1", "1");
        ExamAssert.assertEquals("by name and category 1L", 2, productsPage.getProductRows().size());
    }
}
