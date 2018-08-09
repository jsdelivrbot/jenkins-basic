FROM registry.access.redhat.com/rhel7/rhel-atomic

RUN set -x && microdnf -h && \
    curl -so /etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat-stable/jenkins.repo && \
    rpm --import https://jenkins-ci.org/redhat/jenkins-ci.org.key && \
    microdnf --enablerepo=rhel-7-server-rpms --enablerepo=jenkins install java-1.8.0-openjdk-devel shadow-utils jenkins zip unzip rsync --nodocs && \
    echo microdnf remove libxslt gdbm python-libs python python-lxml python-javapackages  && \
    microdnf clean all && \
    rpm -qa

RUN set -x && \
    mkdir /tmp/oc && \
    curl -sLo /tmp/oc/openshift-origin-client-tools-v3.9.0-191fece-linux-64bit.tar.gz https://github.com/openshift/origin/releases/download/v3.9.0/openshift-origin-client-tools-v3.9.0-191fece-linux-64bit.tar.gz && \
    tar -xzf /tmp/oc/openshift-origin-client-tools-v3.9.0-191fece-linux-64bit.tar.gz -C /tmp/oc  --strip-components=1 && \
    cp /tmp/oc/oc /usr/local/bin/ && \
    chmod 777 /usr/local/bin/oc && \
    rm -rf /tmp/oc

COPY ./contrib/jenkins/support/bin /usr/local/bin
#Default Configuration
COPY ./contrib/jenkins/configuration /var/lib/jenkins
COPY ./contrib/openshift /opt/openshift

RUN set -x && \
    curl -sLo /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.2/dumb-init_1.2.2_amd64 && \
    chmod 777 /usr/local/bin/dumb-init

ENV JENKINS_VERSION=2 \
    HOME=/var/lib/jenkins \
    JENKINS_HOME=/var/lib/jenkins \
    JENKINS_UC=https://updates.jenkins.io \
    OPENSHIFT_JENKINS_IMAGE_VERSION=3.11 \
    LANG=en_US.UTF-8 \
    LC_ALL=en_US.UTF-8

RUN set -x && \
    curl -sLo /usr/local/bin/jenkins-install-plugins https://raw.githubusercontent.com/openshift/jenkins/master/2/contrib/jenkins/install-plugins.sh && \
    chmod 777 /usr/local/bin/jenkins-install-plugins && \
    (export REF=/var/lib/jenkins/plugins; /usr/local/bin/jenkins-install-plugins /opt/openshift/plugins.txt)

RUN set -x && \
    java -version && \
    chmod 664 /etc/passwd && \
    chmod -R 666 /etc/sysconfig/jenkins && \
    chgrp -R 0 /var/log/jenkins && \
    chmod -R 666 /var/log/jenkins && \
    chmod -R 666 /var/lib/jenkins && \
    chgrp -R 0 /usr/local/bin && \
    chmod -R g+rwx /usr/local/bin && \
    chgrp -R 0 /var/lib/jenkins && \
    chmod -R g+rwX /var/lib/jenkins && \
    chgrp -R 0 /var/log && \
    chmod -R g+rwX /var/log && \
    chgrp -R 0 /var/cache/jenkins && \
    chmod -R g+rwX /var/cache/jenkins


LABEL io.k8s.description="Jenkins is a continuous integration server" \
      io.k8s.display-name="Jenkins 2" \
      io.openshift.tags="jenkins,jenkins2,ci" \
      io.openshift.expose-services="8080:http"

USER 1001

