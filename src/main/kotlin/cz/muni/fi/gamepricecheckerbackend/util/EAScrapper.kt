package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.Game
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WindowType
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
class EAScrapper: Scrapper {
    @Value(value = "\${app.ea-games.base.url}")
    lateinit var eaBaseUrl: String

    @Value(value = "\${app.ea-games.game-list.url}")
    lateinit var eaGameList: String


    lateinit var originalWindow: String

    override fun scrape(driver: ChromeDriver): List<Game> {
        getScrappedData(driver)
        return listOf()
    }

    fun getScrappedData(driver: ChromeDriver) {
        driver.get(eaBaseUrl + eaGameList)
        originalWindow = driver.windowHandle
//        println(driver.findElement(By.xpath("//*")).getAttribute("innerHTML"))
//        WebDriverWait(
//            driver,
//            Duration.ofSeconds(3)
//        ).until { ExpectedConditions.visibilityOf(driver.findElement(By.xpath("(//ea-pagination[@slot='pagination'])"))) }
        val pageCount =
            driver.findElement(By.xpath("(//ea-pagination[@slot='pagination'])")).getAttribute("total-pages")
                .toIntOrNull() ?: return
        val allData = getAllCatalogData(driver, pageCount)
        println(allData)
        allData.forEach { scrapeGamePage(driver, it.first, it.second, it.third) }

    }


    fun getCurrentCatalogData(driver: ChromeDriver, previousGameTitle: String): List<Triple<String, String, String>> {
        // wait till shadow root loads and correct data is shown
        val eaBoxSetShadow = driver.findElement(By.xpath("(//ea-box-set)[2]")).shadowRoot
        waitForCatalogLoad(driver, eaBoxSetShadow, previousGameTitle)
        val gameBox = eaBoxSetShadow.findElements(By.cssSelector("ea-game-box"))
        val test = mutableListOf<Triple<String, String, String>>()
        for (element in 0 until gameBox.size) {
            try { // TODO this shouldnt be needed
                WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[element]) }
                val title = gameBox[element].getAttribute("main-link-title")
                WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[element]) }
                val imageUrl = gameBox[element].getAttribute("background-image")
                WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[element]) }
                val url = gameBox[element].getAttribute("main-link-url")
                test.add(Triple(title, imageUrl, url)) // TODO fix this
            } catch (e: StaleElementReferenceException) {
                val newElement = eaBoxSetShadow.findElement(By.cssSelector("ea-game-box[$element]"))
                val title = newElement.getAttribute("main-link-title")
                val imageUrl = newElement.getAttribute("background-image")
                val url = newElement.getAttribute("main-link-url")
                println(imageUrl)
                test.add(Triple(title, imageUrl, url))
            }
        }
        return test
