package cz.muni.fi.gamepricecheckerbackend.model.dto

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller

/**
 *
 * @author Eduard Stefan Mlynarik
 */
data class GameSellerDTO(
    val seller: Seller,
    val link: String,
    val price: Double
)
