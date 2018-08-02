FROM registry.access.redhat.com/rhel7/rhel-atomic

RUN set -x && microdnf -h && \
    curl -so /etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat-stable/jenkins.repo && \
    rpm --import https://jenkins-ci.org/redhat/jenkins-ci.org.key && \
    microdnf --enablerepo=rhel-7-server-rpms --enablerepo=jenkins install java-1.8.0-openjdk-devel shadow-utils jenkins --nodocs && \
    echo microdnf remove libxslt gdbm python-libs python python-lxml python-javapackages  && \
    microdnf clean all && \
    rpm -qa && \
    java -version && \
    chmod 666 /etc/sysconfig/jenkins

ENV JENKINS_VERSION=2 \
    HOME=/var/lib/jenkins \
    JENKINS_HOME=/var/lib/jenkins \
    JENKINS_UC=https://updates.jenkins.io \
    OPENSHIFT_JENKINS_IMAGE_VERSION=3.11 \
    LANG=en_US.UTF-8 \
    LC_ALL=en_US.UTF-8


LABEL io.k8s.description="Jenkins is a continuous integration server" \
      io.k8s.display-name="Jenkins 2" \
      io.openshift.tags="jenkins,jenkins2,ci" \
      io.openshift.expose-services="8080:http"

USER jenkins
