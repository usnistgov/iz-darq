#!/bin/bash

# Read-only companion to qdar-versions.sh
# Prints the current values of everything qdar-versions.sh would change,
# without modifying a single file.

set -e

while getopts :t:q: flag
do
    case "${flag}" in
        t) TARGET=${OPTARG};;
        q) QDAR=${OPTARG};;
    esac
done

if [ -z "$QDAR" ]; then
    echo "qDAR Path is required. (-q)"
    exit 1
fi

if [ -z "$TARGET" ]; then
    TARGET="all"
fi

cd $QDAR

# Reads a pom's own <version>, ignoring the one inside <parent>.
# Falls back to the parent version when the module does not declare its own.
project_version() {
    POM="$1"
    if [ ! -f "$POM" ]; then
        echo "POM NOT FOUND"
        return
    fi
    VALUE=$(sed -e '/<parent>/,/<\/parent>/d' "$POM" \
            | grep -m1 -oE '<version>[^<]*</version>' \
            | sed -E 's:</?version>::g')
    if [ -z "$VALUE" ]; then
        VALUE=$(sed -n '/<parent>/,/<\/parent>/p' "$POM" \
                | grep -m1 -oE '<version>[^<]*</version>' \
                | sed -E 's:</?version>::g')
        if [ -n "$VALUE" ]; then
            VALUE="$VALUE (inherited)"
        fi
    fi
    if [ -z "$VALUE" ]; then
        VALUE="UNKNOWN"
    fi
    echo "$VALUE"
}

# Prints every pom.xml that declares the given property, plus its value.
# Warns when the declarations disagree, since versions:update-property
# rewrites all of them to a single value.
print_property() {
    PROPERTY="$1"
    echo "  Property: $PROPERTY"

    MATCHES=$(grep -rn "<$PROPERTY>" --include=pom.xml . | grep -v '\${' || true)

    if [ -z "$MATCHES" ]; then
        echo "    (not declared in any pom.xml)"
        echo
        return
    fi

    echo "$MATCHES" | while IFS= read -r LINE
    do
        FILE="${LINE%%:*}"
        VALUE=$(echo "$LINE" | grep -oE "<$PROPERTY>[^<]*</$PROPERTY>" | sed -E "s:</?$PROPERTY>::g")
        printf "    %-52s %s\n" "${FILE#./}" "$VALUE"
    done

    DISTINCT=$(echo "$MATCHES" \
               | grep -oE "<$PROPERTY>[^<]*</$PROPERTY>" \
               | sed -E "s:</?$PROPERTY>::g" \
               | sort -u)
    COUNT=$(echo "$DISTINCT" | wc -l | tr -d ' ')
    if [ "$COUNT" -gt 1 ]; then
        echo "    ! INCONSISTENT - $COUNT distinct values: $(echo $DISTINCT | tr '\n' ' ')"
    fi
    echo
}

# Prints the version of a single module.
print_project() {
    LABEL="$1"
    POM="$2"
    printf "    %-52s %s\n" "$LABEL" "$(project_version "$POM")"
}

# Prints the version of every module in the reactor.
print_all_projects() {
    find . -name pom.xml -not -path "*/target/*" -not -path "*/node_modules/*" \
        | sort \
        | while IFS= read -r POM
          do
              printf "    %-52s %s\n" "${POM#./}" "$(project_version "$POM")"
          done
}

check_mqe() {
    echo "[mqe] MQE Version"
    echo "  Changed by: mvn versions:update-property -Dproperty=mqe.version"
    echo
    print_property "mqe.version"
}

check_api() {
    echo "[api] API Version"
    echo "  Changed by: mvn versions:set (all reactor modules)"
    echo "              mvn versions:update-property -Dproperty=qdar.api.version"
    echo
    echo "  Project versions:"
    print_all_projects
    echo
    print_property "qdar.api.version"
}

check_webapp() {
    echo "[webapp] Web App Version"
    echo "  Changed by: mvn --projects darq-webapp versions:set"
    echo "              mvn versions:update-property -Dproperty=qdar.webtool.version"
    echo
    echo "  Project versions:"
    print_project "darq-webapp/pom.xml" "darq-webapp/pom.xml"
    find darq-webapp -mindepth 2 -name pom.xml -not -path "*/target/*" -not -path "*/node_modules/*" \
        | sort \
        | while IFS= read -r POM
          do
              printf "    %-52s %s\n" "$POM" "$(project_version "$POM")"
          done
    echo
    print_property "qdar.webtool.version"
}

check_cli() {
    echo "[cli] CLI Version"
    echo "  Changed by: mvn --projects darq-extract-process/darq-cli-app versions:set"
    echo
    echo "  Project versions:"
    print_project "darq-extract-process/darq-cli-app/pom.xml" "darq-extract-process/darq-cli-app/pom.xml"
    echo
}

check_client() {
    echo "[client] AART Client Version"
    echo "  Changed by: mvn --projects darq-aart-client versions:set"
    echo
    echo "  Project versions:"
    print_project "darq-aart-client/pom.xml" "darq-aart-client/pom.xml"
    echo
}

echo "==================================================================================="
echo " qDAR Version Check - $QDAR"
echo "==================================================================================="
echo

case "${TARGET}" in
    mqe)    check_mqe ;;
    api)    check_api ;;
    webapp) check_webapp ;;
    cli)    check_cli ;;
    client) check_client ;;
    all)
        check_mqe
        check_api
        check_webapp
        check_cli
        check_client ;;
    *)
        echo "Unknown target '$TARGET' (options are : mqe, api, webapp, cli, client, all)"
        exit 1 ;;
esac

exit 0
