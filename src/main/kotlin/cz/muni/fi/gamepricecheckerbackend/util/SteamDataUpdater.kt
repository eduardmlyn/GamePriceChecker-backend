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
import org.springframework.stereotype.Component
import java.time.Duration

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class SteamDataUpdater(
    val steamGameListClient: SteamGameListClient,
    val steamGameDetailClient: SteamGameDetailClient,
    val gameService: GameService
) {

    private val seller = Seller.STEAM
    private val filter = "price_overview"
    private val countryCode = "CZ"
    private val objectMapper = jacksonObjectMapper()

    fun updateGamePrices() {
        val apps = steamGameListClient.getAllGames().appList.apps
        val appIds = apps.map { it.appId }
        var start = 0
        var end = 800 // seems to be the upper limit of the size of the request
        while (true) {
            if (start >= apps.size) return
            if (end >= apps.size) end = apps.size - 1
            val currentAppIds = appIds.subList(start, end)
            val prices = steamGameDetailClient.getGameDetails(currentAppIds, countryCode, filter = filter)
            prices.forEach { (key, value) ->
                savePrice(key, value, apps)
            }
            start = end
            end += 800
            simulatePause()
        }
    }

    fun updateGameDetails() {
        val apps = steamGameListClient.getAllGames().appList.apps
        val appIds = apps.map { it.appId }
        appIds.forEach { currentAppId ->
            val gameDetails = steamGameDetailClient.getGameDetails(listOf(currentAppId), countryCode, filter = null)
            val key = gameDetails.keys.single()
            val value = gameDetails[key]!!
            saveDetails(key, value, apps)
        }
    }

    private fun simulatePause() {
        Thread.sleep(Duration.ofSeconds(5))
    }

    private fun savePrice(key: String, value: SteamGameDetailResponse, apps: List<SteamGame>) {
        val appId = checkSteamResponse(key, value) ?: return
        val jsonValue = objectMapper.writeValueAsString(value.data)
        val steamPriceOverview = objectMapper.readValue(jsonValue, SteamPriceOverview::class.java)
        val gameName = apps.find { it.appId == appId }!!.name // should be fine right?
        val priceInt = steamPriceOverview.priceOverview.final ?: return
        val price = priceInt.toDouble() / 100
        gameService.saveGamePrice(gameName, price, null, seller)
    }

    private fun saveDetails(key: String, value: SteamGameDetailResponse, apps: List<SteamGame>) {
        val appId = checkSteamResponse(key, value) ?: return
        val jsonValue = objectMapper.writeValueAsString(value.data)
        val steamGameDetails = objectMapper.readValue(jsonValue, SteamGameDetails::class.java)
        val gameName = apps.find { it.appId == appId }!!.name
        val description = steamGameDetails.aboutTheGame?.replace(Regex("<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>"), "")
        val image = steamGameDetails.headerImage
        val releaseDate = steamGameDetails.releaseDate?.date
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