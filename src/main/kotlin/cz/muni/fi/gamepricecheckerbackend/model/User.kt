package cz.muni.fi.gamepricecheckerbackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
    @Column(name = "userName", unique = true)
    val userName: String,
    // TODO implement password encoding
    @Column
    val password: String
) {
    constructor(): this("", "", "")
    constructor(userName: String, password: String): this("", userName, password)
    fun comparePassword(password: String): Boolean {
        return false
    }
}
