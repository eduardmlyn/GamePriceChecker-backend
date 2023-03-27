package cz.muni.fi.gamepricecheckerbackend.util.scrapper

import org.openqa.selenium.chrome.ChromeDriver

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface Scrapper {
    fun scrapeGamePrices(driver: ChromeDriver)
}