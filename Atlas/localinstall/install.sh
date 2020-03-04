#!/usr/bin/env bash

error() {
	echo "ERROR: $@"
	exit 1
}
error_msg() {
	while read line; do echo "ERROR: $line"; done
}

[[ -n $JAVA_HOME ]] || error "JAVA_HOME is not defined, which means Java may not be installed."
which mvn > /dev/null || error "Maven (mvn) is required to build Atlas."

# Note that inconsistent name conventions:
targz=apache-atlas-2.0.0-sources.tar.gz
dest=apache-atlas-sources-2.0.0
targetdir=$dest/distro/target/apache-atlas-2.0.0-server/apache-atlas-2.0.0/
url=http://mirrors.sonic.net/apache/atlas/2.0.0/$targz

if [[ ! -d $targetdir ]]
then
	echo "Atlas build artifacts directory $targetdir not found; downloading and building..."
	download="wget -O"
	which wget > /dev/null
	if [[ $? -ne 0 ]]
	then
		download="curl -o"
		which curl > /dev/null
		if [[ $? -ne 0 ]]
		then
			error_msg <<EOF
Neither wget nor curl commands were found. Download Atlas from
  $url
to this directory, then run this script again.
EOF
			error
		fi
	fi
	$download ${targz} $url

	echo "Untaring $targz:"
	tar xvfz $targz
fi

if [[ ! -d $targetdir ]]
then
	echo "Building Atlas..."
	cd $dest
	pwd
	echo "Building Atlas with Maven: (directory: $PWD)"
	export MAVEN_OPTS="-Xms2g -Xmx2g"
	mvn clean -DskipTests package -Pdist,embedded-hbase-solr
	cd -  # move back, so the next cd works!
fi

cd $targetdir
echo "Starting Atlas: (Credentials: admin:admin, directory: $PWD)"
echo "Will dump the version and exit"
bin/atlas_start.py
curl -u admin:admin http://localhost:21000/api/atlas/admin/version

echo "Shutting down Atlas. To run again, run $PWD/bin/atlas_start.py"
bin/atlas_stop.py
