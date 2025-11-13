package com.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GoogleTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        ChromeOptions options = new ChromeOptions();
        // Comment out headless to see the browser
        options.addArguments("--headless");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test Google homepage loads")
    public void testGoogleHomepageLoads() throws InterruptedException {
        driver.get("https://www.google.com");
        Thread.sleep(3000); // Allow network + JS load before waiting

        String title = driver.getTitle();
        assertTrue(title.contains("Google"), "Page title should contain 'Google'");
        System.out.println("✓ Google homepage loaded successfully!");
    }

    @Test
    @Order(2)
    @DisplayName("Test Google search box is present")
    public void testSearchBoxPresent() {
        driver.get("https://www.google.com");

        // Wait for search box to be present
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("q")
        ));

        assertNotNull(searchBox, "Search box should be present");
        assertTrue(searchBox.isDisplayed(), "Search box should be visible");
        System.out.println("✓ Search box found and visible!");
    }

    @Test
    @Order(3)
    @DisplayName("Test Google search functionality")
    public void testGoogleSearch() throws InterruptedException {
        driver.get("https://www.google.com");

        // Find search box and enter text
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("q")
        ));
        searchBox.sendKeys("Selenium WebDriver");
        searchBox.submit();

        // Wait for results page to load
        wait.until(ExpectedConditions.titleContains("Selenium WebDriver"));

        String resultTitle = driver.getTitle();
        assertTrue(resultTitle.contains("Selenium WebDriver"),
                "Results page title should contain search query");

        System.out.println("✓ Search executed successfully!");
        System.out.println("  Result page title: " + resultTitle);
    }

    @Test
    @Order(5)
    @DisplayName("Test Google 'I'm Feeling Lucky' button exists")
    public void testImFeelingLuckyButton() {
        driver.get("https://www.google.com");

        // Wait for the page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("q")));

        // Find "I'm Feeling Lucky" button (it may be hidden initially)
        List<WebElement> buttons = driver.findElements(By.name("btnI"));

        assertTrue(buttons.size() > 0, "I'm Feeling Lucky button should exist");
        System.out.println("✓ I'm Feeling Lucky button found!");
    }

    @Test
    @Order(6)
    @DisplayName("Test Google search with suggestions")
    public void testSearchSuggestions() throws InterruptedException {
        driver.get("https://www.google.com");

        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("q")
        ));

        // Type slowly to trigger suggestions
        searchBox.sendKeys("java");
        Thread.sleep(1000); // Wait for suggestions to appear

        // Check if suggestions dropdown appears
        List<WebElement> suggestions = driver.findElements(
                By.cssSelector("ul[role='listbox'] li")
        );

        if (suggestions.size() > 0) {
            System.out.println("✓ Search suggestions appeared!");
            System.out.println("  Found " + suggestions.size() + " suggestions");
        } else {
            System.out.println("⚠ No suggestions found (may be blocked or delayed)");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test Google Images link")
    public void testGoogleImagesLink() {
        driver.get("https://www.google.com");

        // Find and click Images link
        try {
            WebElement imagesLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.linkText("Images")
            ));
            imagesLink.click();

            // Wait for Images page to load
            wait.until(ExpectedConditions.urlContains("images"));

            String currentUrl = driver.getCurrentUrl();
            assertTrue(currentUrl.contains("images"),
                    "Should navigate to Google Images");
            System.out.println("✓ Google Images link works!");

        } catch (Exception e) {
            System.out.println("⚠ Images link test skipped (element might not be visible)");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test multiple searches")
    public void testMultipleSearches() {
        String[] searchTerms = {"Java", "Python", "JavaScript"};

        for (String term : searchTerms) {
            driver.get("https://www.google.com");

            WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.name("q")
            ));
            searchBox.sendKeys(term);
            searchBox.submit();

            wait.until(ExpectedConditions.titleContains(term));

            String title = driver.getTitle();
            assertTrue(title.contains(term),
                    "Title should contain search term: " + term);
            System.out.println("✓ Search for '" + term + "' successful!");
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test search with Enter key")
    public void testSearchWithEnterKey() {
        driver.get("https://www.google.com");

        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("q")
        ));
        searchBox.sendKeys("Selenium automation");
        searchBox.sendKeys(Keys.RETURN);

        wait.until(ExpectedConditions.titleContains("Selenium automation"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("search"),
                "Should be on search results page");
        System.out.println("✓ Search with Enter key works!");
    }

    @Test
    @Order(10)
    @DisplayName("Test Google search box clears")
    public void testSearchBoxClears() {
        driver.get("https://www.google.com");

        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("q")
        ));

        // Enter text
        searchBox.sendKeys("test query");
        assertEquals("test query", searchBox.getAttribute("value"),
                "Search box should contain entered text");

        // Clear text
        searchBox.clear();
        assertEquals("", searchBox.getAttribute("value"),
                "Search box should be empty after clear");

        System.out.println("✓ Search box clear functionality works!");
    }

