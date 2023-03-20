package cz.muni.fi.gamepricecheckerbackend.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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
    @Column(nullable = false)
    val name: String,
    @Column(nullable = true, columnDefinition = "TEXT")
    val description: String?,
    @Column(nullable = true)
    val imageUrl: String?,
    @Column(nullable = true)
    val releaseDate: String?,
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val externalLinks: Set<GameSeller>,
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val history: List<PriceSnapshot>
) {
    constructor(): this("", "", "", "", "", emptySet(), emptyList())
    constructor(name: String): this("", name, null, null, null, emptySet(), emptyList())
    constructor(name: String, description: String, imageUrl: String, releaseDate: String?): this("", name, description, imageUrl, releaseDate, emptySet(), emptyList())
}
