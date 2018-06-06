# jenkins-basic

```
oc project csnr-devops-lab-tools
oc import-image jenkins-alpine:lts --from=docker.io/jenkins/jenkins:lts-alpine --confirm
```



```

curl -Lo /tmp/oc.tar.gz https://github.com/openshift/origin/releases/download/v3.9.0/openshift-origin-client-tools-v3.9.0-191fece-linux-64bit.tar.gz
tar xzf /tmp/oc.tar.gz openshift-origin-client-tools-v3.9.0-191fece-linux-64bit/oc --strip-components=1 -C /tmp
tar xzf /tmp/oc.tar.gz -C /tmp

```
