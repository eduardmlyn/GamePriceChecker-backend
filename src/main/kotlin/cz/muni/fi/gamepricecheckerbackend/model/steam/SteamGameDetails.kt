package cz.muni.fi.gamepricecheckerbackend.model.steam

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamGameDetails(
    val name: String?,
    @JsonAlias("is_free", "isFree")
    val isFree: Boolean?,
    @JsonAlias("about_the_game", "aboutTheGame")
    val aboutTheGame: String?,
    @JsonAlias("header_image", "headerImage")
    val headerImage: String?,
    @JsonAlias("release_date", "releaseDate")
    val releaseDate: ReleaseDate?,
    @JsonAlias("price_overview", "priceOverview")
    val priceOverview: PriceOverview?
)
