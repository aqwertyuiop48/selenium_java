package com.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver once for all tests
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        ChromeOptions options = new ChromeOptions();
        // Comment out headless to see the browser during tests
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Test Example.com page title")
    public void testPageTitle() {
        driver.get("https://example.com");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String title = driver.getTitle();
        assertEquals("Example Domain", title, "Page title should be 'Example Domain'");
        System.out.println("✓ Page title test passed!");
    }

    @Test
    @DisplayName("Test heading text on Example.com")
    public void testHeadingText() {
        driver.get("https://example.com");
        WebElement heading = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        assertEquals("Example Domain", heading.getText(), "Heading should contain 'Example Domain'");
        System.out.println("✓ Heading text test passed!");
    }

    @Test
    @DisplayName("Test page body text content")
    public void testPageTextContent() {
        driver.get("https://example.com");
        WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String bodyText = body.getText();
        assertTrue(bodyText.contains("This domain is for use in documentation"),
                "Page should contain expected text about documentation");
        System.out.println("✓ Page text content test passed!");
    }

    @Test
    @DisplayName("Test navigation to IANA website")
    public void testNavigation() {
        // First visit example.com
        driver.get("https://example.com");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String firstUrl = driver.getCurrentUrl();
        assertTrue(firstUrl.contains("example.com"), "Should be on example.com");

        // Navigate to IANA (related to example.com)
        driver.get("https://www.iana.org/domains/reserved");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("iana.org"),
                "URL should contain iana.org, but was: " + currentUrl);
        System.out.println("✓ Navigation test passed! Current URL: " + currentUrl);
    }

    @Test
    @DisplayName("Test page has content")
    public void testPageHasContent() {
        driver.get("https://example.com");
        WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        assertNotNull(body, "Page should have body content");
        String bodyText = body.getText();
        assertTrue(bodyText.length() > 0, "Body should have text");
        assertTrue(bodyText.contains("Example Domain"), "Body should contain heading text");
        System.out.println("✓ Page content test passed!");
    }

    @Test
    @DisplayName("Test page structure")
    public void testPageStructure() {
        driver.get("https://example.com");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        // Verify h1 exists
        WebElement heading = driver.findElement(By.tagName("h1"));
        assertNotNull(heading, "Page should have h1 element");

        // Verify body exists and is visible
        WebElement body = driver.findElement(By.tagName("body"));
        assertTrue(body.isDisplayed(), "Body should be displayed");

        System.out.println("✓ Page structure test passed!");
    }
}