FROM registry.access.redhat.com/rhel7/rhel-atomic


RUN microdnf -h && microdnf --enablerepo=rhel-7-server-rpms install java-1.8.0-openjdk-headless --nodocs && \
   microdnf remove python-javapackages python-lxml python python-libs && microdnf clean all

