package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class SeleniumDemo {
    public static void main(String[] args) {
        // Setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        // Remove --headless to see the browser
        options.addArguments("--headless");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");

        // Create WebDriver instance
        WebDriver driver = new ChromeDriver(options);

        // Set up explicit wait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            System.out.println("Navigating to example.com...");
            driver.get("https://example.com");

            // Wait for page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            String title = driver.getTitle();
            System.out.println("Page title: " + title);

            WebElement heading = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
            System.out.println("Heading text: " + heading.getText());

            // Get page body text (example.com no longer has the "More information..." link)
            WebElement body = driver.findElement(By.tagName("body"));
            System.out.println("Page content: " + body.getText());

            // Navigate to IANA (the site example.com used to link to)
            System.out.println("\nNavigating to IANA website...");
            driver.get("https://www.iana.org/domains/reserved");

            // Wait for new page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
            Thread.sleep(2000);

            System.out.println("New page title: " + driver.getTitle());
            System.out.println("Current URL: " + driver.getCurrentUrl());

            System.out.println("\n✓ Selenium demo completed successfully!");

        } catch (Exception e) {
            System.err.println("Error occurred:");
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}