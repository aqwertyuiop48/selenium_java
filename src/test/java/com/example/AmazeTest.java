package com.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazeTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String screenshotDir = "screenshots";

    @BeforeAll
    public static void setupClass() {
        // Create screenshots directory
        new File(screenshotDir).mkdirs();
    }

    @BeforeEach
    public void setupTest() {
        try {
            ChromeOptions options = new ChromeOptions();
            
            // Always run headless in CI
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-notifications");
            
            // Get Selenium Grid URL from environment variable or use default
            String seleniumUrl = System.getenv("SELENIUM_REMOTE_URL");
            if (seleniumUrl == null || seleniumUrl.isEmpty()) {
                seleniumUrl = "http://localhost:4444/wd/hub";
            }
            
            System.out.println("Connecting to Selenium Grid at: " + seleniumUrl);
            
            // Use RemoteWebDriver instead of ChromeDriver
            driver = new RemoteWebDriver(new URL(seleniumUrl), options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(25));
            
            System.out.println("✓ RemoteWebDriver initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize RemoteWebDriver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("WebDriver initialization failed", e);
        }
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            try {
                // Take screenshot on failure
                if (driver instanceof TakesScreenshot) {
                    takeScreenshot("final-state");
                }
            } catch (Exception e) {
                System.err.println("Failed to take screenshot: " + e.getMessage());
            }
            driver.quit();
        }
    }

    private void takeScreenshot(String name) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = screenshotDir + "/" + name + "-" + timestamp + ".png";
            Files.copy(source.toPath(), Paths.get(filename));
            System.out.println("Screenshot saved: " + filename);
        } catch (Exception e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test Amazon Gift Card - Complete Navigation Flow")
    public void testAmazonSite() {
        try {
            // ========== STEP 1: Navigate to Amazon Homepage ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 1: Navigating to Amazon.in Homepage");
            System.out.println("=".repeat(70));
            
            driver.get("https://www.amazon.in/");
            Thread.sleep(3000);
            
            System.out.println("✓ Amazon homepage loaded");
            takeScreenshot("step1-homepage");
            Thread.sleep(2000);

            // ========== STEP 2: Select Gift Cards from Search Dropdown ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 2: Selecting 'Gift Cards' Category from Dropdown");
            System.out.println("=".repeat(70));
            
            WebElement dropdown = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("searchDropdownBox"))
            );
            Select select = new Select(dropdown);
            String initialSelection = select.getFirstSelectedOption().getText();
            System.out.println("Initial category: " + initialSelection);
            
            select.selectByVisibleText("Gift Cards");
            String afterSelection = select.getFirstSelectedOption().getText();
            
            assertNotEquals(initialSelection, afterSelection, "Selection should have changed");
            assertEquals("Gift Cards", afterSelection, "Should be Gift Cards");
            System.out.println("✓ Category changed to: " + afterSelection);
            takeScreenshot("step2-category-selected");
            Thread.sleep(1500);

            // ========== STEP 3: Search for "gift card voucher" ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 3: Searching for 'gift card voucher'");
            System.out.println("=".repeat(70));
            
            WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
            searchBox.clear();
            searchBox.sendKeys("gift card voucher");
            System.out.println("✓ Entered search term: gift card voucher");
            Thread.sleep(1000);
            
            WebElement searchButton = driver.findElement(By.id("nav-search-submit-button"));
            searchButton.click();
            System.out.println("✓ Search submitted");
            
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".s-main-slot")));
            System.out.println("✓ Search results loaded");
            takeScreenshot("step3-search-results");
            Thread.sleep(2000);

            // ========== STEP 4: Apply "Congratulations" Filter ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 4: Applying 'Congratulations' Occasion Filter");
            System.out.println("=".repeat(70));
            
            boolean filterApplied = false;
            try {
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 300)");
                Thread.sleep(1000);
                
                WebElement congratsFilter = null;
                try {
                    congratsFilter = driver.findElement(
                            By.xpath("//span[text()='Congratulations']/ancestor::a[contains(@class, 'a-link-normal')]")
                    );
                } catch (Exception e1) {
                    try {
                        congratsFilter = driver.findElement(
                                By.xpath("//div[@id='s-refinements']//span[contains(text(), 'Congratulations')]/..")
                        );
                    } catch (Exception e2) {
                        congratsFilter = driver.findElement(
                                By.xpath("//*[contains(text(), 'Congratulations') and (self::a or self::span[parent::a])]")
                        );
                    }
                }
                
                if (congratsFilter != null) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", congratsFilter);
                    Thread.sleep(500);
                    
                    try {
                        congratsFilter.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", congratsFilter);
                    }
                    System.out.println("✓ Clicked 'Congratulations' filter");
                    Thread.sleep(2000);
                    
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".s-main-slot")));
                    filterApplied = true;
                    takeScreenshot("step4-filter-applied");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Congratulations filter not found, continuing...");
                takeScreenshot("step4-filter-not-found");
            }
            
            if (filterApplied) {
                System.out.println("✓ Filter applied successfully");
            }
            Thread.sleep(1500);

            // ========== STEP 5: Find and Click SECOND Gift Card (Index 1) ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 5: Locating SECOND Gift Card (Index 1) in Results");
            System.out.println("=".repeat(70));
            
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 400)");
            Thread.sleep(1500);
            
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("[data-component-type='s-search-result']")
            ));
            
            List<WebElement> allProducts = driver.findElements(
                    By.cssSelector("[data-component-type='s-search-result']")
            );
            
            List<WebElement> products = new ArrayList<>();
            System.out.println("\nFiltering products:");
            
            for (int i = 0; i < allProducts.size(); i++) {
                WebElement product = allProducts.get(i);
                String asin = product.getAttribute("data-asin");
                boolean isSponsored = false;
                
                try {
                    product.findElement(By.xpath(".//span[contains(text(), 'Sponsored')]"));
                    isSponsored = true;
                } catch (Exception e) {
                    // Not sponsored
                }
                
                if (asin != null && !asin.isEmpty() && !isSponsored) {
                    products.add(product);
                    System.out.println("  Product " + (products.size() - 1) + ": ASIN=" + asin);
                } else if (isSponsored) {
                    System.out.println("  Skipping sponsored at position " + i);
                }
            }
            
            if (products.size() < 2) {
                takeScreenshot("step5-insufficient-products");
                fail("Need at least 2 products, found: " + products.size());
            }
            
            System.out.println("\nFound " + products.size() + " valid products");
            
            int productIndex = 1;
            WebElement targetProduct = products.get(productIndex);
            System.out.println("\n✓ Selecting product at index " + productIndex);
            
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", targetProduct
            );
            Thread.sleep(1500);
            
            String targetAsin = targetProduct.getAttribute("data-asin");
            System.out.println("Target ASIN: " + targetAsin);
            
            takeScreenshot("step5-target-product");
            
            WebElement productLink = null;
            try {
                productLink = targetProduct.findElement(By.cssSelector("h2 a.a-link-normal"));
            } catch (Exception e1) {
                try {
                    productLink = targetProduct.findElement(By.cssSelector("a.a-link-normal.s-no-outline"));
                } catch (Exception e2) {
                    System.out.println("Direct navigation to ASIN: " + targetAsin);
                    driver.get("https://www.amazon.in/dp/" + targetAsin);
                    Thread.sleep(3000);
                }
            }
            
            if (productLink != null) {
                try {
                    productLink.click();
                } catch (Exception e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", productLink);
                }
                Thread.sleep(3000);
            }
            
            System.out.println("✓ Clicked product at index " + productIndex);

            // ========== STEP 6: Wait for Page Load ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 6: Waiting for Page to Load");
            System.out.println("=".repeat(70));
            
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.id("productTitle")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".a-price-whole"))
            ));
            Thread.sleep(2000);
            
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL: " + currentUrl);
            System.out.println("✓ Page loaded");
            takeScreenshot("step6-product-page");

            // ========== STEP 7: Extract Title ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 7: Extracting Product Title");
            System.out.println("=".repeat(70));
            
            String pageTitle = "";
            try {
                WebElement titleElement = driver.findElement(By.id("productTitle"));
                pageTitle = titleElement.getText().trim();
                System.out.println("Title: " + pageTitle);
            } catch (Exception e) {
                System.out.println("⚠️ Could not extract title");
            }

            // ========== STEP 8: Extract Price ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 8: Extracting Price");
            System.out.println("=".repeat(70));
            
            String productPrice = "";
            
            // Try a-price-whole
            try {
                WebElement priceElement = driver.findElement(By.cssSelector(".a-price-whole"));
                productPrice = priceElement.getText();
                System.out.println("✓ Price extracted: " + productPrice);
            } catch (Exception e) {
                System.out.println("Method 1 failed: " + e.getMessage());
            }
            
            // Try a-offscreen
            if (productPrice.isEmpty()) {
                try {
                    WebElement priceElement = driver.findElement(By.cssSelector(".a-price .a-offscreen"));
                    productPrice = priceElement.getText();
                    System.out.println("✓ Price from offscreen: " + productPrice);
                } catch (Exception e) {
                    System.out.println("Method 2 failed: " + e.getMessage());
                }
            }
            
            // Try gift card button
            if (productPrice.isEmpty()) {
                try {
                    WebElement buttonElement = driver.findElement(By.cssSelector("button.gc-mini-picker-button"));
                    productPrice = buttonElement.getText();
                    System.out.println("✓ Price from button: " + productPrice);
                } catch (Exception e) {
                    System.out.println("Method 3 failed: " + e.getMessage());
                }
            }

            // ========== STEP 9: Validate ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("STEP 9: Validation");
            System.out.println("=".repeat(70));
            
            assertFalse(productPrice.isEmpty(), "Price should not be empty");
            System.out.println("\n✅ FINAL PRICE: " + productPrice);
            takeScreenshot("step9-final-validation");

            // ========== SUMMARY ==========
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST SUMMARY");
            System.out.println("=".repeat(70));
            System.out.println("✓ Step 1: Amazon homepage");
            System.out.println("✓ Step 2: Selected Gift Cards");
            System.out.println("✓ Step 3: Searched gift card voucher");
            System.out.println("✓ Step 4: " + (filterApplied ? "Applied filter" : "Skipped filter"));
            System.out.println("✓ Step 5: Clicked product at index 1 (ASIN: " + targetAsin + ")");
            System.out.println("✓ Step 6: Page loaded");
            System.out.println("✓ Step 7: Title: " + (pageTitle.isEmpty() ? "N/A" : pageTitle));
            System.out.println("✓ Step 8: Price: " + productPrice);
            System.out.println("=".repeat(70));
            System.out.println("\n✅ TEST PASSED\n");
            
            Thread.sleep(2000);
            
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("test-failure");
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Third example")
    public void test3() throws InterruptedException {
        try {
            driver.get("https://www.amazon.in/");
            Thread.sleep(4000);
            takeScreenshot("test3-homepage");
            
            driver.findElement(By.id("searchDropdownBox"));
            driver.findElement(By.xpath("//option[contains(normalize-space(),'Gift Cards')]")).click();
            
            if (driver.findElements(By.id("twotabsearchtextbox")).size() > 0) {
                WebElement textBox = driver.findElement(By.id("twotabsearchtextbox"));
                textBox.sendKeys("gift card voucher");
                System.out.println("TextBox text is:" + textBox.getAttribute("value"));
            }
            
            Thread.sleep(3000);
            takeScreenshot("test3-search");
            
            driver.findElement(By.xpath("//span[normalize-space()='Congratulations']"));
            
            System.out.println("✅ Test 3 completed successfully");
        } catch (Exception e) {
            takeScreenshot("test3-failure");
            throw e;
        }
    }
}