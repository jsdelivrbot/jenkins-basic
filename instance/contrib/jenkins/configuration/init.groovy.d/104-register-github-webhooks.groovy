import jenkins.*
import jenkins.model.*
import org.kohsuke.github.*


String jenkinsUrl = JenkinsLocationConfiguration.get().getUrl()
String genericWebHookTriggerToken = Jenkins.instance.getItem('ON_GH_EVENT').getTrigger(org.jenkinsci.plugins.gwt.GenericTrigger.class).getToken()



Jenkins.instance.getAllItems().each { job ->
  if (job instanceof jenkins.branch.MultiBranchProject){
    for (def branchSource:job.getSources()){
      if (branchSource instanceof jenkins.branch.BranchSource){
        if (branchSource.getSource() instanceof org.jenkinsci.plugins.github_branch_source.GitHubSCMSource){
          	def scmBranchSource = branchSource.getSource()
              //org.jenkinsci.plugins.github_branch_source.GitHubSCMBuilder.uriResolver(job, scmBranchSource.getApiUri())
            com.cloudbees.plugins.credentials.common.StandardCredentials credentials = org.jenkinsci.plugins.github_branch_source.Connector.lookupScanCredentials(job, scmBranchSource.getApiUri(), scmBranchSource.getCredentialsId())
            org.kohsuke.github.GitHub github = org.jenkinsci.plugins.github_branch_source.Connector.connect(scmBranchSource.getApiUri(), credentials);
            String fullName = scmBranchSource.getRepoOwner() + "/" + scmBranchSource.getRepository();
            //println fullName
            org.kohsuke.github.GHRepository ghRepository = github.getRepository(fullName);
            Map hooks =[
                'github-webhook':['url':"${jenkinsUrl}github-webhook/", 'events':[org.kohsuke.github.GHEvent.PULL_REQUEST, org.kohsuke.github.GHEvent.PUSH]],
                'generic-webhook-trigger.0':['url':"${jenkinsUrl}generic-webhook-trigger/invoke?token=${genericWebHookTriggerToken}", 'events':[org.kohsuke.github.GHEvent.PULL_REQUEST]]
            ]
            for (def hook:ghRepository.getHooks()){
                //println hook
                hooks.each{ String name, Map newHook ->
                    if (hook.getConfig()['url'].startsWith(newHook.url)){
                        newHook['_hook']=hook
                    }
                }
            }
            //Create Hooks
            hooks.each{ String name, Map newHook ->
                if (newHook._hook == null){
                    Map hookCfg = ['url':newHook.url]
                    if (newHook.qs){
                        if (hookCfg.url.contains('?')){
                            hookCfg.url=hookCfg.url+'&'
                        }else{
                            hookCfg.url=hookCfg.url+'?'
                        }
                        hookCfg.url=hookCfg.url+newHook.qs
                    }
                    println "Registering webhook: ${[new URL(hookCfg.url), newHook.events]}"
                    ghRepository.createWebHook(new URL(hookCfg.url), newHook.events)
                }
            }
        }
      }
    }
  }
}
