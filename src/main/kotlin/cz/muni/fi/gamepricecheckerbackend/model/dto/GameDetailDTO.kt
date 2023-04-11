package cz.muni.fi.gamepricecheckerbackend.model.dto

import java.util.*

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class GameDetailDTO(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val releaseDate: Date?,
    val sellerLinks: Set<GameSellerDTO>,
    val history: List<PriceSnapshotDTO>
)
