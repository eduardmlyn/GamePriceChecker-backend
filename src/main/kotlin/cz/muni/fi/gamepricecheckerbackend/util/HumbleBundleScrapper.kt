package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.Game
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
class HumbleBundleScrapper : Scrapper {
    @Value(value = "\${app.humble-bundle.base.url}")
    lateinit var hbBaseUrl: String

    @Value(value = "\${app.humble-nundle.game-list.url}")
    lateinit var hbGameList: String

    lateinit var originalWindow: String

    override fun scrape(driver: ChromeDriver): List<Game> {
        getScrappedData(driver)
        return listOf()
    }

    fun getScrappedData(driver: ChromeDriver) {
        println(hbBaseUrl + hbGameList)
        driver.get("$hbBaseUrl$hbGameList")
        originalWindow = driver.windowHandle
        val allLinks = getAllCatalogLinks(driver)
        println("Printing all links")
        println(allLinks)
        allLinks.map {
            getCurrentGameData(driver, it)
        }
    }

    fun getAllCatalogLinks(driver: ChromeDriver): List<String> {
        val allCatalogLinks = mutableListOf<String>()
        while (true) {
            val wait = WebDriverWait(driver, Duration.ofSeconds(5))
            wait.until {
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("a[class\$='entity-link js-entity-link']")
                )
            }
            val currentCatalogLinks = driver.findElements(By.cssSelector("a[class\$='entity-link js-entity-link']"))
            val currentLinks = currentCatalogLinks.map { it.getAttribute("href") }
            println("Printing current links, amount: ${currentLinks.size}")
            println(currentLinks)
            allCatalogLinks += currentLinks
            val nextButton = driver.findElement(By.className("js-grid-next"))
            if (!nextButton.isEnabled) break
            driver.executeScript("arguments[0].click()", nextButton)
            // TODO solve request rate limit
            Thread.sleep(3000) // change value according to the rate limit
            break // TODO remove after testing
        }
        return allCatalogLinks
    }

    fun getCurrentGameData(driver: ChromeDriver, path: String) {
        driver.get(path)
        Thread.sleep(1000)
        val ageCheck = driver.findElements(By.className("age-check-container"))
        println(ageCheck)
        if (ageCheck.isNotEmpty()) {
            submitAgeCheck(driver)
        }
        WebDriverWait(driver, Duration.ofSeconds(5)).until {
            ExpectedConditions.presenceOfElementLocated(
                By.className("human_name-view")
            )
        }
        val gameName = driver.findElement(By.className("human_name-view"))
        val gamePrice = driver.findElement(By.className("current-price"))
        println(gameName.text)
        println(gamePrice.text)
    }

    fun submitAgeCheck(driver: ChromeDriver) {
        val yearElement = driver.findElement(By.className("js-selection-year"))
        Select(yearElement).selectByVisibleText("2000")
        val submitButton = driver.findElement(By.className("js-submit-button"))
        driver.executeScript("arguments[0].click()", submitButton)
        Thread.sleep(1000)
    }
}