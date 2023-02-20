package cz.muni.fi.gamepricecheckerbackend.model.steam

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class SteamGameDetailResponse(
    val success: Boolean,
    val data: SteamGameDetails
)