//        WebDriverWait(driver, Duration.ofSeconds(5)).until { ExpectedConditions.stalenessOf(gameBox[0]) }
//        val mappedGameBox = gameBox.map {
//            Triple(
//            it.getAttribute("main-link-title"),
//            it.getAttribute("background-image"),
//            it.getAttribute("main-link-url")
//            )
//        }//.filter { !it.first.contains("Sims") || !it.first.contains("Titanfall") } // TODO too dirty
//        mappedGameBox.forEach { scrapeGamePage(driver, it.first, it.second, it.third) }
//        return mappedGameBox
    }

    // check for other games in Ea, currently works for sw jedi survivor
    fun scrapeGamePage(driver: ChromeDriver, name: String, imageUrl: String, path: String): Game? {
        driver.switchTo().newWindow(WindowType.TAB)
        // TODO add checking if buy option is available
        driver.get("$eaBaseUrl$path")
        val pcAppElements = driver.findElements(By.xpath("(//ea-cta[@type='standard'][@slot='cta'])"))
        val aboutGameElements = driver.findElements(By.xpath("(//ea-hybrid-sub-nav-item[@link='#about-the-game'])"))
        println(pcAppElements.map { it.getAttribute("link-url") })
        val urlLinks = pcAppElements.mapNotNull { it.getAttribute("link-url") }
        // TODO if url link doesnt have ends with pc but has ends with buy -> go to /buy check again
        if (pcAppElements.mapNotNull { it.getAttribute("link-url") }
                .none { it.endsWith("/pc") } && aboutGameElements.size == 0) {
            println("Skipping game that isn't available in EA App.")
            closeAndReturn(driver)
            return null
        }
        if (aboutGameElements.size == 0) {
            val currentUrl = driver.currentUrl
            val base = currentUrl.split("?")[0] // remove any url params
            driver.get("$base/buy/pc")
        }
        // TODO add check if price-badge visible -> also check if game free
        val priceShadow = driver.findElement(By.xpath("(//ea-hybrid-price-badge)[6]")).shadowRoot
        // wait until price is readable, is this "clean"
        WebDriverWait(
            driver,
            Duration.ofSeconds(3)
        ).until { driver.findElement(By.xpath("(//ea-hybrid-price-badge)[6]")).shadowRoot.findElement(By.cssSelector(".text")).text != "--" }
        val price = priceShadow.findElement(By.cssSelector(".text"))
        // TODO release date isnt in all of the game buy pages
        val releaseDateElement =
            driver.findElement(By.xpath("(//ea-hybrid-condensed-table-header[contains(text(), 'Release date')]/following-sibling::ea-hybrid-condensed-table-content)"))
        val descriptionSection =
            driver.findElement(By.xpath("(//ea-text[@slot='about-section'])[2]")) // consider lopping through these ea-texts
        val descriptionElement = descriptionSection.findElement(By.cssSelector("ea-hybrid-paragraph"))
        val gamePrice = price.text // TODO format the text
        val releaseDate = releaseDateElement.text // TODO format the text
        val description = descriptionElement.text // TODO format text?
        println("Game name: $name")
        println("Game price: $gamePrice")
        println("Game image url: $imageUrl")
        println("Game release date: $releaseDate")
        println("Game description: $description")
        closeAndReturn(driver)
        return Game(name, description, imageUrl, "")
    }

    private fun closeAndReturn(driver: ChromeDriver) {
        driver.close()
        driver.switchTo().window(originalWindow)
    }

    // TODO add checks?
    private fun getNextElementAndClick(driver: ChromeDriver) {
        val paginationElement = driver.findElement(By.xpath("(//ea-pagination[@slot='pagination'])"))
        val shadowPagination = paginationElement.shadowRoot
        val nextElement = shadowPagination.findElement(By.cssSelector("span[data-page-target='next']"))
        driver.executeScript("arguments[0].click()", nextElement)

    }

    // TODO add checks?
    private fun waitForCatalogLoad(driver: ChromeDriver, shadowRoot: SearchContext, previousGameTitle: String) {
        val eaBoxSet =
            driver.findElement(By.xpath("(//ea-box-set)[2]")).shadowRoot.findElement(By.cssSelector("ea-game-box"))
        WebDriverWait(
            driver,
            Duration.ofSeconds(5)
        ).until { ExpectedConditions.refreshed(ExpectedConditions.stalenessOf(eaBoxSet)) }

        if (previousGameTitle != "") {
            println("here")
            WebDriverWait(driver, Duration.ofSeconds(5)).until {
                ExpectedConditions.refreshed(
                    ExpectedConditions.and(
                        ExpectedConditions.stalenessOf(eaBoxSet),
                        ExpectedConditions.not(
                            ExpectedConditions.attributeToBe(By.cssSelector("ea-game-box"), "main-link-title", previousGameTitle)
                        )
                    )
                )
            }
        }
        WebDriverWait(driver, Duration.ofSeconds(5)).until {
            ExpectedConditions.refreshed(
                ExpectedConditions.stalenessOf(
                    shadowRoot.findElement(By.cssSelector("ea-game-box"))
                )
            )
        }
    }

    private fun getAllCatalogData(driver: ChromeDriver, pageCount: Int): Set<Triple<String, String, String>> {
        val allCatalogData: MutableSet<Triple<String, String, String>> = mutableSetOf()
        var previousGameTitle = ""
        for (page in 1..pageCount) {
            val newData = getCurrentCatalogData(driver, previousGameTitle)
            println("newData:")
            println(newData)
            allCatalogData += newData
            println("allData:")
            println(allCatalogData)
            previousGameTitle = newData[0].first
            getNextElementAndClick(driver)
//            Thread.sleep(3000) // TODO fix with using webdriverwait
            // attributeToBe/stalenesOf
        }
        return allCatalogData
    }
}