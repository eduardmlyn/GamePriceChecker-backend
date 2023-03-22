package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.client.SteamGameDetailClient
import cz.muni.fi.gamepricecheckerbackend.client.SteamGameListClient
import cz.muni.fi.gamepricecheckerbackend.util.scrapper.EAScrapper
import cz.muni.fi.gamepricecheckerbackend.util.scrapper.HumbleBundleScrapper
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
    val steamGameListClient: SteamGameListClient,
    val steamGameDetailClient: SteamGameDetailClient
) {

    @Scheduled(cron = "@daily")
    fun updateEAData() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            eaScrapper.scrapeGamePrices(webDriver)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
    }

    @Scheduled(cron = "@daily")
    fun updateHumbleBundleData() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            humbleBundleScrapper.scrapeGamePrices(webDriver)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
    }

    @Scheduled(cron = "@weekly")

    @Scheduled(cron = "@daily")
    fun updateSteamData() {
        val data = steamGameListClient.getAllGames()
        // TODO implement
    }
}