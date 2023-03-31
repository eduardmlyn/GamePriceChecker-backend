
TODOS:
* change feignClient to requestline?
* add try catch to scraper findelement
* overall cleanup and fixes
* change scraping to just get the catalog data with price every day and check for any difference in description/image and other data every week/month
* move saving scrapped games to gameService

Possible extensions:
* add email to user for notifications
* more game provider API's/scraping
* scrape data from EA for all platforms not PC only


Change list 0.2.21:
* Created more tests, made them finally work
* added @Transactional to repositories
* Modified Swagger documentation

Change list 0.2.21b:
* added security logging for debugging
* updated methods in Authentication Controller
* updated methods in Authentication Service
* fix jwt validation != --> == ...
* added method to get username from jwt
* added authentication to any other requests than listed
* changed method to use the jwt username method
* added more tests

Change list 0.3.6
* added webdrivermanager
* added ea games site
* changed response status for registering
* added scrape endpoint for testing scraping ea site
* implemented/changed methods in game facade/repository/service
* updated gameServiceTest test class
* added dependencies for webdriver setup and selenium for scraping
* created scraper class WIP -> will be extracted to interface and have specific implementations for specific sites

Change list 0.3.12
* extracted chrome configuration to its own class
* cleaned up pom.xml
* added documentation to some classes/methods
* removed facades, replaced by services
* extracted ea scrapper, created interface for scraping classes
* created humble bundle scrapper
* created repositories for game links and price snapshots
* changed price snapshot date to Instant

Change list 0.3.14
* refactored chrome configuration to chrome factory
* added gameLink, priceSnapshot repository methods
* created cronJob class
* added scraping of game description to humble bundle scrapper

Change list 0.3.20
* implemented abstract scraping class for EA and Humble Bundle scrappers, contains saving scrapped data, more functionality to come in later version
* fixed typo in application.properties
* created Seller enum
* moved entities to entity package
* moved enum classes enums package
* moved @Transactional from repositories to services
* fixed chrome driver factory -> wasn't thread safe
* slightly refactored EA scrapper -> WIP
* refactored Humble Bundle scrapper
* modified Game entity
* created DTO objects
* renamed GameLink and game link stuff to GameSeller
* implemented repositories for gameSeller and priceSnapshot
* modified GameService to use DTOs and new repositories
* modified cronJob class
* removed deprecated stuff

Change list 0.3.22 and 0.3.27
* added scheduling thread property
* moved scrappers to scrapper package
* refactored humble bundle scrapper
* refactored ea scrapper
* added Steam data updater to update games from steam
* implemented methods to save scrapped and steam games
* changed game repository methods
* changed steam models to accept different responses
* changed entities to allow null values
* renamed README.md to ChangeList.md

Change list 0.3.28
* changed Transactional import and made readonly where applicable
* added @DynamicUpdate to entities for performance

Change list 0.3.29
* added @CrossOrigin to start connecting frontend with backend
* fixed game save issue in GameService

Change list 0.3.31
* added bigger timeout for feign
* cleanup of game controller
* modified and added methods to game repository
  * note: added query to findGameByName to link games sold by Steam and Humble Bundle
* added methods to save price snapshot
* created event, event publisher and event listener to create price snapshots after updating data 