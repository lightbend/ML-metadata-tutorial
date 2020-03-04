#!/usr/bin/env bash
# Run a Docker image for Apache Atlas

help() {
	cat <<EOF
Usage: $0 [-h | --help] [- | --port P]
Where:
  -h | --help     Show this message and quit.
  -p | --port P   Use P as the local port (default: 21000).
  -n | --name N   Use N as the container name (default: atlas).
EOF
}

error() {
	echo "ERROR: $@"
	echo "ERROR: "
	help | while read line; do echo "ERROR: $line"; done
	exit 1
}

NAME=atlas
PORT=21000
while [[ $# -gt 0 ]]
do
	case $1 in
		-h|--h*)
			help
			exit 0
			;;
		-p|-p*)
			shift
			PORT=$1
			;;
		-n|-n*)
			shift
			NAME=$1
			;;
		*)
			error "Unrecognized argument: $1"
			;;
	esac
	shift
done

dir=$(dirname $0)
set -x
docker run --rm -it \
    -v $dir/atlas-logs:/opt/apache-atlas-2.0.0/logs \
    -v $dir/atlas-conf:/opt/apache-atlas-2.0.0/conf \
    -p $PORT:21000 \
    --name $NAME \
    lightbend/atlas:0.0.1

