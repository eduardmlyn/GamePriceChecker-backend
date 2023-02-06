package cz.muni.fi.gamepricecheckerbackend.wrapper

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class ResponseWrapper<T>(
    val message: String,
    val data: T
)
