package cz.muni.fi.gamepricecheckerbackend.client

import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamAllGamesResponse
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamGameDetailResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 *
 * @author Eduard Stefan Mlynarik
 */
// TODO create feign client to steam api
@FeignClient(name = "SteamListAPI", url = "\${app.steam-api.game-list.url}")
interface SteamGameListClient {
    @GetMapping("/ISteamApps/GetAppList/v2")
    fun getAllGames(): SteamAllGamesResponse
}

@FeignClient(name = "SteamDetailAPI", url = "\${app.steam-api.game-detail.url}")
interface SteamGameDetailClient {
    @GetMapping("/appdetails")
    fun getGameDetails(
        @RequestParam(value = "appids") appId: Int,
        @RequestParam(value = "cc") countryCode: String,
        @RequestParam(value = "format") format: String = "json"
    ): Map<String, SteamGameDetailResponse>
}