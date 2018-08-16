import jenkins.*
import jenkins.model.*

class JenkinsInstall extends Script {
    def run() {
        println "${args}"
    }
    static void main(String[] args) {           
        org.codehaus.groovy.runtime.InvokerHelper.runScript(JenkinsInstall, args)     
    }

}