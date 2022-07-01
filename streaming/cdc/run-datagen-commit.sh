#!/bin/bash

./run.sh datagen -s=0 -e=$((100 + $RANDOM % 200)) --do-commit=true --do-rollback=false

