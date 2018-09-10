import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.*
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.*

//it **CANNOT** run in a separate thread :(
//Thread.start {
    java.time.Instant startTime = java.time.Instant.now()
    
    ['ON_GH_EVENT', 'ON_STARTUP'].each { jobName ->
        def job = null
        //wait for job to load
        while (job == null) {
            if (java.time.Duration.between(startTime, java.time.Instant.now()).getSeconds() > 60) throw new RuntimeException('Timeout!')
            //println 'Waiting for ON_GH_EVENT to load'
            Thread.sleep(1000);
            job = Jenkins.instance.getItemByFullName(jobName)
        }

        final ScriptApproval sa = ScriptApproval.get();
        for (hudson.tasks.Builder builder:job.getBuilders()){
            if (builder instanceof hudson.plugins.groovy.SystemGroovy){
                def builderSource = builder.getSource()
                if (builderSource instanceof hudson.plugins.groovy.StringSystemScriptSource){
                    String scriptSource = builderSource.getScript().getScript()
                    ScriptApproval.PendingScript s = new ScriptApproval.PendingScript(scriptSource, GroovyLanguage.get(), ApprovalContext.create())
                    String scriptHash = s.getHash()
                    println "Approving Groovy Script for ${job.getDisplayName()} with hash ${scriptHash}"
                    sa.approveScript(scriptHash)
                }
            }
        }
    }
//}