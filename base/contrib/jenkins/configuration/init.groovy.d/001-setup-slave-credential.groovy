import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import org.jenkinsci.plugins.plaincredentials.impl.*;
import jenkins.model.Jenkins
import hudson.model.*
import hudson.util.Secret;



String openshiftPodNamespace=new File('/var/run/pod/namespace').getText('UTF-8').trim()
String openshiftSecretName = new File('/var/run/secrets/jenkins-slave-user/metadata.name').getText('UTF-8').trim()
String username = new File('/var/run/secrets/jenkins-slave-user/username').getText('UTF-8').trim()

User u = User.get(username)
def apiToken=u.getProperty(jenkins.security.ApiTokenProperty.class)
println "\'${u.getId()}\' API token:${apiToken.getApiTokenInsecure()}"
['oc','patch', "secret/${openshiftSecretName}", '-p', '{"stringData": {"password": "'+apiToken.getApiTokenInsecure()+'"}}', '-n', openshiftPodNamespace].execute().waitFor()


Jenkins.instance.getAuthorizationStrategy().add(hudson.slaves.SlaveComputer.CREATE, username)
Jenkins.instance.getAuthorizationStrategy().add(hudson.slaves.SlaveComputer.CONNECT, username)
Jenkins.instance.getAuthorizationStrategy().add(Jenkins.READ, username)
u.save();
