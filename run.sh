#! /usr/bin/env bash

FILE="target/scala-2.13/balloons-assembly-0.0.1.jar"

function dockerBuild {
  echo "building with docker"
  docker run --mount type=bind,source="$(pwd)",target=/opt --rm hseeberger/scala-sbt:8u222_1.3.5_2.13.1  /bin/sh -c 'cd /opt; sbt clean assembly'
}

if [[ -f "$FILE" ]]; then
  echo "enter balloons (e.g. 4 2 1 or r123):"
else 
  echo "building..."
  
  command -v sbt >/dev/null 2>&1 || { echo >&2 "SBT is required to build"; exit 1; }

  sbt assembly

  echo "enter balloons (e.g. 4 2 1 or r123):"
fi;

echo "args are $*"
java -jar $FILE