package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigManager;
import utils.LogHelper;

import java.time.Duration;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            String browser = ConfigManager.get("browser").toLowerCase();
            boolean isHeadless = ConfigManager.getBoolean("headless");

            LogHelper.info("Initializing browser: " + browser + " (headless: " + isHeadless + ")");
            WebDriver driver;

            switch (browser) {
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (isHeadless)
                        firefoxOptions.addArguments("-headless");
                    driver = new FirefoxDriver(firefoxOptions);
                    break;
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (isHeadless)
                        edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--start-maximized");
                    edgeOptions.addArguments("--disable-notifications");
                    edgeOptions.addArguments("--remote-allow-origins=*");
                    driver = new EdgeDriver(edgeOptions);
                    break;
                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (isHeadless)
                        chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            int implicitWait = ConfigManager.getInt("implicit.wait");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

            if (!browser.equals("firefox")) {
                driver.manage().window().maximize();
            }

            driverThreadLocal.set(driver);

            int explicitWait = ConfigManager.getInt("explicit.wait");
            waitThreadLocal.set(new WebDriverWait(driver, Duration.ofSeconds(explicitWait)));
        }
        return driverThreadLocal.get();
    }

    public static WebDriverWait getWait() {
        if (waitThreadLocal.get() == null) {
            getDriver();
        }
        return waitThreadLocal.get();
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
            waitThreadLocal.remove();
            LogHelper.info("Driver quit successfully");
        }
    }
}
