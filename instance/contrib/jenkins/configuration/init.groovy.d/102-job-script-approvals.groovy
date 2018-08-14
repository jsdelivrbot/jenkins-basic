import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.*
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.*


Thread.start {
    Thread.sleep(1000)
    final ScriptApproval sa = ScriptApproval.get();
    def job = Jenkins.instance.getItem('ON_GH_EVENT');
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