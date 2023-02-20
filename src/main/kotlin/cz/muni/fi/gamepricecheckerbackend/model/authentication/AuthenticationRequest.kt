package cz.muni.fi.gamepricecheckerbackend.model.authentication

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class AuthenticationRequest(
    val password: String,
    val username: String
)