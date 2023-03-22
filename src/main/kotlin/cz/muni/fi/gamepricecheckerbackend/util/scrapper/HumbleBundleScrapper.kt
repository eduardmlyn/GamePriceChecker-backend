package cz.muni.fi.gamepricecheckerbackend.util.scrapper

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.NoSuchElementException
import kotlin.random.Random

/**
 * Implementation of scraping the Humble Bundle site.
 *
 * @author Eduard Stefan Mlynarik
 */
// TODO add catching exceptions and resolve them
@Service
class HumbleBundleScrapper(
    val gameService: GameService
) :
    AbstractScrapper() {
    @Value(value = "\${app.humble-bundle.base.url}")
    lateinit var hbBaseUrl: String

    @Value(value = "\${app.humble-bundle.game-list.url}")
    lateinit var hbGameList: String

    private lateinit var originalWindow: String

    private val seller = Seller.HUMBLE_BUNDLE

    override fun scrapeGamePrices(driver: ChromeDriver) {
        setCatalogWindow(driver)
        var pageNum = 1
        while (true) {
            println("Currently on page $pageNum, might differ by 1")
            pageNum++
            simulateUserBehaviour()
            val catalogElements = getCatalogGameElements(driver)
            catalogElements.forEach {
                extractAndSaveGameDataFromElement(driver, it)
            }
            if (!goToNextPageIfAble(driver)) return
        }
    }

    override fun scrapeGameDetails(driver: ChromeDriver) {
        val gamesForSeller = gameService.getUpdatableGamesForSeller(seller)
        gamesForSeller.forEach {
            setGameDetailWindow(driver, it.link)
            // add Web driver waits
            val description = getGameDescription(driver)
            val imageUrl = getGameImage(driver)
            gameService.updateGameInformation(it.game.id, description, imageUrl, null)
        }
    }

    private fun setGameDetailWindow(driver: ChromeDriver, path: String) {
        driver.get(path)
        simulateUserBehaviour()
        val ageCheck = driver.findElements(By.className("age-check-container"))
        if (ageCheck.isNotEmpty()) submitAgeCheck(driver)
    }

    private fun submitAgeCheck(driver: ChromeDriver) {
        waitForElements(driver, By.className("js-selection-year"))
        waitForElements(driver, By.className("js-submit-button"))
        val yearElement = driver.findElement(By.className("js-selection-year"))
        Select(yearElement).selectByVisibleText("2000")
        val submitButton = driver.findElement(By.className("js-submit-button"))
        clickButton(driver, submitButton)
        simulateUserBehaviour()
    }

    private fun getGameDescription(driver: ChromeDriver): String {
        waitForElements(driver, By.cssSelector("div[class*='description-view'] a[class\$='read-more-toggle']"))
        val showMoreButton = driver.findElement(
            By.cssSelector("div[class*='description-view'] a[class\$='read-more-toggle']")
        )
        clickButton(driver, showMoreButton)
        val descriptionElement = driver.findElement(By.cssSelector("div[class*='description-view'] .property-content"))
        return descriptionElement.text
    }

    private fun getGameImage(driver: ChromeDriver): String {
        waitForElements(driver, By.cssSelector("div[class*='capsule-view'] img"))
        val imageElement = driver.findElement(
            By.cssSelector("div[class*='capsule-view'] img")
        )
        return imageElement.getAttribute("src")
    }

    private fun setCatalogWindow(driver: ChromeDriver) {
        driver.get("$hbBaseUrl$hbGameList")
        originalWindow = driver.windowHandle
    }

    private fun waitForElements(driver: ChromeDriver, locator: By) {
        WebDriverWait(
            driver,
            Duration.ofSeconds(5)
        ).until {
            ExpectedConditions.and(
                ExpectedConditions.presenceOfAllElementsLocatedBy(locator),
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
            )
        }
    }

    private fun clickButton(driver: ChromeDriver, element: WebElement) {
        driver.executeScript("arguments[0].click()", element)
    }

    private fun goToNextPageIfAble(driver: ChromeDriver): Boolean {
        waitForElements(driver, By.className("js-grid-next"))
        val nextButton = driver.findElement(By.className("js-grid-next"))
        if (!nextButton.isEnabled) return false
        clickButton(driver, nextButton)
        simulateUserBehaviour()
        return true
    }

    private fun getCatalogGameElements(driver: ChromeDriver): List<WebElement> {
        waitForElements(driver, By.cssSelector("div[class*='entity js-entity']"))
        return driver.findElements(By.cssSelector("div[class*='entity js-entity']"))
    }

    private fun extractAndSaveGameDataFromElement(driver: ChromeDriver, element: WebElement) {
        waitForElements(driver, By.cssSelector("a[class*='entity-link js-entity-link']"))
        waitForElements(driver, By.cssSelector("span[class='price']"))
        waitForElements(driver, By.cssSelector("span[class='entity-title']"))
        val gameDetailLink = element.findElement(
            By.cssSelector("a[class*='entity-link js-entity-link']")
        ).getAttribute("href")
        val comingSoonElement = element.findElements(By.cssSelector("span[class*='coming-soon']"))
        if (comingSoonElement.size != 0) return // TODO add log for skipping game with the name of the game
        val gamePriceString = element.findElement(By.cssSelector("span[class='price']")).text
        val gameName = element.findElement(By.cssSelector("span[class='entity-title']")).text
        val gamePrice = gamePriceString.replace(Regex("[^\\d.]"), "").toDoubleOrNull()
        if (gamePrice != null) {
            gameService.saveGamePrice(gameName, gamePrice, gameDetailLink, seller)
        }
        // Log not saved game because of price
    }

    private fun simulateUserBehaviour() {
        val randomWaitTime = Random.nextLong(2, 4)
        Thread.sleep(Duration.ofSeconds(randomWaitTime))
    }
}