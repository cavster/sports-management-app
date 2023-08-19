# Use an official OpenJDK 8 base image from AdoptOpenJDK
FROM adoptopenjdk:8-jre-hotspot

# Define build-time arguments for Scala and SBT versions
ARG SCALA_VERSION=2.12.15
ARG SBT_VERSION=1.5.4

# Install Java 8 runtime headless
RUN apt-get update && \
    apt-get install -y openjdk-8-jre-headless
# Install wget
RUN apt-get update && \
    apt-get install -y wget

# Install GPG

RUN apt-get update && apt-get install -y gnupg
# Add the GPG key for the Scala repository
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823

# Install Scala
RUN wget https://downloads.lightbend.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.deb && \
    dpkg -i scala-$SCALA_VERSION.deb && \
    rm scala-$SCALA_VERSION.deb

# Install SBT
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-get update && \
    apt-get install -y sbt

# Set the working directory to /app
WORKDIR /app

# Copy your project files into the container
COPY . .

# Build and run your Scala application using SBT
CMD ["sbt", "run"]
