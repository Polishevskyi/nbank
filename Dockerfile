# basic docker image
# build a base image from scratch each time (java, mvn, git)
# bare docker image and install java, maven
# IF YOU CAN BUILD AN IMAGE ON TOP OF ANOTHER IMAGE WHERE EVERYTHING IS ALREADY INSTALLED
# MARKETPLACE ALL DOCKER IMAGES - docker hub
FROM maven:3.9.9-eclipse-temurin-23

# Default argument values
ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:3000

# Environment variables for the container
ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}

# work from the /app folder
WORKDIR /app

# copy the pomnik
COPY pom.xml .

# load dependencies and cache
RUN mvn dependency:go-offline

# copy the whole project
COPY . .

# Run checkstyle check during build
RUN mvn checkstyle:check

# now there are dependencies inside, the whole project is in place and we are ready to run tests

USER root

# mvn test -P api
# mvn -DskipTests=true surfire-report:report
# the log was output to a file instead of to the console
# bash file
CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile: ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
    } > /app/logs/run.log 2>&1"