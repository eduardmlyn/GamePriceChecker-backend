package cz.muni.fi.gamepricecheckerbackend.model.dto

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class GameDTO(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val releaseDate: String?
)
