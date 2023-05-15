package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.util.scrapper.EAScrapper
import cz.muni.fi.gamepricecheckerbackend.util.scrapper.HumbleBundleScrapper
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class ScheduledGameUpdate(
    private val humbleBundleScrapper: HumbleBundleScrapper,
    private val eaScrapper: EAScrapper,
    private val chromeDriverFactory: ChromeDriverFactory,
    private val steamDataUpdater: SteamDataUpdater,
    private val taskDoneEventPublisher: TaskDoneEventPublisher,
    private val logger: Logger
) {

    @Scheduled(cron = "@daily")
    fun updateEaGameData() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            eaScrapper.scrapeGamePrices(webDriver)
            logger.info("Finished Ea data scraping")
        } catch (e: Exception) {
            logger.error(e.message)
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
            logger.info("Finished HB price scraping")
        } catch (e: Exception) {
            logger.error(e.message)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
            taskDoneEventPublisher.publishTaskDone(
                "Updating humble bundle game prices completed.",
                Seller.HUMBLE_BUNDLE
            )
        }
    }

    @Scheduled(cron = "@monthly")
    fun updateHumbleBundleGameData() {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            humbleBundleScrapper.scrapeGameDetails(webDriver)
            logger.info("Finished HB detail scraping")
        } catch (e: Exception) {
            logger.error(e.message)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
    }


    @Scheduled(cron = "@daily")
    fun updateSteamPrices() {
        try {
            steamDataUpdater.updateGamePrices()
            logger.info("Finished Steam price update")
        } catch (e: Exception) {
            logger.error(e.message)
        } finally {
            taskDoneEventPublisher.publishTaskDone("Updating steam game prices completed.", Seller.STEAM)
        }
    }

    @Scheduled(cron = "@monthly")
    fun updateSteamData() {
        try {
            steamDataUpdater.updateGameDetails()
            logger.info("Finished Steam detail update")
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

}
