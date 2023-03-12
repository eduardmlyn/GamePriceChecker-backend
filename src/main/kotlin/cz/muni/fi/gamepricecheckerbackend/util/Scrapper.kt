package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.Game
import org.openqa.selenium.chrome.ChromeDriver

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface Scrapper {
    fun scrape(driver: ChromeDriver): List<Game>
}