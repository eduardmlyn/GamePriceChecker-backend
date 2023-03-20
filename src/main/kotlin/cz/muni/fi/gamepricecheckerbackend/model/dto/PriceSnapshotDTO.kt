package cz.muni.fi.gamepricecheckerbackend.model.dto

import java.time.Instant

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class PriceSnapshotDTO(
    val averagePrice: Double,
    val minimumPrice: Double,
    val date: Instant
)
