#!/bin/sh

##############################################################################
# Gradle start up script for UN*X
##############################################################################

# Attempt to set APP_HOME
APP_HOME=$( cd "${BASH_SOURCE%/*}" && pwd )

# Add default JVM options here.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

exec "/opt/gradle/bin/gradle" "$@"
