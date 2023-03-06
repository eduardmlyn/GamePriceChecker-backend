package cz.muni.fi.gamepricecheckerbackend.model.steam

import com.fasterxml.jackson.annotation.JsonAlias

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class SteamGameDetails(
    val type: String, // check for "game"
    val name: String,
    @JsonAlias("is_free", "isFree")
    val isFree: Boolean,
    // TODO choose from the 3 descriptions
    @JsonAlias("detailed_description", "detailedDescription")
    val detailedDescription: String,
    @JsonAlias("about_the_game", "aboutTheGame")
    val aboutTheGame: String,
    @JsonAlias("short_description", "shortDescription")
    val shortDescription: String,
    @JsonAlias("header_image", "headerImage")
    val headerImage: String?, // can be null?
    // add categories?/genres?
    // val categories: List<Any> // { id: .., description: ..} -> both categories and genres
    val screenshots: List<Screenshot>,
    @JsonAlias("release_date", "releaseDate")
    val releaseDate: ReleaseDate,
    @JsonAlias("price_overview", "priceOverview")
    val priceOverview: PriceOverview?
)
