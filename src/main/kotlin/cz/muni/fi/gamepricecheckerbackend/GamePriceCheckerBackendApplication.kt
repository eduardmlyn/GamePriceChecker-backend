package cz.muni.fi.gamepricecheckerbackend

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignAutoConfiguration

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info = Info(title = "Game Price Checker API", version = "1.0", description = "API of Game Price Checker Backend"))
@ImportAutoConfiguration(FeignAutoConfiguration::class)
class GamePriceCheckerBackendApplication

fun main(args: Array<String>) {
    runApplication<GamePriceCheckerBackendApplication>(*args)
}
