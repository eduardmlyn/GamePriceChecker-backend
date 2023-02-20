package cz.muni.fi.gamepricecheckerbackend.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Entity
@Table(name = "games")
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    @Column
    val name: String,
    @Column
    val description: String,
    @Column
    val imageUrl: String,
    @Column
    val releaseDate: String,
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val externalLinks: Set<GameLink>,
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val history: List<PriceSnapshot>,
    @ManyToMany(mappedBy = "favorites")
    val users: List<User>
) {
    constructor(): this("", "", "", "", "", emptySet(), emptyList(), emptyList())
}
