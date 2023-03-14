package cz.muni.fi.gamepricecheckerbackend.util

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class ScheduledGameUpdate(
    val humbleBundleScrapper: HumbleBundleScrapper,
    val eaScrapper: EAScrapper,
    val chromeDriverFactory: ChromeDriverFactory
) {

    @Scheduled(cron = "@daily")
    fun updateGameInformation() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        humbleBundleScrapper.scrape(webDriver)
        eaScrapper.scrape(webDriver)
        chromeDriverFactory.destroyChromeDriverInstance()
        // TODO add steam api calls -> also needs timing
        // as well as saving the data that is scraped/returned by steam
        // saving/updating
    }
}