package sscom;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.jetbrains.annotations.NotNull;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

class commonMethods {
    private static String OS = System.getProperty("os.name").toLowerCase();

    static WebDriver setupTests() {
        WebDriver driver;
        String webDriversPath;
        final ClassLoader classLoader = ssComTestClass.class.getClassLoader();
        if (OS.contains("win")) {
            webDriversPath = Objects.requireNonNull(classLoader.getResource("drivers")).getPath() + "\\";
            System.setProperty("webdriver.chrome.driver", webDriversPath + "chromedriver.exe");
        } else if (OS.contains("mac")) {
            webDriversPath = Objects.requireNonNull(classLoader.getResource("drivers")).getPath() + "/";
            System.setProperty("webdriver.chrome.driver", webDriversPath + "chromedriver");
        }
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        return driver;
    }

    static void quitTests(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }

    static void navigateToSearchPage(@NotNull WebDriver driver) {
        //opening main page
        driver.get("https://www.ss.com/");
        waitForLoad(driver);
        assertEquals("[ERROR-1] Wrong main page.", "Sludinājumi - SS.COM", driver.getTitle());
        //performing click on 'meklesana' menu item
        driver.findElement(By.cssSelector("span.page_header_menu>b.menu_main:nth-child(3)")).click();
        waitForLoad(driver);
        assertEquals("[ERROR-2] Wrong search page.", "Sludinājumi - SS.COM - Meklēt sludinājumus. Sludinājumus meklēšana", driver.getTitle());
    }

