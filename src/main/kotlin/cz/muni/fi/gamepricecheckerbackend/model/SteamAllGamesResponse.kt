package cz.muni.fi.gamepricecheckerbackend.model

import com.fasterxml.jackson.annotation.JsonAlias

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class SteamAllGamesResponse(
    @JsonAlias("applist", "appList")
    val appList: SteamApps
)
