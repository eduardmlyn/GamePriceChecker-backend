package cz.muni.fi.gamepricecheckerbackend.model.dto

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class GameDetailDTO(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val releaseDate: String?,
    val sellerLinks: Set<GameSellerDTO>,
    val history: List<PriceSnapshotDTO>
)
