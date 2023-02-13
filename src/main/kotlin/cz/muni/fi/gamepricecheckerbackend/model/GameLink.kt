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

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Entity
@Table(name = "gameLinks")
data class GameLink(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    @Column
    val link: String,
    @Column
    val price: Double,
    @Column
    val seller: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    val game: Game
) {
    constructor(): this("", "", 0.0, "", Game())
}
