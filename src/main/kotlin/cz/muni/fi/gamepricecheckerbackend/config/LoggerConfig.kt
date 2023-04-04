package cz.muni.fi.gamepricecheckerbackend.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Configuration
class LoggerConfig {
    val logger: Logger = LoggerFactory.getLogger(LoggerConfig::class.java)

    @Bean
    fun logger(): Logger {
        return logger
    }
}