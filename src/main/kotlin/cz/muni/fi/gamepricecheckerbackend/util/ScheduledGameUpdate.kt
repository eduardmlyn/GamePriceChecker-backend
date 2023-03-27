package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.util.scrapper.EAScrapper
import cz.muni.fi.gamepricecheckerbackend.util.scrapper.HumbleBundleScrapper
import jakarta.annotation.PostConstruct
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
    val chromeDriverFactory: ChromeDriverFactory,
    val steamDataUpdater: SteamDataUpdater
) {
//    uncomment for testing methods
//    @PostConstruct
//    fun init() {
//        updateSteamData()
//        updateSteamPrices()
//        updateEaGameData()
//        updateHumbleBundleGamePrices()
//        updateHumbleBundleGameData()
//    }

    @Scheduled(cron = "@daily")
    fun updateEaGameData() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            eaScrapper.scrapeGamePrices(webDriver)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
    }

    @Scheduled(cron = "@daily")
    fun updateHumbleBundleGamePrices() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            humbleBundleScrapper.scrapeGamePrices(webDriver)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
    }

    @Scheduled(cron = "@weekly")
    fun updateHumbleBundleGameData() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            humbleBundleScrapper.scrapeGameDetails(webDriver)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
    }

    @Scheduled(cron = "@daily")
    fun updateSteamPrices() {
        steamDataUpdater.updateGamePrices()
    }

    // might be needed to make this monthly, depends on the time of the execution
    @Scheduled(cron = "@weekly")
    fun updateSteamData() {
        steamDataUpdater.updateGameDetails()
    }
}