#!/bin/sh

set -e

# --batch to prevent interactive command --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$GPG_PASSPHRASE" --output .github/workflows/jcv_sign.gpg .github/workflows/jcv_sign.gpg.gpg
