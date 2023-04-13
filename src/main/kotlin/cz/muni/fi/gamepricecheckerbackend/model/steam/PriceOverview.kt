package cz.muni.fi.gamepricecheckerbackend.model.steam

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PriceOverview(
    val final: Int?,
)
