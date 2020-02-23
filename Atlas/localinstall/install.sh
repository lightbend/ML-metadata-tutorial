#!/usr/bin/env bash

error() {
	echo "ERROR: $@"
	while read line; do echo "ERROR: $line"; done
	exit 1
}

[[ -n $JAVA_HOME ]] || error "JAVA_HOME is not defined, which means Java may not be installed."
which mvn > /dev/null || error "Maven (mvn) is required to build Atlas."

source=http://mirrors.sonic.net/apache/atlas/2.0.0/apache-atlas-2.0.0-sources.tar.gz
dest=apache-atlas-2.0.0-sources.tar.gz
if [[ ! -f $dest ]]
then
	echo "$dest not found locally; downloading..."
	download="wget -O"
	which wget > /dev/null
	if [[ $? -ne 0 ]]
	then
		download="curl -o"
		which curl > /dev/null
		if [[ $? -ne 0 ]]
		then
			error "Need wget or curl" <<EOF
Neither wget nor curl commands were found. Download Atlas from
$source
Then run this script again.
EOF
		fi
	fi
	$download $dest $source
fi

echo "Untaring $dest:"
tar xvfz apache-atlas-2.0.0-sources.tar.gz
cd apache-atlas-sources-2.0.0/
pwd

echo "Building Atlas with Maven:"
export MAVEN_OPTS="-Xms2g -Xmx2g"
mvn clean -DskipTests package -Pdist,embedded-hbase-solr

echo "Starting Atlas: Credentials are admin:admin."
cd distro/target/apache-atlas-2.0.0-server/apache-atlas-2.0.0/
pwd
bin/atlas_start.py
curl -u admin:admin http://localhost:21000/api/atlas/admin/version


bin/atlas_stop.py