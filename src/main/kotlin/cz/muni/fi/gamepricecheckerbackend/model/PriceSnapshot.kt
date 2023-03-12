package cz.muni.fi.gamepricecheckerbackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Entity
@Table(name = "price_snapshot")
data class PriceSnapshot(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    val game: Game,
    @Column
    val averagePrice: Double,
    @Column
    val minimumPrice: Double,
    @Column
    val date: Instant
) {
    constructor(): this("", Game(),0.0, 0.0, Instant.now())
}
