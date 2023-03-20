package cz.muni.fi.gamepricecheckerbackend.util

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Component

/**
 * Configuration of Chrome Web Driver
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class ChromeDriverFactory {

    fun getChromeDriverInstance(): ChromeDriver {
        return initWebDriver()
    }

    fun destroyChromeDriverInstance(driver: ChromeDriver) {
        driver.quit()
    }

    private fun initWebDriver(): ChromeDriver {
        WebDriverManager.chromedriver().setup()
        val options = ChromeOptions()
        val userAgent = """
            Mozilla/5.0 (X11; Linux x86_64) 
            AppleWebKit/537.36 (KHTML, like Gecko) 
            Chrome/60.0.3112.50 Safari/537.36""".trimIndent()
        options.addArguments(
            "user-agent=$userAgent",
//            "--headless=new",
            "--window-size=1920,1080",
            "--disable-extensions",
            "--start-maximized",
            "--disable-gpu",
            "--remote-allow-origins=*"
        )
        return ChromeDriver(options)
    }
}