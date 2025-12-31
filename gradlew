#!/usr/bin/env sh

# Simplified Gradle wrapper script for Unix
DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_CMD="java"
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
PROPS="$DIR/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Gradle wrapper jar not found: $WRAPPER_JAR" >&2
  exit 1
fi

exec "$JAVA_CMD" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
