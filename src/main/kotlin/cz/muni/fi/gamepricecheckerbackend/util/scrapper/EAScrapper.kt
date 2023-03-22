package cz.muni.fi.gamepricecheckerbackend.util.scrapper

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

/**
 *
 * @author Eduard Stefan Mlynarik
 */
// TODO add webdriverWaits
@Service
class EAScrapper(val gameService: GameService) : AbstractScrapper() {
    @Value(value = "\${app.ea-games.base.url}")
    lateinit var eaBaseUrl: String

    @Value(value = "\${app.ea-games.game-list.url}")
    lateinit var eaGameList: String

    // is this even needed?
    lateinit var originalWindow: String

    private val seller = Seller.EA_GAMES

    override fun scrapeGamePrices(driver: ChromeDriver) {
        setCatalogWindow(driver)
        val allLinks = getAllCatalogLinks(driver)
        allLinks.forEach {
            if (!goToGameDetailPage(driver, "$eaBaseUrl$it")) return@forEach
            val gameName = getGameName(driver)
            println(gameName)
        }
    }

    override fun scrapeGameDetails(driver: ChromeDriver) {

    }

    private fun setCatalogWindow(driver: ChromeDriver) {
        driver.get("$eaBaseUrl$eaGameList")
        originalWindow = driver.windowHandle
    }

    private fun getAllCatalogLinks(driver: ChromeDriver): List<String> {
        val allCatalogLinks = mutableListOf<String>()
        while (true) {
            Thread.sleep(Duration.ofSeconds(5))
            val currentCatalog = getCurrentCatalog(driver)
            allCatalogLinks += currentCatalog.keys
            return allCatalogLinks
            if (!goToNextPageIfAble(driver)) return allCatalogLinks
        }
    }

    private fun getCurrentCatalog(driver: ChromeDriver): Map<String, String> {
        val shadowRoot = driver.findElement(By.xpath("(//ea-box-set)[2]")).shadowRoot
        val catalogElements = shadowRoot.findElements(By.cssSelector("ea-game-box"))
        return catalogElements.associate {
            it.getAttribute("main-link-url") to it.getAttribute("background-image")
        }
    }

    private fun goToNextPageIfAble(driver: ChromeDriver): Boolean {
        val paginationShadowRoot = driver.findElement(
            By.xpath("(//ea-pagination[@slot='pagination'])")
        ).shadowRoot
        val nextButton = paginationShadowRoot.findElement(By.cssSelector("span[data-page-target='next']"))
        if (!nextButton.isEnabled) return false
        clickButton(driver, nextButton)
        Thread.sleep(Duration.ofSeconds(3))
        return true
    }

    private fun goToGameDetailPage(driver: ChromeDriver, path: String): Boolean {
        driver.get(path)
        Thread.sleep(Duration.ofSeconds(3))
        // TODO check for complications in path/on site
        return true
    }

    private fun getGameName(driver: ChromeDriver): String {
        val nameElement = driver.findElement(By.cssSelector("ea-hybrid-heading[class='header d4']"))
        return nameElement.text
    }

    private fun getGamePrice(driver: ChromeDriver): Double? {
        val priceShadowRoot = driver.findElement(By.xpath("(//ea-hybrid-price-badge[6])")).shadowRoot
        val priceElement = priceShadowRoot.findElement(By.className("text"))
        return priceElement.text.replace(Regex("[^\\d.]"), "").toDoubleOrNull()
    }

    private fun getGameDescription(driver: ChromeDriver): String {
        val descriptionSectionElement = driver.findElement(
            By.xpath(
                "(//ea-hybrid-condensed-table-header[contains(text(), 'Release date')]" +
                        "/following-sibling::ea-hybrid-condensed-table-content)"
            )
        )
        val descriptionElement = descriptionSectionElement.findElement(By.cssSelector("ea-hybrid-paragraph"))
        return descriptionElement.text
    }

    // TODO remove, not needed?
    private fun getGameImage(driver: ChromeDriver, imagePath: String): String {
        return imagePath
    }

    private fun getGameReleaseDate(driver: ChromeDriver): String {
        val releaseDateElement = driver.findElement(
            By.xpath(
                "(//ea-hybrid-condensed-table-header[contains(text(), 'Release date')]" +
                        "/following-sibling::ea-hybrid-condensed-table-content)"
            )
        )
        return releaseDateElement.text
    }

