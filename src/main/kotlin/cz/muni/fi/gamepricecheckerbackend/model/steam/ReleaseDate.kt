package cz.muni.fi.gamepricecheckerbackend.model.steam

import com.fasterxml.jackson.annotation.JsonAlias

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class ReleaseDate(
    @JsonAlias("coming_soon", "comingSoon")
    val comingSoon: String,
    val date: String
)