package cz.muni.fi.gamepricecheckerbackend;

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = [GamePriceCheckerBackendApplication::class])
@TestPropertySource(locations = ["classpath:application-test.properties"])
class BaseIntegrationTest
