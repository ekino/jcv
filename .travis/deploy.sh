#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

PROJECT_VERSION=$(./gradlew -q printVersion | tail -n 1)

echo "Project version is ${PROJECT_VERSION}"

ENCODED_SIGN_FILE_PATH="${DIR}/${ENCRYPTED_GPG_FILE_NAME}"
SIGN_FILE_PATH="${DIR}/${GPG_FILE_NAME}"

openssl aes-256-cbc -K $encrypted_329e0007fbf5_key -iv $encrypted_329e0007fbf5_iv -in "${ENCODED_SIGN_FILE_PATH}" -out "${SIGN_FILE_PATH}" -d

if [[ "${PROJECT_VERSION}" =~ .*-SNAPSHOT$ ]]; then
    ./gradlew publish -PossrhUrl="${PUBLISH_SNAPSHOT_REPO_URL}" -PossrhUsername="${PUBLISH_REPO_USERNAME}" -PossrhPassword="${PUBLISH_REPO_PASSWORD}" -Psigning.keyId="${GPG_KEY_ID}" -Psigning.password="${GPG_PASSPHRASE}" -Psigning.secretKeyRingFile="${SIGN_FILE_PATH}"
elif [[ ! "${PROJECT_VERSION}" =~ .*-SNAPSHOT$ && "${TRAVIS_TAG}" =~ ^([0-9])+\.([0-9])+(\.([0-9])+)*(-[a-zA-Z0-9_-]+)*$ ]]; then
    ./gradlew checkSnapshotDependencies publish -PossrhUrl="${PUBLISH_RELEASE_REPO_URL}" -PossrhUsername="${PUBLISH_REPO_USERNAME}" -PossrhPassword="${PUBLISH_REPO_PASSWORD}" -Psigning.keyId="${GPG_KEY_ID}" -Psigning.password="${GPG_PASSPHRASE}" -Psigning.secretKeyRingFile="${SIGN_FILE_PATH}"
else
    echo "The version and/or the tag are invalid : Nothing to deploy"
fi
