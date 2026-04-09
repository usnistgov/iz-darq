set -e

while getopts :t:v:q: flag
do
    case "${flag}" in
        t) TARGET=${OPTARG};;
        v) VERSION=${OPTARG};;
        q) QDAR=${OPTARG};;
    esac
done

if [ -z "$QDAR" ]; then 
    echo "qDAR Path is required. (-q)"
    exit 1
fi

if [ -z "$TARGET" ]; then 
    echo "Target is required. (-t) (options are : mqe, api, webapp, cli, client)"
    exit 1
fi

if [ -z "$VERSION" ]; then 
    echo "Version is required. (-v)"
    exit 1
fi

cd $QDAR

case "${TARGET}" in
    mqe) 
        echo "Changing MQE Version to $VERSION"
        mvn versions:update-property -Dproperty=mqe.version -DnewVersion=$VERSION ;;
    api) 
        echo "Changing API Version to $VERSION"
        mvn versions:set -DnewVersion=$VERSION
        mvn versions:update-property -Dproperty=qdar.api.version -DnewVersion=$VERSION ;;
    webapp) 
        echo "Changing Web App Version to $VERSION"
        mvn --projects darq-webapp versions:set -DnewVersion=$VERSION
        mvn versions:update-property -Dproperty=qdar.webtool.version -DnewVersion=$VERSION ;;
    cli) 
        echo "Changing CLI Version to $VERSION"
        mvn --projects darq-extract-process/darq-cli-app versions:set -DnewVersion=$VERSION ;;
    client) 
        echo "Changing AART Client to $VERSION"
        mvn --projects darq-aart-client versions:set -DnewVersion=$VERSION ;;
esac
exit 0