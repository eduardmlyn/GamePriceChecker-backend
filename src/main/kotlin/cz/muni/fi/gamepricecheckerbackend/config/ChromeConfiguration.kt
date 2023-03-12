package cz.muni.fi.gamepricecheckerbackend.config

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of Chrome Web Driver
 *
 * @author Eduard Stefan Mlynarik
 */
@Configuration
class ChromeConfiguration {
    var webDriver: ChromeDriver? = null

    @Bean
    fun chromeDriver(): ChromeDriver {
        return webDriver ?: initWebDriver()
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
//            "--headless",
            "--window-size=1920,1080",
            "--disable-extensions",
            "--start-maximized",
            "--disable-gpu",
            "--ignore-certificate-errors"
        )
        return ChromeDriver(options)
    }
}