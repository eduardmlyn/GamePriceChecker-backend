package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
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
    val steamDataUpdater: SteamDataUpdater,
    val taskDoneEventPublisher: TaskDoneEventPublisher
) {
//    uncomment for testing methods
    fun init() {
//        updateSteamData()
        updateSteamPrices()
        updateEaGameData()
        updateHumbleBundleGamePrices()
//        updateHumbleBundleGameData()
    }

    @Scheduled(cron = "@daily")
    fun updateEaGameData() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            eaScrapper.scrapeGamePrices(webDriver)
        } catch (e: Exception) {
            println(e)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
            taskDoneEventPublisher.publishTaskDone("Updating ea game prices completed.", Seller.EA_GAMES)
        }
    }

    @Scheduled(cron = "@daily")
    fun updateHumbleBundleGamePrices() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            humbleBundleScrapper.scrapeGamePrices(webDriver)
        } catch (e: Exception) {
            println(e)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
            taskDoneEventPublisher.publishTaskDone("Updating humble bundle game prices completed.", Seller.HUMBLE_BUNDLE)
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
        try {
            steamDataUpdater.updateGamePrices()
        } catch (e: Exception) {
            println(e)
        } finally {
            taskDoneEventPublisher.publishTaskDone("Updating steam game prices completed.", Seller.STEAM)
        }
    }

    // might be needed to make this monthly, depends on the time of the execution
    @Scheduled(cron = "@weekly")
    fun updateSteamData() {
        steamDataUpdater.updateGameDetails()
    }
}