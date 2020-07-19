#!/bin/bash
# set -x #echo on

while getopts 'c:u:h:f:v:' opt; do
	case $opt in
		u) USER="$OPTARG" ;;
		h) HOST="$OPTARG" ;;				
		c) CERT="$OPTARG" ;;
		f) FILE="$OPTARG" ;;		
		v) VERSION="$OPTARG" ;;		
		*) exit 1 ;;
	esac
done

ssh -o StrictHostKeyChecking=no -i certs/$CERT -l $USER $HOST "/bin/bash --login -s" -- < scripts/swarm-update.sh "$FILE"