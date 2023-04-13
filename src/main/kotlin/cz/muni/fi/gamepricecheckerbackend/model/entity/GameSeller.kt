package cz.muni.fi.gamepricecheckerbackend.model.entity

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Entity
@DynamicUpdate
@Table(name = "game_seller")
data class GameSeller(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    @Column(nullable = true, columnDefinition = "TEXT")
    var link: String?,
    @Column
    var price: Double,
    @Column
    val seller: Seller,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    val game: Game
) {
    constructor(seller: Seller, game: Game) : this("", null, 0.0, seller, game)
    constructor(link: String?, price: Double, seller: Seller, game: Game) : this("", link, price, seller, game)
}
