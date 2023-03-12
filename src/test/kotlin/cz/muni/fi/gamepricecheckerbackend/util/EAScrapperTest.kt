package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.BaseIntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Autowired

class EAScrapperTest : BaseIntegrationTest() {

    @Autowired
    lateinit var EAScrapper: EAScrapper

    @Autowired
    lateinit var webDriver: ChromeDriver

    @Test
    fun scrape() {
        val eaGames = EAScrapper.scrape(webDriver)

        Assertions.assertEquals(eaGames.size, 1)

    }
}