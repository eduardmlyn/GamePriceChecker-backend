package cz.muni.fi.gamepricecheckerbackend.util.scrapper

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import kotlin.random.Random

/**
 * Abstract scrapper class.
 *
 * @author Eduard Stefan Mlynarik
 */
abstract class AbstractScrapper: Scrapper {
    protected fun waitForElements(driver: ChromeDriver, locator: By) {
        WebDriverWait(
            driver,
            Duration.ofSeconds(5)
        ).until {
            ExpectedConditions.and(
                ExpectedConditions.presenceOfAllElementsLocatedBy(locator),
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
            )
        }
    }

    protected fun simulateUserBehaviour() {
        val randomWaitTime = Random.nextLong(2, 4)
        Thread.sleep(Duration.ofSeconds(randomWaitTime).toMillis())
    }

    protected fun clickButton(driver: ChromeDriver, element: WebElement) {
        driver.executeScript("arguments[0].click()", element)
    }
}
