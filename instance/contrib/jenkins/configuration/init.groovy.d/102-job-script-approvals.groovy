import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.*
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.*


Thread.start {
    java.time.Instant startTime = java.time.Instant.now()

    final ScriptApproval sa = ScriptApproval.get();
    def job = null
    while (job == null) {
        if (java.time.Duration.between(startTime, java.time.Instant.now()).getSeconds() > 60) throw new RuntimeException('Timeout!')
        Thread.sleep(1000);
        job = Jenkins.instance.getItem('ON_GH_EVENT')
    }

    for (hudson.tasks.Builder builder:job.getBuilders()){
        if (builder instanceof hudson.plugins.groovy.SystemGroovy){
            def builderSource = builder.getSource()
            if (builderSource instanceof hudson.plugins.groovy.StringSystemScriptSource){
                String scriptSource = builderSource.getScript().getScript()
                ScriptApproval.PendingScript s = new ScriptApproval.PendingScript(scriptSource, GroovyLanguage.get(), ApprovalContext.create())
                sa.approveScript(s.getHash())
            }
        }
    }
}