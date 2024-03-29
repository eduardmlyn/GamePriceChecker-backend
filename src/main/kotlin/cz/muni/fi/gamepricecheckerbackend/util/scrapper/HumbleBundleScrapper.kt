package cz.muni.fi.gamepricecheckerbackend.util.scrapper

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.Select
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Implementation of scraping the Humble Bundle site.
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
class HumbleBundleScrapper(private val gameService: GameService, private val logger: Logger) : AbstractScrapper() {
    @Value(value = "\${app.humble-bundle.base.url}")
    lateinit var hbBaseUrl: String

    @Value(value = "\${app.humble-bundle.game-list.url}")
    lateinit var hbGameList: String


    private val seller = Seller.HUMBLE_BUNDLE

    override fun scrapeGamePrices(driver: ChromeDriver) {
        setCatalogWindow(driver)
        while (true) {
            simulateUserBehaviour()
            val catalogElements = getCatalogGameElements(driver)
            catalogElements.forEach {
                try {
                    extractAndSaveGameDataFromElement(driver, it)
                } catch (e: Exception) {
                    logger.error(e.message)
                }
            }
            if (!goToNextPageIfAble(driver)) return
        }
    }

    fun scrapeGameDetails(driver: ChromeDriver) {
        val gamesForSeller = gameService.getUpdatableGamesForSeller(seller)
        gamesForSeller.forEach {
            try {
                setGameDetailWindow(driver, it.second.link!!)
                val description = getGameDescription(driver)
                val imageUrl = getGameImage(driver)
                gameService.updateGameInformation(it.first, description, imageUrl, null)
            } catch (e: Exception) {
                logger.error(e.message)
            }
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

    private fun getGameDescription(driver: ChromeDriver): String? {
        waitForElements(driver, By.cssSelector("div[class*='description-view'] a[class\$='read-more-toggle']"))
        val showMoreButton = driver.findElements(
            By.cssSelector("div[class*='description-view'] a[class\$='read-more-toggle']")
        )
        if (showMoreButton.isEmpty()) return null
        clickButton(driver, showMoreButton[0])
        val descriptionElement = driver.findElements(By.cssSelector("div[class*='description-view'] .property-content"))
        if (descriptionElement.isEmpty()) return null
        return descriptionElement[0].text
    }

    private fun getGameImage(driver: ChromeDriver): String? {
        waitForElements(driver, By.cssSelector("div[class*='capsule-view'] img"))
        val imageElements = driver.findElements(By.cssSelector("div[class*='capsule-view'] img"))
        if (imageElements.isEmpty()) return null
        return imageElements[0].getAttribute("src")
    }

    private fun setCatalogWindow(driver: ChromeDriver) {
        driver.get("$hbBaseUrl$hbGameList")
    }

    private fun goToNextPageIfAble(driver: ChromeDriver): Boolean {
        waitForElements(driver, By.className("js-grid-next"))
        val nextButton = driver.findElement(By.className("js-grid-next"))
        val nextButtonClasses = nextButton.getAttribute("class")
        if (nextButtonClasses != null && nextButtonClasses.contains("disabled")) return false
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
        val gameName = element.findElement(By.cssSelector("span[class='entity-title']")).text
        if (comingSoonElement.size != 0) {
            logger.info("Skipping game $gameName scraping because of coming soon")
            return
        }
        val gamePriceString = element.findElement(By.cssSelector("span[class='price']")).text
        val gamePrice = gamePriceString.replace(Regex("[^\\d.]"), "").toDoubleOrNull()
        if (gamePrice != null) {
            gameService.saveGamePrice(gameName, gamePrice, gameDetailLink, seller)
            return
        }
        logger.info("Skipping game $gameName because of no price")
    }
}
