/*
Types of web elements:
- edit box
- link
- button
- image, image link, image button
- text area
- check box
- radio button
- drop down box
- list box
- combo box
*/

/*
Priority in decreasing order of selecting elements:
- By.id("email")
- By.name("password")
- By.cssSelector("input[type='email']")
- By.xpath("//input[@type='email']")
- By.linkText("Help")
- By.className("login-btn")
- driver.findElement(By.tagName("input"))
- By.xpath("/html/body/div[2]/button")   (absolute xpath)
 */

package com.example;

import java.time.Duration;
import org.junit.jupiter.api.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.*;

public class InterviewTest{
    private static WebDriver driver;
    private static WebDriverWait wait;

    public static void main(String[] args) {
        System.out.println("Hello Selenium!");
    }

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setup(){
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
    public void teardown(){
        if(driver != null) driver.quit();
    }


    @Test
    @DisplayName("First example")
    public void test1(){

        driver.get("https://example.com/");
        driver.findElement(By.cssSelector("html"));
        driver.findElement(By.xpath("//html"));
        driver.findElement(By.tagName("html"));

        driver.findElement(By.cssSelector("html > head"));
        driver.findElement(By.xpath("//html/head"));
        driver.findElement(By.tagName("head"));

        driver.findElement(By.xpath("//body"));
        driver.findElement(By.tagName("body"));

        driver.findElement(By.cssSelector("div"));
        driver.findElement(By.xpath("//div"));
        driver.findElement(By.tagName("div"));

        driver.findElement(By.cssSelector("div > h1"));
        driver.findElement(By.xpath("//h1[normalize-space()='Example Domain']"));
        driver.findElement(By.xpath("//h1[contains(normalize-space(),'Example Domain')]"));
        driver.findElement(By.xpath("//h1[normalize-space()='Example Domain']"));
        WebElement h1 = driver.findElement(By.tagName("h1"));
        String text = h1.getText(), attribute = h1.getAttribute("outerHTML"), css = h1.getCssValue("color");
        System.out.println(text + " , " + attribute + " , " + css);

        driver.findElement(By.cssSelector("div > p")); // only first element
        List<WebElement> elements = driver.findElements(By.cssSelector("div > p"));  // all such elements
        driver.findElement(By.xpath("//div/p[1]"));
        driver.findElement(By.tagName("p"));

        driver.findElement(By.xpath("//a[normalize-space()='Learn more']"));
        driver.findElement(By.xpath("//a[normalize-space()='Learn more']"));
        driver.findElement(By.tagName("a"));

        // for going to next page
        if (isClickable(driver, By.cssSelector("a[href='https://iana.org/domains/example']"))) {
            System.out.println("Clickable!");
        }
        driver.findElement(By.cssSelector("a[href='https://iana.org/domains/example']")).click();

    }


    @Test
    @Order(2)
    @DisplayName("Second example")
    public void test2() throws InterruptedException{

        driver.get("https://www.google.com/");
        WebElement type = driver.findElement(By.name("q"));
        type.clear();
        type.sendKeys("QA automation", Keys.ENTER);
        Thread.sleep(2000);

        //driver.findElement(By.cssSelector("div.FPdoLc > center > input.gNO89b")).getText().contentEquals("Google Search");
        WebElement type1 = driver.findElement(By.name("q"));
        type1.clear();
        Actions actions = new Actions(driver);

        // ctrl + A
        actions.keyDown(Keys.CONTROL)
        .sendKeys("a")
        .keyUp(Keys.CONTROL)
        .perform();

         // ctrl + C
        actions.keyDown(Keys.CONTROL)
        .sendKeys("c")
        .keyUp(Keys.CONTROL)
        .perform();

         // ctrl + V
        actions.keyDown(Keys.CONTROL)
        .sendKeys("v")
        .keyUp(Keys.CONTROL)
        .perform();

    }

    @Test
    @Order(3)
    @DisplayName("Third example")
    public void test3() throws InterruptedException{

        driver.get("https://www.amazon.in/");
        Thread.sleep(4000);
        driver.findElement(By.id("searchDropdownBox"));
        driver.findElement(By.xpath("//option[contains(normalize-space(),'Gift Cards')]")).click();
        if(driver.findElements(By.id("twotabsearchtextbox")).size()>0){
            WebElement textBox = driver.findElement(By.id("twotabsearchtextbox"));
            textBox.sendKeys("gift card voucher");
            System.out.println("TextBox text is:" + textBox.getAttribute("value"));
        }


    }


    public boolean isClickable(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}