    //wait for page to be fully loaded method
    private static void waitForLoad(WebDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) Objects.requireNonNull(driver1)).executeScript("return document.readyState").equals("complete");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(pageLoadCondition);
    }

    //wait for element to be available method
    private static void waitForElement(WebDriver driver, String elementId) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
    }

    //method to simulate select of item from search inputs dropdown using mouse
    private static void clickOnInputsDropDownItem(WebDriver driver, String itemName) throws InterruptedException {
        waitForElement(driver, "preload_txt");
        moveMouseOverElement(driver, driver.findElement(By.xpath("//input[@id='ptxt']")));
        List<WebElement> list = driver.findElements(By.xpath("//div[@id='preload_txt']/div"));
        for (WebElement element : list) {
            if (element.getText().equals(itemName)) {
                moveMouseOverElementWithClick(driver, element);
                break;
            }
        }
    }

    private static void moveMouseOverElement(WebDriver driver, WebElement element) throws InterruptedException {
        Actions action = new Actions(driver);
        action.moveToElement(element);
        Thread.sleep(1000);
    }

    private static void moveMouseOverElementWithClick(WebDriver driver, WebElement element) throws InterruptedException {
        Actions action = new Actions(driver);
        action.moveToElement(element).click().build().perform();
        Thread.sleep(1000);
    }

    //check search result page when nothing was selected in search page
    static void assertSearchResultsPageDefaults(@NotNull WebDriver driver) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(driver.getTitle(), "SS.COM Meklēšanas rezultāti", "[ERROR-3] Wrong search results page.");
        softAssert.assertEquals(driver.findElement(By.xpath("//*[@id='page_main']/tbody/tr/td/table[2]")).getText(), "Pēc Jūsu pieprasījuma netika atrasts neviens sludinājums", "[ERROR-4] Wrong text on page.");
        softAssert.assertAll();
    }

    //check search result page
    static void assertSearchResultsPage(@NotNull WebDriver driver, @NotNull Map<String, String> inputs, boolean simulateMouseSelect) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(driver.getTitle(), "SS.COM " + inputs.get("sadala") + " - " + inputs.get("kategorija") + ". Meklēšanas rezultāti", "[ERROR-5] Wrong search results page.");

        if (simulateMouseSelect) {
            softAssert.assertEquals(driver.findElement(By.className("in1")).getAttribute("value"), inputs.get("finalString"), "[ERROR-6] Wrong text in 'Meklēšana:' input.");
        } else {
            softAssert.assertEquals(driver.findElement(By.className("in1")).getAttribute("value"), inputs.get("inputString"), "[ERROR-7] Wrong text in 'Meklēšana:' input.");
        }
        Select rezims = new Select(driver.findElement(By.xpath("//select[@class='filter_sel w95']")));
        Select rajons = new Select(driver.findElement(By.xpath("//select[@class='filter_sel w140 ']")));
        Select darijums = new Select(driver.findElement(By.xpath("//select[@class='filter_sel l100']")));
        softAssert.assertEquals(rezims.getFirstSelectedOption().getText(), "Saraksts", "[ERROR-8] Wrong value selected in 'Režīms:' dropdown.");
        softAssert.assertEquals(rajons.getFirstSelectedOption().getText(), "Visi sludinājumi", "[ERROR-9] Wrong value selected in 'Rajons:' dropdown.");
        softAssert.assertEquals(darijums.getFirstSelectedOption().getText(), "Visi", "[ERROR-10] Wrong value selected in 'Darījuma veids:' dropdown.");
        softAssert.assertEquals(driver.findElement(By.xpath("//*[@id='head_line']/td[1]/span[1]")).getText(), " Sludinājumi", "[ERROR-11] Search results are not present.");
        softAssert.assertAll();
    }

    //check search result page when wrong 'sadala' was selected in search page
    static void assertWrongSearchResultsPage(@NotNull WebDriver driver, @NotNull Map<String, String> inputs) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(driver.getTitle(), "SS.COM " + inputs.get("sadala") + ". Meklēšanas rezultāti", "[ERROR-12] Wrong search results page.");
        softAssert.assertEquals(driver.findElement(By.xpath("//*[@id='page_main']/tbody/tr/td/table[2]/tbody/tr/td")).getText(), "Pēc Jūsu pieprasījuma netika atrasts neviens sludinājums\nMēģiniet taisīt meklēšanu starp visiem sludinājumiem.", "[ERROR-13] Wrong text on page.");
        softAssert.assertEquals(driver.findElement(By.xpath("//*[@id='page_main']/tbody/tr/td/center/form/input")).getAttribute("value"), "Meklēt", "[ERROR-14] 'Meklēt' button is not shown.");
        softAssert.assertEquals(driver.findElement(By.xpath("//*[@id='page_main']/tbody/tr/td/div[2]/div")).getText(), "Skatīties sludinājumus sadaļā:", "[ERROR-15] 'Skatīties sludinājumus sadaļā:' text is not shown.");
        softAssert.assertEquals(driver.findElement(By.xpath("//*[@id='page_main']/tbody/tr/td/div[2]/h4[2]/a")).getText(), "Elektrotehnika : Sakaru līdzekļi : Mobilie telefoni : Apple : iPhone 8", "[ERROR-16] Link to correct search is not shown.");
        softAssert.assertAll();
    }

    //fill in search phrase and select needed items in dropdowns on search page
    static void performSearch(@NotNull WebDriver driver, @NotNull Map<String, String> inputs, boolean simulateMouseSelect) throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        driver.findElement(By.id("ptxt")).sendKeys(inputs.get("inputString"));
        if (simulateMouseSelect) {
            clickOnInputsDropDownItem(driver, inputs.get("finalString"));
        }
        Select sadala = new Select(driver.findElement(By.xpath("//select[@name='cid_0']")));
        sadala.selectByVisibleText(inputs.get("sadala"));
        Select ketegorija = new Select(driver.findElement(By.xpath("//select[@name='cid_1']")));
        if (sadala.getFirstSelectedOption().getText().equals("")) {
            assertEquals("[ERROR-17] 'Kategorija:' dropdown is visible. ", "display: none;", driver.findElement(By.id("category_tr")).getAttribute("style"));
        } else {
            ketegorija.selectByVisibleText(inputs.get("kategorija"));
        }
        Select pilseta = new Select(driver.findElement(By.xpath("//select[@id='s_region_select']")));
        pilseta.selectByVisibleText(inputs.get("pilseta"));
        Select periods = new Select(driver.findElement(By.xpath("//select[@name='pr']")));
        periods.selectByVisibleText(inputs.get("periods"));

        //assertions before click on "Search" to verify that all inserted/selected values are correct
        if (simulateMouseSelect) {
            softAssert.assertEquals(driver.findElement(By.className("in1")).getAttribute("value"), inputs.get("finalString"), "[ERROR-18] Wrong text in 'Meklējamais vārds vai frāze:' input.");
        } else {
            softAssert.assertEquals(driver.findElement(By.className("in1")).getAttribute("value"), inputs.get("inputString"), "[ERROR-19] Wrong text in 'Meklējamais vārds vai frāze:' input.");
        }
        softAssert.assertEquals(sadala.getFirstSelectedOption().getText(), inputs.get("sadala"), "[ERROR-20] Wrong value selected in 'Sadaļa:' dropdown.");
        softAssert.assertEquals(ketegorija.getFirstSelectedOption().getText(), inputs.get("kategorija"), "[ERROR-21] Wrong value selected in 'Kategorija:' dropdown.");
        softAssert.assertEquals(pilseta.getFirstSelectedOption().getText(), inputs.get("pilseta"), "[ERROR-22] Wrong value selected in 'Pilsēta, rajons:' dropdown.");
        softAssert.assertEquals(periods.getFirstSelectedOption().getText(), inputs.get("periods"), "[ERROR-23] Wrong value selected in 'Meklēt par periodu:' dropdown.");
        softAssert.assertAll();
        driver.findElement(By.className("btn")).click();
        waitForLoad(driver);
    }

    //verifying that 'kategorija' dropdown stays on the page
    //when some item was selected in 'sadala' dropdown and after that empty item selected in 'sadala'
    static void visibilityOfKategorija(@NotNull WebDriver driver, @NotNull Map<String, String> inputs) {
        assertEquals("[ERROR-24] 'Kategorija:' dropdown is visible. ", "display: none;", driver.findElement(By.id("category_tr")).getAttribute("style"));
        Select sadala = new Select(driver.findElement(By.xpath("//select[@name='cid_0']")));
        sadala.selectByVisibleText(inputs.get("sadala"));
        assertEquals("[ERROR-25] 'Kategorija:' dropdown is not visible. ", "display: table-row;", driver.findElement(By.id("category_tr")).getAttribute("style"));
        sadala.selectByVisibleText("");
        assertEquals("[ERROR-26] 'Kategorija:' dropdown is not visible. ", "display: table-row;", driver.findElement(By.id("category_tr")).getAttribute("style"));
    }
}
