package cz.muni.fi.gamepricecheckerbackend.util.scrapper

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.ParseException
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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

    private val seller = Seller.EA_GAMES

    private val formatterSimple = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    private val formatterComplex = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a 'GMT'Z") // TODO test this

    override fun scrapeGamePrices(driver: ChromeDriver) {
        setCatalogWindow(driver)
        simulateUserBehaviour()
        val allLinks = getAllCatalogLinks(driver)
        allLinks.forEach { // TODO rewrite?
            if (goToDetailPageIfAble(driver, it)) {
                extractAndSaveGameData(driver)
            } else {
                println("Skipping game...") // TODO change to logging
            }
        }
    }

    private fun extractAndSaveGameData(driver: ChromeDriver) {
        simulateUserBehaviour()
        val name = getGameName(driver)
        if (name == null) {
            println("Skipping game... cannot find game name") // TODO change to log
            return
        }
        val description = getGameDescription(driver)
        val releaseDateString = getReleaseDate(driver)
        var releaseDate: Date? = null
        try {
            if (releaseDateString != null) {
                val localDate = LocalDate.parse(releaseDateString, formatterSimple)
                releaseDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            }
        } catch (e: ParseException) {
            val localDate = LocalDate.parse(releaseDateString, formatterComplex)
            releaseDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
        val currentUrl = driver.currentUrl
        val price = getGamePrice(driver)
        if (price != null) {
            println("Saving object to database")
            gameService.saveGame(name, price, description, null, releaseDate, currentUrl, seller)
        }
    }

    private fun setCatalogWindow(driver: ChromeDriver) {
        driver.get("$eaBaseUrl$eaGameList")
    }

    private fun getAllCatalogLinks(driver: ChromeDriver): List<String> {
        val allCatalogLinks = mutableListOf<String>()
        while (true) {
            simulateUserBehaviour()
            val currentCatalog = getCatalogLinks(driver)
            allCatalogLinks += currentCatalog
            if (!goToNextPageIfAble(driver)) return allCatalogLinks
        }
    }

    private fun getCatalogLinks(driver: ChromeDriver): List<String> {
        waitForElements(driver, By.xpath("(//ea-box-set)[2]"))
        val shadowRoot = driver.findElement(By.xpath("(//ea-box-set)[2]")).shadowRoot
        val catalogElements = shadowRoot.findElements(By.cssSelector("ea-game-box"))
        return catalogElements.map {
            "$eaBaseUrl${it.getAttribute("main-link-url")}"
        }
    }

    private fun goToNextPageIfAble(driver: ChromeDriver): Boolean {
        val paginationElement = driver.findElement(
            By.xpath("(//ea-pagination[@slot='pagination'])")
        )
        val currentPage = paginationElement.getAttribute("current-page").toIntOrNull()
        val totalPages = paginationElement.getAttribute("total-pages").toIntOrNull()
        if (currentPage == null || totalPages == null) return false // TODO fix this
        if (currentPage >= totalPages) return false
        val paginationShadowRoot = paginationElement.shadowRoot
        val nextButton = paginationShadowRoot.findElement(By.cssSelector("span[data-page-target='next']"))
        clickButton(driver, nextButton)
        simulateUserBehaviour()
        return true
    }

    private fun goToDetailPageIfAble(driver: ChromeDriver, path: String): Boolean {
//        try { // TODO is this needed everywhere to catch non-accessible sites?
            driver.get(path)
//        } catch (e: Exception) {
//            return false
//        }
        simulateUserBehaviour()
        if (hasAboutGame(driver)) return true
        return changeToPcOption(driver)
    }

    private fun getGameName(driver: ChromeDriver): String? {
        simulateUserBehaviour()
        val gameName = driver.findElements(By.cssSelector(".header.d4"))
        if (gameName.isEmpty()) return null
        return gameName[0].text
    }

    private fun getGameDescription(driver: ChromeDriver): String? {
        val gameDescription =
            driver.findElements(By.cssSelector("ea-text[type='full-bleed'] ea-hybrid-paragraph[color='dark']"))
        if (gameDescription.isEmpty()) return null
        return gameDescription[0].text
    }

    // TODO refactor
    private fun getGamePrice(driver: ChromeDriver): Double? {
        val gamePrice =
            driver.findElements(By.cssSelector("ea-hybrid-price-query[query-type='gamePrice'] ea-hybrid-price-badge"))
        if (gamePrice.isNotEmpty()) {
            val priceString = gamePrice[0].getAttribute("text")
            if (priceString == null || priceString == "") return null
            return priceString.replace(Regex("[^\\d.]"), "").toDoubleOrNull()
        }
        val priceDisplayElements =driver.findElements(By.cssSelector("ea-hybrid-price-display"))
        if (priceDisplayElements.isEmpty()) return null // -> might indicate the game is free
        val priceDisplayShadowRoot = priceDisplayElements[0].shadowRoot
        val oldGamePrice = priceDisplayShadowRoot.findElements(By.cssSelector("div[class='price-text']"))
        if (oldGamePrice.isEmpty()) return null
        var priceString = oldGamePrice[0].text
        if (priceString == null || priceString == "") return null
        for (i in 1..3) { // TODO might need to click buttons to get the data of price -> for now ignore
            if (priceString == "--") { // TODO extract to another method
                simulateUserBehaviour()
                val oldGamePriceRefreshed =
                    priceDisplayShadowRoot.findElement(By.cssSelector("div[class='price-text']"))
                priceString = oldGamePriceRefreshed.text
            } // Somehow indicate that the price couldn't be fetched
        }
        // replacing ',' with '.' to properly get the game price
        priceString = priceString.replace(',', '.')
        println("price string: $priceString")
        return priceString.replace(Regex("[^\\d.]"), "").toDoubleOrNull()
    }

    // TODO get Image
    private fun getImage(driver: ChromeDriver): String? {
//        https://media.contentapi.ea.com/content/dam/gin/images/2017/01/ea-header-small-gneric-game-4k.jpg.adapt.crop1x1.767p.jpg
//        https://media.contentapi.ea.com/content/dam/gin/images/2017/01/ea-header-small-gneric-game-4k.jpg.adapt.crop1x1.767p.jpg
        return null
    }

    private fun getReleaseDate(driver: ChromeDriver): String? {
        val releaseDateElements = driver.findElements(
            By.xpath("//ea-hybrid-condensed-table-header[normalize-space()='Release date']//following::ea-hybrid-condensed-table-content")
        )
        if (releaseDateElements.isEmpty()) return null
        println(releaseDateElements[0].text)
        return releaseDateElements[0].text
    }

    private fun hasAboutGame(driver: ChromeDriver): Boolean {
        val aboutGameElements = driver.findElements(By.cssSelector("ea-hybrid-sub-nav-item[link='#about-the-game']"))
        return aboutGameElements.isNotEmpty()
    }

    private fun changeToPcOption(driver: ChromeDriver): Boolean {
        val pcOptionUrl = driver.currentUrl.split("?")[0] + "/buy/pc"
        driver.get(pcOptionUrl)
        simulateUserBehaviour()
        val title = driver.title
        if (title.contains("404")) return false // add check for 500?
        return true
    }
}
