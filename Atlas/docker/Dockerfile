FROM ubuntu:18.04
ARG VERSION=2.0.0

RUN apt-get update \
    && apt-get -y upgrade \
    && apt-get -y install apt-utils \
    && apt-get -y install \
        maven \
        wget \
        python \
        openjdk-8-jdk-headless \
        patch \
        lsof

# Get and build Atlas
RUN cd /tmp \
    && wget http://mirror.linux-ia64.org/apache/atlas/${VERSION}/apache-atlas-${VERSION}-sources.tar.gz \
    && mkdir /tmp/atlas-src \
    && tar --strip 1 -xzvf apache-atlas-${VERSION}-sources.tar.gz -C /tmp/atlas-src \
    && rm apache-atlas-${VERSION}-sources.tar.gz \
    && cd /tmp/atlas-src \
    && export MAVEN_OPTS="-Xms2g -Xmx2g" \
    && export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64" \
    && mvn clean -DskipTests package -Pdist,embedded-hbase-solr \
    && mv /tmp/atlas-src/distro/target/apache-atlas-${VERSION}-server/apache-atlas-${VERSION} /opt \
    && chmod -R 777 /opt \
    && rm -Rf /tmp/atlas-src

# Create user
RUN useradd atlas
RUN sed -i 's/atlas:x:1000:1000::/atlas:x:1000800000:1000:atlas:/' /etc/passwd
RUN chown -R atlas:atlas /opt

USER atlas

WORKDIR /opt

EXPOSE 21000

ENV PATH=$PATH:/opt
ENV START=/opt/apache-atlas-${VERSION}

ENV JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"
ENV MANAGE_LOCAL_HBASE=true
ENV MANAGE_LOCAL_SOLR=true
ENV ATLAS_SERVER_OPTS="-server -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+PrintTenuringDistribution -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dumps/atlas_server.hprof -Xloggc:logs/gc-worker.log -verbose:gc -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=1m -XX:+PrintGCDetails -XX:+PrintHeapAtGC -XX:+PrintGCTimeStamps"
#ENV ATLAS_SERVER_HEAP="-Xms8000m -Xmx8000m -XX:MaxNewSize=5120m -XX:MetaspaceSize=100M -XX:MaxMetaspaceSize=512m"

ENTRYPOINT ["/bin/bash", "-c", "${START}/bin/atlas_start.py; tail -fF ${START}/logs/application.log"]