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
else
  echo "ENV FILE $ENV_FILE not found"
fi
rm -r /opt/homebrew/opt/tomcat@8/libexec/webapps/*;
cp ./qdar.war /opt/homebrew/opt/tomcat@8/libexec/webapps/;
/opt/homebrew/opt/tomcat@8/bin/catalina jpda run;
