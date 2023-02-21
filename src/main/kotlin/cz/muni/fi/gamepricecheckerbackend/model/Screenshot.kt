package cz.muni.fi.gamepricecheckerbackend.model

import com.fasterxml.jackson.annotation.JsonAlias

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class Screenshot(
    val id: Int,
    @JsonAlias("path_thumbnail", "pathThumbnail")
    val pathThumbnail: String,
    @JsonAlias("path_full", "pathFull")
    val pathFull: String
)