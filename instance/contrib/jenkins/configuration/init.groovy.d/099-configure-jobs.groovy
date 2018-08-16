import jenkins.*
import jenkins.model.*

class ConfigureJobs009 extends Script {

static Map exec(List args, Appendable stdout=null, Appendable stderr=null, Closure stdin=null){
    ProcessBuilder builder = new ProcessBuilder(args)
    def proc = builder.start()

    if (stdin!=null) {
        OutputStream out = proc.getOutputStream();
        stdin(out)
        out.flush();
        out.close();
    }

    if (stdout == null ){
        stdout = new StringBuffer()
    }
    
    //if (stderr == null ){
    //    stderr = stdout
    //}
    proc.waitForProcessOutput(stdout, stderr)
    int exitValue= proc.exitValue()

    Map ret = ['out': stdout, 'err': stderr, 'status':exitValue, 'cmd':args]

    return ret
}

def run() {
    String secretToken = UUID.randomUUID()
    Map ocGetSecretToken = exec(['sh', '-c', "set -x; oc get \"secret/\$(cat /var/run/secrets/github/metadata.name)\" \"--output=jsonpath={.data['generic-hook\\.token']}\" | base64 --decode"])

    if (ocGetSecretToken.status != 0 || ocGetSecretToken.out.toString().trim() == ""){
        println "Updating/Creating token"
        exec(['sh', '-c', "oc patch \"secret/\$(cat /var/run/secrets/github/metadata.name)\" -p '{\"stringData\": {\"generic-hook.token\": \"${secretToken}\"}}'" as String])
    }else{
        println "Using existing token"
        secretToken = ocGetSecretToken.out.toString()
    }


    def job = Jenkins.instance.getItem('ON_GH_EVENT')
    job.getTrigger(org.jenkinsci.plugins.gwt.GenericTrigger.class).setToken(secretToken)
    job.save()
    job.doReload()
}
    static void main(String[] args) {           
        org.codehaus.groovy.runtime.InvokerHelper.runScript(ConfigureJobs009, args)     
    }

}