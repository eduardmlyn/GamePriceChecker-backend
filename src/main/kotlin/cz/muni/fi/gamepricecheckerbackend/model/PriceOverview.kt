package cz.muni.fi.gamepricecheckerbackend.model

import com.fasterxml.jackson.annotation.JsonAlias

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class PriceOverview(
    val currency: String,
    val initial: Int,
    val final: Int,
    @JsonAlias("discount_percent", "discountPercent")
    val discountPercent: Int,
    @JsonAlias("initial_formatted")
    val initialFormatted: String,
    @JsonAlias("final_formatted", "finalFormatted")
    val finalFormatted: String
)
