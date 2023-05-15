package cz.muni.fi.gamepricecheckerbackend.util

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

/**
 *
 * @author Eduard Stefan Mlynarik
 */
class DateParserTest {
    private val logger: Logger = mockk()
    private val dateParser: DateParser = DateParser(logger)

    @Test
    fun `test parseDate with valid simple date`() {
        val dateString = "15 May, 2023"
        val expectedDate = Date.from(
            LocalDate.of(2023, 5, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()
        )

        val result = dateParser.parseDate(dateString)

        assertEquals(expectedDate, result)
        verify(exactly = 0) { logger.error(any()) }
    }

    @Test
    fun `test parseDate with valid shorter date`() {
        val dateString = "15 May"
        val expectedDate = Date.from(
            LocalDate.of(2023, 5, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        every { logger.info(any()) } just Runs

        val result = dateParser.parseDate(dateString)

        assertEquals(expectedDate, result)
        verify(exactly = 0) { logger.error(any()) }
    }

    @Test
    fun `test parseDate with valid simple month first date`() {
        val dateString = "May 15, 2023"
        val expectedDate = Date.from(
            LocalDate.of(2023, 5, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        every { logger.info(any()) } just Runs

        val result = dateParser.parseDate(dateString)

        assertEquals(expectedDate, result)
        verify(exactly = 0) { logger.error(any()) }
    }

    @Test
    fun `test parseDate with valid complex date`() {
        val dateString = "May 15, 2023 2:30 PM GMT-0700"
        val expectedDate = Date.from(
            LocalDate.of(2023, 5, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        every { logger.info(any()) } just Runs

        val result = dateParser.parseDate(dateString)

        assertEquals(expectedDate, result)
        verify(exactly = 0) { logger.error(any()) }
    }

    @Test
    fun `test parseDate with null input`() {
        val result = dateParser.parseDate(null)

        assertNull(result)
    }

    @Test
    fun `test parseDate with invalid date`() {
        val dateString = "Invalid date"
        every { logger.info(any()) } just Runs

        val result = dateParser.parseDate(dateString)

        assertNull(result)
        verify(exactly = 0) { logger.error(any()) }
    }
}