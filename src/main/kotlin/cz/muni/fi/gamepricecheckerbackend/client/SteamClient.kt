package cz.muni.fi.gamepricecheckerbackend.client

import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamAllGamesResponse
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamGameDetailResponse
import org.springframework.cloud.openfeign.CollectionFormat
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * Steam Feign client for getting all games
 * they sell on their store.
 *
 * @see <a href="https://partner.steamgames.com/doc/webapi/ISteamApps">Detailed documentation</a>
 *
 * @author Eduard Stefan Mlynarik
 */
@FeignClient(name = "SteamListAPI", url = "\${app.steam-api.game-list.url}")
interface SteamGameListClient {
    @GetMapping("/ISteamApps/GetAppList/v2")
    fun getAllGames(): SteamAllGamesResponse
}

/**
 * Steam Feign client for getting details of a game.
 *
 * @see <a href="https://wiki.teamfortress.com/wiki/User:RJackson/StorefrontAPI">Detailed documentation</a>
 *
 * @author Eduard Stefan Mlynarik
 */
@FeignClient(name = "SteamDetailAPI", url = "\${app.steam-api.game-detail.url}")
interface SteamGameDetailClient {
    @GetMapping("/appdetails")
    @CollectionFormat(feign.CollectionFormat.CSV)
    fun getGameDetails(
        @RequestParam(value = "appids") appId: List<Int>,
        @RequestParam(value = "cc") countryCode: String,
        @RequestParam(value = "format") format: String = "json",
        @RequestParam(value = "filters") filter: String?
    ): Map<String, SteamGameDetailResponse>
}