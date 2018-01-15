#!/bin/bash

INPUT_FILE=$1
OUTPUT_FILE=$2
OUTPUT_DIR=$(dirname ${OUTPUT_FILE})

if [ ! -d $OUTPUT_DIR ]
then
  mkdir -p $OUTPUT_DIR
fi

CONFY=/var/lib/confy/bin/confy-assembly-2.3.jar

if [ ! -f $CONFY ]
then
  echo "$CONFY does not exist!"
  exit 1
fi

java -cp $CONFY confy.app.ConfigurationApp -i $INPUT_FILE > $OUTPUT_FILE
