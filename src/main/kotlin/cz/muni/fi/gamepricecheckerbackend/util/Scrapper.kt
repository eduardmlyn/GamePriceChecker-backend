package cz.muni.fi.gamepricecheckerbackend.util

import org.openqa.selenium.chrome.ChromeDriver

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface Scrapper {
    fun scrape(driver: ChromeDriver)
}