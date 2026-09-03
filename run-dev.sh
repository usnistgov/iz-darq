#!/bin/sh
# Define the filenames
SDKMAN_INIT="$HOME/.sdkman/bin/sdkman-init.sh"
RC_FILE=".sdkmanrc"
ENV_FILE="./.dev.env"
TOMCAT_VERSION=8

if [ -f "$RC_FILE" ]; then
    # Check if SDKMAN! is actually installed
    if [ -s "$SDKMAN_INIT" ]; then
        echo "Found $RC_FILE. Initializing SDKMAN!..."
        source "$SDKMAN_INIT"
        sdk env
    else
        echo "Error: $RC_FILE detected, but SDKMAN! is not installed on this machine."
        echo "Visit https://sdkman.io/install to set it up."
    fi
else
    echo "No $RC_FILE found. Using default Java version:"
    java -version 2>&1 | head -n 1
fi
if [ -f "$RC_FILE" ]; then
  set -a;
  source $ENV_FILE;
  set +a;
  echo "ENV FILE $ENV_FILE loaded"
else
  echo "ENV FILE $ENV_FILE not found"
fi
rm -r /opt/homebrew/opt/tomcat@$TOMCAT_VERSION/libexec/webapps/*;
cp ./darq-webapp/darq-app/target/qdar.war/qdar.war /opt/homebrew/opt/tomcat@$TOMCAT_VERSION/libexec/webapps/;

# Homebrew's tomcat@N/bin/catalina is a one-line wrapper that hardcodes
#   JAVA_HOME="/opt/homebrew/opt/openjdk"
# so it silently ignores the JDK `sdk env` just selected. Call catalina.sh
# directly instead, and pass through the .sdkmanrc JDK.
if [ -z "$JAVA_HOME" ]; then
  echo "Error: JAVA_HOME is not set; cannot pin Tomcat to the .sdkmanrc JDK."
  exit 1
fi
echo "Starting Tomcat $TOMCAT_VERSION with JAVA_HOME=$JAVA_HOME"
JRE_HOME="$JAVA_HOME" \
  /opt/homebrew/opt/tomcat@$TOMCAT_VERSION/libexec/bin/catalina.sh jpda run;