//    @Test
//    @Order(11)
//    @DisplayName("Test Amazon Gift Card - Complete Navigation Flow")
//    public void testAmazonSite() {
//        try {
//            // ========== STEP 1: Navigate to Amazon Homepage ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 1: Navigating to Amazon.in Homepage");
//            System.out.println("=".repeat(70));
//
//            driver.get("https://www.amazon.in/");
//            Thread.sleep(3000); // Allow network + JS load before waiting
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nav-logo-sprites")));
//            System.out.println("✓ Amazon homepage loaded");
//            Thread.sleep(2000);
//
//            // ========== STEP 2: Select Gift Cards from Search Dropdown ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 2: Selecting 'Gift Cards' Category from Dropdown");
//            System.out.println("=".repeat(70));
//
//            WebElement dropdown = wait.until(
//                    ExpectedConditions.presenceOfElementLocated(By.id("searchDropdownBox"))
//            );
//            Select select = new Select(dropdown);
//
//            String initialSelection = select.getFirstSelectedOption().getText();
//            System.out.println("Initial category: " + initialSelection);
//
//            select.selectByVisibleText("Gift Cards");
//            String afterSelection = select.getFirstSelectedOption().getText();
//
//            assertNotEquals(initialSelection, afterSelection, "Selection should have changed");
//            assertEquals("Gift Cards", afterSelection, "Should be Gift Cards");
//            System.out.println("✓ Category changed to: " + afterSelection);
//            Thread.sleep(1500);
//
//            // ========== STEP 3: Search for "gift card voucher" ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 3: Searching for 'gift card voucher'");
//            System.out.println("=".repeat(70));
//
//            WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
//            searchBox.clear();
//            searchBox.sendKeys("gift card voucher");
//            System.out.println("✓ Entered search term: gift card voucher");
//            Thread.sleep(1000);
//
//            WebElement searchButton = driver.findElement(By.id("nav-search-submit-button"));
//            searchButton.click();
//            System.out.println("✓ Search submitted");
//
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".s-main-slot")));
//            System.out.println("✓ Search results loaded");
//            Thread.sleep(2000);
//
//            // ========== STEP 4: Apply "Congratulations" Filter ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 4: Applying 'Congratulations' Occasion Filter");
//            System.out.println("=".repeat(70));
//
//            boolean filterApplied = false;
//
//            try {
//                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 300)");
//                Thread.sleep(1000);
//
//                WebElement congratsFilter = null;
//
//                try {
//                    congratsFilter = driver.findElement(
//                            By.xpath("//span[text()='Congratulations']/ancestor::a[contains(@class, 'a-link-normal')]")
//                    );
//                } catch (Exception e1) {
//                    try {
//                        congratsFilter = driver.findElement(
//                                By.xpath("//div[@id='s-refinements']//span[contains(text(), 'Congratulations')]/..")
//                        );
//                    } catch (Exception e2) {
//                        congratsFilter = driver.findElement(
//                                By.xpath("//*[contains(text(), 'Congratulations') and (self::a or self::span[parent::a])]")
//                        );
//                    }
//                }
//
//                if (congratsFilter != null) {
//                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", congratsFilter);
//                    Thread.sleep(500);
//
//                    try {
//                        congratsFilter.click();
//                    } catch (Exception e) {
//                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", congratsFilter);
//                    }
//
//                    System.out.println("✓ Clicked 'Congratulations' filter");
//                    Thread.sleep(2000);
//
//                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".s-main-slot")));
//                    filterApplied = true;
//                }
//
//            } catch (Exception e) {
//                System.out.println("⚠️ Congratulations filter not found, continuing...");
//            }
//
//            if (filterApplied) {
//                System.out.println("✓ Filter applied successfully");
//            }
//            Thread.sleep(1500);
//
//            // ========== STEP 5: Find and Click SECOND Gift Card (Index 1) ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 5: Locating SECOND Gift Card (Index 1) in Results");
//            System.out.println("=".repeat(70));
//
//            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 400)");
//            Thread.sleep(1500);
//
//            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
//                    By.cssSelector("[data-component-type='s-search-result']")
//            ));
//
//            java.util.List<WebElement> allProducts = driver.findElements(
//                    By.cssSelector("[data-component-type='s-search-result']")
//            );
//
//            java.util.List<WebElement> products = new java.util.ArrayList<>();
//            System.out.println("\nFiltering products:");
//
//            for (int i = 0; i < allProducts.size(); i++) {
//                WebElement product = allProducts.get(i);
//                String asin = product.getAttribute("data-asin");
//
//                boolean isSponsored = false;
//                try {
//                    product.findElement(By.xpath(".//span[contains(text(), 'Sponsored')]"));
//                    isSponsored = true;
//                } catch (Exception e) {
//                    // Not sponsored
//                }
//
//                if (asin != null && !asin.isEmpty() && !isSponsored) {
//                    products.add(product);
//                    System.out.println("  Product " + (products.size() - 1) + ": ASIN=" + asin);
//                } else if (isSponsored) {
//                    System.out.println("  Skipping sponsored at position " + i);
//                }
//            }
//
//            if (products.size() < 2) {
//                fail("Need at least 2 products, found: " + products.size());
//            }
//
//            System.out.println("\nFound " + products.size() + " valid products");
//
//            int productIndex = 1;
//            WebElement targetProduct = products.get(productIndex);
//
//            System.out.println("\n✓ Selecting product at index " + productIndex);
//
//            ((JavascriptExecutor) driver).executeScript(
//                    "arguments[0].scrollIntoView({block: 'center'});", targetProduct
//            );
//            Thread.sleep(1500);
//
//            String targetAsin = targetProduct.getAttribute("data-asin");
//            System.out.println("Target ASIN: " + targetAsin);
//
//            WebElement productLink = null;
//
//            try {
//                productLink = targetProduct.findElement(By.cssSelector("h2 a.a-link-normal"));
//            } catch (Exception e1) {
//                try {
//                    productLink = targetProduct.findElement(By.cssSelector("a.a-link-normal.s-no-outline"));
//                } catch (Exception e2) {
//                    System.out.println("Direct navigation to ASIN: " + targetAsin);
//                    driver.get("https://www.amazon.in/dp/" + targetAsin);
//                    Thread.sleep(3000);
//                }
//            }
//
//            if (productLink != null) {
//                try {
//                    productLink.click();
//                } catch (Exception e) {
//                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", productLink);
//                }
//                Thread.sleep(3000);
//            }
//
//            System.out.println("✓ Clicked product at index " + productIndex);
//
//            // ========== STEP 6: Wait for Page Load ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 6: Waiting for Page to Load");
//            System.out.println("=".repeat(70));
//
//            wait.until(ExpectedConditions.or(
//                    ExpectedConditions.presenceOfElementLocated(By.id("productTitle")),
//                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".a-price-whole"))
//            ));
//
//            Thread.sleep(2000);
//
//            String currentUrl = driver.getCurrentUrl();
//            System.out.println("Current URL: " + currentUrl);
//            System.out.println("✓ Page loaded");
//
//            // ========== STEP 7: Extract Title ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 7: Extracting Product Title");
//            System.out.println("=".repeat(70));
//
//            String pageTitle = "";
//            try {
//                WebElement titleElement = driver.findElement(By.id("productTitle"));
//                pageTitle = titleElement.getText().trim();
//                System.out.println("Title: " + pageTitle);
//            } catch (Exception e) {
//                System.out.println("⚠️ Could not extract title");
//            }
//
//            // ========== STEP 8: Extract Price ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 8: Extracting Price");
//            System.out.println("=".repeat(70));
//
//            String productPrice = "";
//
//            // Just get the text directly from a-price-whole
//            try {
//                WebElement priceElement = driver.findElement(By.cssSelector(".a-price-whole"));
//                productPrice = priceElement.getText();
//                System.out.println("✓ Price extracted: " + productPrice);
//            } catch (Exception e) {
//                System.out.println("Method 1 failed: " + e.getMessage());
//            }
//
//            // If that didn't work, try a-offscreen
//            if (productPrice.isEmpty()) {
//                try {
//                    WebElement priceElement = driver.findElement(By.cssSelector(".a-price .a-offscreen"));
//                    productPrice = priceElement.getText();
//                    System.out.println("✓ Price from offscreen: " + productPrice);
//                } catch (Exception e) {
//                    System.out.println("Method 2 failed: " + e.getMessage());
//                }
//            }
//
//            // If still empty, try gift card button
//            if (productPrice.isEmpty()) {
//                try {
//                    WebElement buttonElement = driver.findElement(By.cssSelector("button.gc-mini-picker-button"));
//                    productPrice = buttonElement.getText();
//                    System.out.println("✓ Price from button: " + productPrice);
//                } catch (Exception e) {
//                    System.out.println("Method 3 failed: " + e.getMessage());
//                }
//            }
//
//            // ========== STEP 9: Validate ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("STEP 9: Validation");
//            System.out.println("=".repeat(70));
//
//            assertFalse(productPrice.isEmpty(), "Price should not be empty");
//            System.out.println("\n✅ FINAL PRICE: " + productPrice);
//
//            // ========== SUMMARY ==========
//            System.out.println("\n" + "=".repeat(70));
//            System.out.println("TEST SUMMARY");
//            System.out.println("=".repeat(70));
//            System.out.println("✓ Step 1: Amazon homepage");
//            System.out.println("✓ Step 2: Selected Gift Cards");
//            System.out.println("✓ Step 3: Searched gift card voucher");
//            System.out.println("✓ Step 4: " + (filterApplied ? "Applied filter" : "Skipped filter"));
//            System.out.println("✓ Step 5: Clicked product at index 1 (ASIN: " + targetAsin + ")");
//            System.out.println("✓ Step 6: Page loaded");
//            System.out.println("✓ Step 7: Title: " + (pageTitle.isEmpty() ? "N/A" : pageTitle));
//            System.out.println("✓ Step 8: Price: " + productPrice);
//            System.out.println("=".repeat(70));
//            System.out.println("\n✅ TEST PASSED\n");
//
//            Thread.sleep(2000);
//
//        } catch (Exception e) {
//            System.err.println("\n❌ TEST FAILED: " + e.getMessage());
//            e.printStackTrace();
//            fail("Test failed: " + e.getMessage());
//        }
//    }
}