package cz.muni.fi.gamepricecheckerbackend.client

import cz.muni.fi.gamepricecheckerbackend.model.SteamAllGamesResponse
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

/**
 *
 * @author Eduard Stefan Mlynarik
 */
// TODO create feign client to steam api
@FeignClient(name = "SteamAPI", url = "\${app.steam-api.url}")
interface SteamClient {
    @GetMapping("ISteamApps/GetAppList/v2")
    fun getAllGames(): SteamAllGamesResponse
}