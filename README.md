
TODOS:
* change feignClient to requestline?
* remove facades?
* add try catch to scraper findelement

Possible extensions:
* add email to user for notifications
* more game provider API's/scraping


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