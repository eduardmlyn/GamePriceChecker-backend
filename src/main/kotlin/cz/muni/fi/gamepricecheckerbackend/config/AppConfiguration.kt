package cz.muni.fi.gamepricecheckerbackend.config

import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Configuration
class AppConfiguration(val userRepository: UserRepository) {

    var webDriver: ChromeDriver? = null

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username -> userRepository.findUserByUserName(username) }
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun chromeDriver(): ChromeDriver {
        return webDriver ?: initWebDriver()
    }

    private fun initWebDriver(): ChromeDriver {
        WebDriverManager.chromedriver().setup()
        val options = ChromeOptions()
        val userAgent = """
            Mozilla/5.0 (X11; Linux x86_64) 
            AppleWebKit/537.36 (KHTML, like Gecko) 
            Chrome/60.0.3112.50 Safari/537.36""".trimIndent()
        options.addArguments(
            "user-agent=$userAgent",
//            "--headless",
            "--window-size=1920,1080",
            "--disable-extensions",
            "--start-maximized",
            "--disable-gpu",
            "--ignore-certificate-errors"
        )
        return ChromeDriver(options)
    }
}