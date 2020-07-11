#!/bin/bash
# Starts all the services passed as arguments
exec docker-compose --project-name na-service -f docker-compose.yml up --build na-service