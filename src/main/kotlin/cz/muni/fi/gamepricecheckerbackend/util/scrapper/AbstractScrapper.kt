package cz.muni.fi.gamepricecheckerbackend.util.scrapper

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import jakarta.transaction.Transactional

/**
 * Abstract scrapper class.
 *
 * @author Eduard Stefan Mlynarik
 */
abstract class AbstractScrapper: Scrapper {
    // TODO needed?
}
