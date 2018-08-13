import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import org.jenkinsci.plugins.plaincredentials.impl.*;
import hudson.model.*
import hudson.util.Secret;


def runOrDie(def command, String errorMessage){
  def process=command.execute()
  String processText = process.text
  def exitValue = process.waitFor()
  println command.join(' ')
  if (process.exitValue() != 0 ) throw new RuntimeException("${errorMessage} [${processText}] (exit value:${process.exitValue()})")  
  return processText
}

Thread.start {
    String openshiftSecretName = 'jenkins-slave-user'
    String username=runOrDie(['sh', '-c', "oc get secret/${openshiftSecretName} --template={{.data.username}} | base64 --decode"], "'secret/${openshiftSecretName}' was NOT nound")

    User u = User.get(username)
    def apiToken=u.getProperty(jenkins.security.ApiTokenProperty.class)
    println "\'${u.getId()}\' API token:${apiToken.getApiTokenInsecure()}"
    ['oc','patch', "secret/${openshiftSecretName}", '-p', '{"stringData": {"password": "'+apiToken.getApiTokenInsecure()+'"}}', '-n', 'csnr-devops-lab-tools'].execute().waitFor()

    
    Jenkins.instance.getAuthorizationStrategy().add(hudson.slaves.SlaveComputer.CREATE, username)
    Jenkins.instance.getAuthorizationStrategy().add(hudson.slaves.SlaveComputer.CONNECT, username)
    Jenkins.instance.getAuthorizationStrategy().add(Item.READ, username)
    u.save();
}