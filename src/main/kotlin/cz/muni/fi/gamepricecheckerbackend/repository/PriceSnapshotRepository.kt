package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.PriceSnapshot
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Transactional
interface PriceSnapshotRepository: JpaRepository<PriceSnapshot, String>