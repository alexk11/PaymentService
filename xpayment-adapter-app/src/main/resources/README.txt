Compile jar file with
"mvn clean compile assembly:single" to avoid the error "no main manifest attribute in app.jar"

Build docker image:
 docker build --build-arg JAR_FILE=.//target//xpayment-adapter-app-0.0.1-SNAPSHOT.jar -t xpayment-adapter-app .
