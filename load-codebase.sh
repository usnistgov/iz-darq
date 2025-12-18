#!/usr/bin/env bash
set -e

while getopts :q: flag
do
    case "${flag}" in
        q) QDAR=${OPTARG};;
    esac
done

if [ -z "$QDAR" ]; then
    QDAR="."
fi


SHA=$(cat codebase-compiled-xml.sha)
FILE_URL="https://raw.githubusercontent.com/immregistries/codebase/$SHA/base/Compiled.xml"
TMP_DOWNLOAD_LOCATION="$(mktemp -d)"

echo "Fetching codebase Compiled XML for SHA $SHA"
curl -sSL "$FILE_URL" -o $TMP_DOWNLOAD_LOCATION/Compiled.xml

echo "Moving Compiled.xml file into project"
echo "- Moving into CLI"
cp $TMP_DOWNLOAD_LOCATION/Compiled.xml $QDAR/darq-extract-process/darq-cli-app/src/main/resources/Compiled.xml
echo "- Moving into WebApp"
cp $TMP_DOWNLOAD_LOCATION/Compiled.xml $QDAR/darq-webapp/darq-app/src/main/resources/Compiled.xml