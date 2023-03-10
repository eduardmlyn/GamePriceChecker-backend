package cz.muni.fi.gamepricecheckerbackend.model.steam

import com.fasterxml.jackson.annotation.JsonAlias

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class SteamGame(
    @JsonAlias("appid", "appId")
    val appId: Int,
    val name: String
)