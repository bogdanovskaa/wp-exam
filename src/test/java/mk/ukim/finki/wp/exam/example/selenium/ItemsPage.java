package mk.ukim.finki.wp.exam.example.selenium;

import lombok.Getter;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

@Getter
public class ItemsPage extends AbstractPage {

    @FindBy(css = "tr[class=item]")
    private List<WebElement> productRows;


    @FindBy(css = ".delete-item")
    private List<WebElement> deleteButtons;


    @FindBy(className = "edit-item")
    private List<WebElement> editButtons;


    @FindBy(css = ".add-item")
    private List<WebElement> addProductButton;

    public ItemsPage(WebDriver driver) {
        super(driver);
    }

    public static ItemsPage to(WebDriver driver) {
        get(driver, "/");
        return PageFactory.initElements(driver, ItemsPage.class);
    }

    public void assertElemts(int productsNumber, int deleteButtons, int editButtons, int addButtons) {
        Assert.assertEquals("Item's rows do not match", productsNumber, this.getProductRows().size());
        Assert.assertEquals("Delete do not match", deleteButtons, this.getDeleteButtons().size());
        Assert.assertEquals("Edit do not match", editButtons, this.getEditButtons().size());
        Assert.assertEquals("Add is visible", addButtons, this.getAddProductButton().size());
    }
}
