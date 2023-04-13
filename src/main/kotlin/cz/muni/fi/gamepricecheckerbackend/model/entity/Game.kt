package cz.muni.fi.gamepricecheckerbackend.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.Date

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Entity
@DynamicUpdate
@Table(name = "games", indexes = [Index(name = "name_index", columnList = "name", unique = true)])
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String?,
    @Column(nullable = true)
    var imageUrl: String?,
    @Column(nullable = true)
    var releaseDate: Date?,
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val gameSellers: Set<GameSeller>,
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val history: List<PriceSnapshot>,
//    @ManyToMany(mappedBy = "favorites")
//    val users: MutableList<User>
) {
    constructor(): this("", "", "", "", null, emptySet(), emptyList())
    constructor(name: String): this("", name, null, null, null, emptySet(), emptyList())
    constructor(name: String, description: String?, imageUrl: String?, releaseDate: Date?): this("", name, description, imageUrl, releaseDate, emptySet(), emptyList())
}
