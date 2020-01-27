cd /Users/boris/BigData
export JAVA_HOME=/Library/Java/Home
tar xvfz apache-atlas-2.0.0-sources.tar.gz
cd apache-atlas-sources-2.0.0/
export MAVEN_OPTS="-Xms2g -Xmx2g"
mvn clean -DskipTests package -Pdist,embedded-hbase-solr
cd distro/target/apache-atlas-2.0.0-server/apache-atlas-2.0.0/
bin/atlas_start.py
curl -u admin:admin http://localhost:21000/api/atlas/admin/version


bin/atlas_stop.py