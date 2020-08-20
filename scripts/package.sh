#!/bin/bash
# set -x #echo on
mvn -s .circleci/settings.xml compile
sudo chmod 755 na-service/target/classes/drivers/*
mvn -s .circleci/settings.xml package