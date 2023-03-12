package cz.muni.fi.gamepricecheckerbackend.wrapper

/**
 * Wrapper class for responses.
 *
 * @author Eduard Stefan Mlynarik
 */
data class ResponseWrapper<T>(
    val message: String,
    val data: T
)
