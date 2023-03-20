package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * Implementation of scraping the Humble Bundle site.
 *
 * @author Eduard Stefan Mlynarik
 */
// TODO add catching exceptions and resolve them
@Service
class HumbleBundleScrapper(
    gameRepository: GameRepository,
    gameSellerRepository: GameSellerRepository
) : AbstractScrapper(gameRepository, gameSellerRepository) {
    @Value(value = "\${app.humble-bundle.base.url}")
    lateinit var hbBaseUrl: String

    @Value(value = "\${app.humble-bundle.game-list.url}")
    lateinit var hbGameList: String

    lateinit var originalWindow: String

    val seller = Seller.HUMBLE_BUNDLE

    override fun scrape(driver: ChromeDriver) {
        setCatalogWindow(driver)
        val allLinks = getAllCatalogLinks(driver)
        allLinks.forEach {
            goToGameDetailPage(driver, it)
            val gameName = getGameName(driver)
            val gamePrice = getGamePrice(driver)
            val gameDescription = getGameDescription(driver)
            val gameImage = getGameImage(driver)
//            printAll(gameName, gamePrice, gameDescription, gameImage)
            if (gamePrice != null) {
                saveScrappedGame(gameName, gamePrice, gameDescription, gameImage, null, it, seller)
            }
        }
        // todo definitely change things here
    }

    private fun printAll(name: String, price: Double?, description: String, image: String) {
        println("Game name: $name\nGame price: $price\nGame description: $description\nGame image link: $image")
    }

    private fun setCatalogWindow(driver: ChromeDriver) {
        driver.get("$hbBaseUrl$hbGameList")
        originalWindow = driver.windowHandle
    }

    private fun getAllCatalogLinks(driver: ChromeDriver): List<String> {
        val allCatalogLinks = mutableListOf<String>()
//        var counter = 1
        while (true) {
            Thread.sleep(Duration.ofSeconds(5))
            allCatalogLinks += getCurrentCatalogLinks(driver)
//            return allCatalogLinks
            if (!goToNextPageIfAble(driver)) return allCatalogLinks
//            if (counter >= 1) return allCatalogLinks
//            counter++
        }
    }

    private fun getCurrentCatalogLinks(driver: ChromeDriver): List<String> {
        waitForElements(driver, By.cssSelector("a[class='entity-link js-entity-link']"))
        val catalogLinks = driver.findElements(By.cssSelector("a[class='entity-link js-entity-link']"))
        return catalogLinks.map { it.getAttribute("href") }
    }

    private fun goToNextPageIfAble(driver: ChromeDriver): Boolean {
        waitForElements(driver, By.className("js-grid-next"))
        val nextButton = driver.findElement(By.className("js-grid-next"))
        if (!nextButton.isEnabled) return false
        clickButton(driver, nextButton)
        Thread.sleep(Duration.ofSeconds(2))
        return true
    }

    private fun goToGameDetailPage(driver: ChromeDriver, path: String) {
        driver.get(path)
        Thread.sleep(Duration.ofSeconds(2))
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
        Thread.sleep(Duration.ofSeconds(2))
    }

    private fun getGameName(driver: ChromeDriver): String {
        waitForElements(driver, By.cssSelector("h1[data-entity-kind='product']"))
        val nameElement = driver.findElement(By.cssSelector("h1[data-entity-kind='product']"))
        return nameElement.text
    }

    private fun getGamePrice(driver: ChromeDriver): Double? {
        waitForElements(driver, By.className("current-price"))
        val priceElement = driver.findElement(By.className("current-price"))
        return priceElement.text.replace(Regex("[^\\d.]"), "").toDoubleOrNull()
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

    private fun clickButton(driver: ChromeDriver, element: WebElement) {
        driver.executeScript("arguments[0].click()", element)
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
}
