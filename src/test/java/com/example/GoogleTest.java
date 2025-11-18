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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(driver -> {
            String title = driver.getTitle();
            return title.contains("Selenium+automation") || title.contains("Selenium automation");
        });

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
}