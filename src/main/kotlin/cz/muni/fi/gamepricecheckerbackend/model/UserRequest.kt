package cz.muni.fi.gamepricecheckerbackend.model

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Deprecated(message = "Use AuthenticationRequest data class instead")
data class UserRequest(
    val userName: String,
    val password: String
)
