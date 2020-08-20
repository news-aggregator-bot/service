#!/bin/bash
# set -x #echo on
mvn compile
sudo chmod 755 na-service/target/classes/drivers/*
mvn package