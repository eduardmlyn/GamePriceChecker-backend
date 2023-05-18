# GamePriceChecker-Backend

Requirements to run the application:
* PostgreSQL 15
* Maven 4
* Java 17

To build the application, execute the command `mvn clean install` to run with tests.
To build the application without tests, execute `mvn clean install -DskipTests`.

Afterwards an executable is created in the \target folder.
To run the executable, run the command `java -jar GamePriceChecker-backend-1.0-SNAPSHOT`.

The application also includes a Dockerfile.

To create an image of the application and run the image in a container, do the following:
* build the application as described above
* run docker engine
* run `docker build .`