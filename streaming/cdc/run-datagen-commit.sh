#!/bin/bash

./run.sh datagen -s=0 -e=$((1 + $RANDOM % 100)) --do-commit=true --do-rollback=false

