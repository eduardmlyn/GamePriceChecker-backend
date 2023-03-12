package cz.muni.fi.gamepricecheckerbackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


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
    @Enumerated(EnumType.STRING)
    @Column
    val role: Role,
    @Column(name = "userName", unique = true)
    val userName: String,
    @Column
    private val password: String,
    @ManyToMany
    @JoinTable(
        name = "game_favorites",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "game_id")]
    )
    val favorites: List<Game>
) : UserDetails {
    constructor() : this("", Role.USER, "", "", emptyList<Game>())
    constructor(userName: String, password: String, role: Role) : this("", role, userName, password, emptyList())

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return userName
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
