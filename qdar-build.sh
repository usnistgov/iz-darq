#!/bin/bash

set -e

while getopts :q:o: flag
do
    case "${flag}" in
        q) QDAR=${OPTARG};;
        o) OUTPUT=${OPTARG};;
    esac
done

if [ -z "$QDAR" ]; then 
    echo "qDAR Path is required. (-qdar)"
    exit 1
fi

if [ -z "$OUTPUT" ]; then 
    OUTPUT="$( pwd )"
fi

TMP_LOCATION="$(mktemp -d)"
DEPENDENCIES_LOCATION="$(mktemp -d)"

# Fetch and load codebase Compiled.xml file
./load-codebase.sh -q "$QDAR"

# Fetch and build dependencies
export QDAR
export COMPILED_XML="$QDAR"/darq-extract-process/darq-cli-app/src/main/resources/Compiled.xml
./dependencies.sh build "$DEPENDENCIES_LOCATION"

echo "Building qDAR"

echo "Building qDAR Client"
cd "$QDAR"/darq-webapp/qdar-analysis-client
npm install
npm run build-prod

echo "Moving Compiled.xml file into output directory"
mkdir -p "$TMP_LOCATION"/resources/WEB-INF/classes
cp "$QDAR"/darq-extract-process/darq-cli-app/src/main/resources/Compiled.xml "$TMP_LOCATION"/resources/WEB-INF/classes/Compiled.xml

echo "Building qDAR CLI"
cd "$QDAR"
mvn clean install -pl :darq-cli-app -am

echo "Update the Compiled.xml file in darq-cli-app-*-with-dependencies.jar (overrides any Compiled.xml files)"
jar -uvf "$QDAR"/darq-extract-process/darq-cli-app/target/darq-cli-app-*-with-dependencies.jar -C "$TMP_LOCATION"/resources/WEB-INF/classes Compiled.xml

echo "Moving CLI into qDAR Webapp Resource"
cp "$QDAR"/darq-extract-process/darq-cli-app/target/darq-cli-app-*-with-dependencies.jar $QDAR/darq-webapp/darq-app/src/main/resources/qdar-cli.jar

echo "Building qDAR WAR"
mvn clean install -pl :darq-app -am

echo "Update the Compiled.xml file in qdar.war (overrides any Compiled.xml files)"
jar -uvf "$QDAR"/darq-webapp/darq-app/target/qdar.war -C "$TMP_LOCATION"/resources WEB-INF/classes/Compiled.xml

echo "Moving Built artifacts into output directory"
cp "$QDAR"/darq-extract-process/darq-cli-app/target/darq-cli-app-*-with-dependencies.jar "$OUTPUT"/qdar-cli-nokey.jar
cp "$QDAR"/darq-webapp/darq-app/target/qdar.war "$OUTPUT"/qdar.war

rm -rf "$TMP_LOCATION"
exit 0