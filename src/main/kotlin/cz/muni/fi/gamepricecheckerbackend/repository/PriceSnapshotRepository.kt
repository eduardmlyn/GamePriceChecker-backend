package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.PriceSnapshot
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface PriceSnapshotRepository : JpaRepository<PriceSnapshot, String> {
    fun findPriceSnapshotsByGameId(gameId: String): List<PriceSnapshot>
}