    private fun clickButton(driver: ChromeDriver, element: WebElement) {
        driver.executeScript("arguments[0].click()", element)
    }
}
//    @Value(value = "\${app.ea-games.base.url}")
//    lateinit var eaBaseUrl: String
//
//    @Value(value = "\${app.ea-games.game-list.url}")
//    lateinit var eaGameList: String
//
//
//    lateinit var originalWindow: String
//
//    override fun scrape(driver: ChromeDriver) {
//        getScrappedData(driver)
//    }
//
//    fun getScrappedData(driver: ChromeDriver) {
//        driver.get(eaBaseUrl + eaGameList)
//        originalWindow = driver.windowHandle
//        val pageCount =
//            driver.findElement(By.xpath("(//ea-pagination[@slot='pagination'])")).getAttribute("total-pages")
//                .toIntOrNull() ?: return
//        val allData = getAllCatalogData(driver, pageCount)
//        println(allData)
//        allData.forEach { scrapeGamePage(driver, it.first, it.second, it.third) }
//
//    }
//
//
//    fun getCurrentCatalogData(driver: ChromeDriver, previousGameTitle: String): List<Triple<String, String, String>> {
//        // wait till shadow root loads and correct data is shown
//        val eaBoxSetShadow = driver.findElement(By.xpath("(//ea-box-set)[2]")).shadowRoot
//        val gameBox = eaBoxSetShadow.findElements(By.cssSelector("ea-game-box"))
//        val test = mutableListOf<Triple<String, String, String>>()
//        for (element in 0 until gameBox.size) {
//            try { // TODO this shouldnt be needed
//                WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[element]) }
//                val title = gameBox[element].getAttribute("main-link-title")
//                WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[element]) }
//                val imageUrl = gameBox[element].getAttribute("background-image")
//                WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[element]) }
//                val url = gameBox[element].getAttribute("main-link-url")
//                test.add(Triple(title, imageUrl, url)) // TODO fix this
//            } catch (e: StaleElementReferenceException) {
//                val newElement = eaBoxSetShadow.findElement(By.cssSelector("ea-game-box[$element]"))
//                val title = newElement.getAttribute("main-link-title")
//                val imageUrl = newElement.getAttribute("background-image")
//                val url = newElement.getAttribute("main-link-url")
//                println(imageUrl)
//                test.add(Triple(title, imageUrl, url))
//            }
//        }
//        return test
////        WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[0]) }
////        val mappedGameBox = gameBox.map {
////            Triple(
////            it.getAttribute("main-link-title"),
////            it.getAttribute("background-image"),
////            it.getAttribute("main-link-url")
////            )
////        }//.filter { !it.first.contains("Sims") || !it.first.contains("Titanfall") } // TODO too dirty
////        mappedGameBox.forEach { scrapeGamePage(driver, it.first, it.second, it.third) }
////        return mappedGameBox
//    }
//
//    // check for other games in Ea, currently works for sw jedi survivor
//    fun scrapeGamePage(driver: ChromeDriver, name: String, imageUrl: String, path: String): Game? {
//        driver.switchTo().newWindow(WindowType.TAB)
//        // TODO add checking if buy option is available
//        driver.get("$eaBaseUrl$path")
//        val pcAppElements = driver.findElements(By.xpath("(//ea-cta[@type='standard'][@slot='cta'])"))
//        val aboutGameElements = driver.findElements(By.xpath("(//ea-hybrid-sub-nav-item[@link='#about-the-game'])"))
//        println(pcAppElements.map { it.getAttribute("link-url") })
//        val urlLinks = pcAppElements.mapNotNull { it.getAttribute("link-url") }
//        // TODO if url link doesnt have ends with pc but has ends with buy -> go to /buy check again
//        if (pcAppElements.mapNotNull { it.getAttribute("link-url") }
//                .none { it.endsWith("/pc") } && aboutGameElements.size == 0) {
//            println("Skipping game that isn't available in EA App.")
//            closeAndReturn(driver)
//            return null
//        }
//        if (aboutGameElements.size == 0) {
//            val currentUrl = driver.currentUrl
//            val base = currentUrl.split("?")[0] // remove any url params
//            driver.get("$base/buy/pc")
//        }
//        // TODO add check if price-badge visible -> also check if game free
//        val priceShadow = driver.findElement(By.xpath("(//ea-hybrid-price-badge)[6]")).shadowRoot
//        // wait until price is readable, is this "clean"
//        WebDriverWait(
//            driver,
//            Duration.ofSeconds(3)
//        ).until { driver.findElement(By.xpath("(//ea-hybrid-price-badge)[6]")).shadowRoot.findElement(By.cssSelector(".text")).text != "--" }
//        val price = priceShadow.findElement(By.cssSelector(".text"))
//        // TODO release date isnt in all of the game buy pages
//        val releaseDateElement =
//            driver.findElement(By.xpath("(//ea-hybrid-condensed-table-header[contains(text(), 'Release date')]/following-sibling::ea-hybrid-condensed-table-content)"))
//        val descriptionSection =
//            driver.findElement(By.xpath("(//ea-text[@slot='about-section'])[2]")) // consider lopping through these ea-texts
//        val descriptionElement = descriptionSection.findElement(By.cssSelector("ea-hybrid-paragraph"))
//        val gamePrice = price.text // TODO format the text
//        val releaseDate = releaseDateElement.text // TODO format the text
//        val description = descriptionElement.text // TODO format text?
//        println("Game name: $name")
//        println("Game price: $gamePrice")
//        println("Game image url: $imageUrl")
//        println("Game release date: $releaseDate")
//        println("Game description: $description")
//        closeAndReturn(driver)
//        return Game(name, description, imageUrl, "")
//    }
//
//    private fun closeAndReturn(driver: ChromeDriver) {
//        driver.close()
//        driver.switchTo().window(originalWindow)
//    }
//
//    // TODO add checks?
//    private fun getNextElementAndClick(driver: ChromeDriver) {
//        val paginationElement = driver.findElement(By.xpath("(//ea-pagination[@slot='pagination'])"))
//        val shadowPagination = paginationElement.shadowRoot
//        val nextElement = shadowPagination.findElement(By.cssSelector("span[data-page-target='next']"))
//        driver.executeScript("arguments[0].click()", nextElement)
//
//    }
//
//    private fun getAllCatalogData(driver: ChromeDriver, pageCount: Int): Set<Triple<String, String, String>> {
//        val allCatalogData: MutableSet<Triple<String, String, String>> = mutableSetOf()
//        var previousGameTitle = ""
//        for (page in 1..pageCount) {
//            val newData = getCurrentCatalogData(driver, previousGameTitle)
//            println("newData:")
//            println(newData)
//            allCatalogData += newData
//            println("allData:")
//            println(allCatalogData)
//            previousGameTitle = newData[0].first
//            getNextElementAndClick(driver)
////            Thread.sleep(3000) // TODO fix with using webdriverwait
//            // attributeToBe/stalenesOf
//        }
//        return allCatalogData
//    }
//}