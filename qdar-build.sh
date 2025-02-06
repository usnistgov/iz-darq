set -e

while getopts :v:u:c:l:q:m:o: flag
do
    case "${flag}" in
        v) MQE_VALIDATOR=${OPTARG};;
        u) MQE_UTILS=${OPTARG};;
        c) MQE_CODEBASE_CLIENT=${OPTARG};;
        l) LONESTAR_FORECASTER=${OPTARG};;
        m) MISMO=${OPTARG};;
        q) QDAR=${OPTARG};;
        o) OUTPUT=${OPTARG};;
    esac
done

if [ -z "$QDAR" ]; then 
    echo "qDAR Path is required. (-qdar)"
    exit 1
fi

if [ -z "$MQE_VALIDATOR" ]; then 
    echo "MQE Validator Path is required. (-qdar)"
    exit 1
fi

TMP_LOCAL_REPO="$(mktemp -d)"

if [ -z "$OUTPUT" ]; then 
    OUTPUT="$( pwd )"
fi

if [[ -n "${LONESTAR_FORECASTER}" ]]; then
    echo "Building Lonestar Forecaster"
    cd $LONESTAR_FORECASTER
    mvn jar:jar package install:install -DskipTests -Dmaven.javadoc.skip=true -Dgpg.skip -Dexec.skip=true -Dmaven.repo.local=$TMP_LOCAL_REPO
fi

if [[ -n "${MISMO}" ]]; then
    echo "Building MISMO"
    cd $MISMO
    mvn clean install -DskipTests -Dmaven.javadoc.skip=true -Dgpg.skip -Dmaven.repo.local=$TMP_LOCAL_REPO
fi

if [[ -n "${MQE_CODEBASE_CLIENT}" ]]; then
    echo "Building MQE Codebase Client"
    cd $MQE_CODEBASE_CLIENT
    mvn clean install -DskipTests -Dmaven.javadoc.skip=true -Dgpg.skip -Dmaven.repo.local=$TMP_LOCAL_REPO
fi

if [[ -n "${MQE_UTILS}" ]]; then
    echo "Building MQE Utils"
    cd $MQE_UTILS
    mvn clean install -DskipTests -Dmaven.javadoc.skip=true -Dgpg.skip -Dmaven.repo.local=$TMP_LOCAL_REPO
fi

if [[ -n "${MQE_VALIDATOR}" ]]; then
    echo "Building MQE Validator"
    cd $MQE_VALIDATOR
    mvn clean install -DskipTests -Dmaven.javadoc.skip=true -Dgpg.skip -Dmaven.repo.local=$TMP_LOCAL_REPO
fi

echo "Building qDAR"
echo "Building qDAR Client"
cd $QDAR/darq-webapp/qdar-analysis-client
npm run build-prod
echo "Moving MQE's Latest Compiled.xml file into project"
echo "- Moving into CLI"
cp $MQE_VALIDATOR/src/test/resources/Compiled.xml $QDAR/darq-extract-process/darq-cli-app/src/main/resources/Compiled.xml
echo "- Moving into WebApp"
cp $MQE_VALIDATOR/src/test/resources/Compiled.xml $QDAR/darq-webapp/darq-app/src/main/resources/Compiled.xml
echo "- Moving into output directory"
mkdir $OUTPUT/resources
mkdir $OUTPUT/resources/WEB-INF
mkdir $OUTPUT/resources/WEB-INF/classes
cp $MQE_VALIDATOR/src/test/resources/Compiled.xml $OUTPUT/resources/WEB-INF/classes/Compiled.xml
echo "Building qDAR CLI"
cd $QDAR
mvn clean install -pl :darq-cli-app -am -Dmaven.repo.local=$TMP_LOCAL_REPO
echo "Update the Compiled.xml file in darq-cli-app-*-with-dependencies.jar"
jar -uvf $QDAR/darq-extract-process/darq-cli-app/target/darq-cli-app-*-with-dependencies.jar -C $OUTPUT/resources/WEB-INF/classes Compiled.xml
echo "Moving CLI into qDAR Webapp Resource"
cp $QDAR/darq-extract-process/darq-cli-app/target/darq-cli-app-*-with-dependencies.jar $QDAR/darq-webapp/darq-app/src/main/resources/qdar-cli.jar
echo "Building qDAR WAR"
mvn clean install -pl :darq-app -am -Dmaven.repo.local=$TMP_LOCAL_REPO
echo "Update the Compiled.xml file in qdar.war"
jar -uvf $QDAR/darq-webapp/darq-app/target/qdar.war -C $OUTPUT/resources WEB-INF/classes/Compiled.xml
echo "Moving Built artifacts into output directory"
cp $QDAR/darq-extract-process/darq-cli-app/target/darq-cli-app-*-with-dependencies.jar $OUTPUT/qdar-cli-nokey.jar
cp $QDAR/darq-webapp/darq-app/target/qdar.war $OUTPUT/qdar.war
rm -rf $OUTPUT/resources
rm -rf $TMP_LOCAL_REPO
exit 0