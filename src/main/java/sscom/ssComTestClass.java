package sscom;

import static sscom.commonMethods.*;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.HashMap;

public class ssComTestClass {

    private static WebDriver driver;
    final private Map<String, String> inputs = new HashMap<>();

    @BeforeMethod
    public void setup() {
        driver = setupTests();
    }

    @AfterMethod
    public void closeDriver() {
        quitTests(driver);
    }

    @Test
    public void assertSearchHappyFlowWithClickOnInputsDropDown() throws InterruptedException {
        //navigating to search page
        navigateToSearchPage(driver);

        //fill inputs map with test data
        inputs.put("inputString", "Iphone");
        inputs.put("finalString", "Iphone 8");
        inputs.put("sadala", "Elektrotehnika");
        inputs.put("kategorija", "Sakaru līdzekļi");
        inputs.put("pilseta", "Rīga");
        inputs.put("periods", "Pēdējās 3 dienās");

        //performing search simulating select of correct 'sadala' using mouse and test data from inputs map
        performSearch(driver, inputs, true);
        //verifying search results
        assertSearchResultsPage(driver, inputs, true);
    }

    @Test
    public void assertSearchResultsDefaults() throws InterruptedException {
        //navigating to search page
        navigateToSearchPage(driver);

        //fill inputs map with default data
        inputs.put("inputString", "");
        inputs.put("sadala", "");
        inputs.put("kategorija", "");
        inputs.put("pilseta", "Visi sludinājumi");
        inputs.put("periods", "Starp visiem sludinājumiem");

        //performing search without simulating select of correct 'sadala' using mouse and test data from inputs map
        performSearch(driver, inputs, false);
        //verifying search results
        assertSearchResultsPageDefaults(driver);
    }

    @Test
    public void assertSearchResultsHappyFlow() throws InterruptedException {
        //navigating to search page
        navigateToSearchPage(driver);

        //fill inputs map with test data
        inputs.put("inputString", "Iphone 8");
        inputs.put("sadala", "Elektrotehnika");
        inputs.put("kategorija", "Sakaru līdzekļi");
        inputs.put("pilseta", "Rīga");
        inputs.put("periods", "Pēdējās 3 dienās");

        //performing search without simulating select of correct 'sadala' using mouse and test data from inputs map
        performSearch(driver, inputs, false);
        //verifying search results
        assertSearchResultsPage(driver, inputs, false);
    }

    @Test
    public void assertWrongSearchResults() throws InterruptedException {
        //navigating to search page
        navigateToSearchPage(driver);

        //fill inputs map with test data
        inputs.put("inputString", "Iphone 8");
        inputs.put("sadala", "Darbs un bizness");
        inputs.put("kategorija", "");
        inputs.put("pilseta", "Jūrmala");
        inputs.put("periods", "Pēdejā nedēļā");

        //performing search without simulating select of correct 'sadala' using mouse and test data from inputs map
        performSearch(driver, inputs, false);
        //verifying search results
        assertWrongSearchResultsPage(driver, inputs);
    }

    @Test
    public void assertKategorijaVisibleAfterSadalaChangedToEmpty() {
        //navigating to search page
        navigateToSearchPage(driver);
        //fill inputs map with test data
        inputs.put("sadala", "Mājai");
        //verifying that 'kategorija' dropdown stays on the page
        //when some item was selected in 'sadala' dropdown and after that empty item selected in 'sadala'
        visibilityOfKategorija(driver, inputs);
    }
}
