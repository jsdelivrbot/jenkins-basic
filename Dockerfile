FROM registry.access.redhat.com/rhel7/rhel-atomic


RUN microdnf -h && microdnf --enablerepo=rhel-7-server-rpms install java-1.8.0-openjdk-headless --nodocs && \
    microdnf clean all

