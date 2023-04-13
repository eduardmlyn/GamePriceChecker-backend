package cz.muni.fi.gamepricecheckerbackend.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.muni.fi.gamepricecheckerbackend.client.SteamGameDetailClient
import cz.muni.fi.gamepricecheckerbackend.client.SteamGameListClient
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamGame
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamGameDetailResponse
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamGameDetails
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamPriceOverview
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.text.ParseException
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.min

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class SteamDataUpdater(
    private val steamGameListClient: SteamGameListClient,
    private val steamGameDetailClient: SteamGameDetailClient,
    private val gameService: GameService,
    private val dateParser: DateParser,
    private val logger: Logger
) {

    private val seller = Seller.STEAM
    private val filter = "price_overview"
    private val countryCode = "CZ"
    private val steamLinkBase = "https://store.steampowered.com/search/?term="
    private val objectMapper = jacksonObjectMapper()

    fun updateGamePrices() {
        val apps = steamGameListClient.getAllGames().appList.apps
        val appIds = apps.map { it.appId }
        var start = 0
        var end = 700
        while (true) {
            try {
                if (start >= apps.size - 1) return
                logger.info("Getting apps from $start to $end meaning ${end / 700} iteration")
                val currentAppIds = appIds.subList(start, end)
                val prices = steamGameDetailClient.getGameDetails(currentAppIds, countryCode, filter = filter)
                prices.forEach { (key, value) ->
                    savePrice(key, value, apps)
                }
                start = end
                end = min(end + 700, apps.size - 1)
                simulatePause()
            } catch (e: Exception) {
                logger.error("An error with message: ${e.message} at the ${end / 700} iteration")
            }
        }
    }

    fun updateGameDetails() {
        val apps = steamGameListClient.getAllGames().appList.apps
        val gamesToUpdate = gameService.getUpdatableGamesForSeller(seller).map { it.first.name }
        val appIds = apps.filter { it.name in gamesToUpdate }.map { it.appId }

        appIds.forEach { currentAppId ->
            try {
                val gameDetails = steamGameDetailClient.getGameDetails(listOf(currentAppId), countryCode, filter = null)
                val key = gameDetails.keys.single()
                val value = gameDetails[key]!!
                saveDetails(key, value, apps)
                simulatePause()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    private fun simulatePause() {
        Thread.sleep(Duration.ofSeconds(3).toMillis())
    }

    private fun savePrice(key: String, value: SteamGameDetailResponse, apps: List<SteamGame>) {
        val appId = checkSteamResponse(key, value) ?: return
        val jsonValue = objectMapper.writeValueAsString(value.data)
        val steamPriceOverview = objectMapper.readValue(jsonValue, SteamPriceOverview::class.java)
        val gameName = apps.find { it.appId == appId }!!.name
        val priceInt = steamPriceOverview.priceOverview.final ?: return
        val price = priceInt.toDouble() / 100
        gameService.saveGamePrice(gameName, price, "$steamLinkBase$gameName", seller)
    }

    private fun saveDetails(key: String, value: SteamGameDetailResponse, apps: List<SteamGame>) {
        val appId = checkSteamResponse(key, value) ?: return
        val jsonValue = objectMapper.writeValueAsString(value.data)
        val steamGameDetails = objectMapper.readValue(jsonValue, SteamGameDetails::class.java)
        val gameName = apps.find { it.appId == appId }!!.name
        val description =
            steamGameDetails.aboutTheGame?.replace(Regex("<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>"), "")
        val image = steamGameDetails.headerImage
        val releaseDateString = steamGameDetails.releaseDate?.date
        val releaseDate: Date? = dateParser.parseDate(releaseDateString)
        val price: Double = if (steamGameDetails.isFree == true) {
            0.0
        } else {
            steamGameDetails.priceOverview?.final?.toDouble()?.div(100) ?: return
        }
        gameService.saveGame(gameName, price, description, image, releaseDate, null, seller)
        simulatePause()
    }

    private fun checkSteamResponse(key: String, value: SteamGameDetailResponse): Int? {
        if (!value.success) return null
        if (value.data == null) return null
        if (value.data is List<*>) return null
        return key.toIntOrNull()
    }
}
