package cz.muni.fi.gamepricecheckerbackend.model

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class AuthenticationRequest(
    val password: String,
    val username: String
)