package cz.muni.fi.gamepricecheckerbackend.model.steam

import com.fasterxml.jackson.annotation.JsonAlias

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class SteamPriceOverview(
    @JsonAlias("price_overview", "priceOverview")
    val priceOverview: PriceOverview
)
