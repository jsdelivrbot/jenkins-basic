FROM registry.access.redhat.com/rhel7/rhel-atomic


RUN set -x && microdnf -h && microdnf --enablerepo=rhel-7-server-rpms install java-1.8.0-openjdk-headless shadow-utils --nodocs && \
   microdnf remove libxslt gdbm python-libs python python-lxml python-javapackages  && microdnf clean all && rpm -qa
