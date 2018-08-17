import jenkins.model.Jenkins

import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.domains.*;
import org.jenkinsci.plugins.plaincredentials.impl.*;

import hudson.util.Secret;
import com.cloudbees.jenkins.GitHubWebHook;
import com.cloudbees.jenkins.*
import org.kohsuke.github.*

println "Configuring Global Libraries"
List globalLibraries=[
  [
      "name": "bcdevops-jenkins-shared-library",
      "scm": [
          "url": "https://github.com/BCDevOps/jenkins-pipeline-shared-lib.git" 
      ],
      "defaultVersion": "cvarjao/develop",
      "implicit": true,
      "allowVersionOverride": true,
      "includeInChangesets": true
  ]
]

List libraries=[]
for (Map globalLibrary: jenkinsConfig.globalLibraries) {
  def libScm = new jenkins.plugins.git.GitSCMSource(globalLibrary.scm.url);
  libScm.setCredentialsId('github-account');
  libScm.setTraits([new jenkins.plugins.git.traits.BranchDiscoveryTrait(), new jenkins.plugins.git.traits.TagDiscoveryTrait()]);
  def libRetriever = new org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever(libScm)
  def libConfig = new org.jenkinsci.plugins.workflow.libs.LibraryConfiguration(globalLibrary.name, libRetriever)
  libConfig.setDefaultVersion(globalLibrary.defaultVersion?:'master');
  libConfig.setImplicit(globalLibrary.implicit?:true);
  libConfig.setAllowVersionOverride(globalLibrary.allowVersionOverride?:true);
  libConfig.setIncludeInChangesets(globalLibrary.includeInChangesets?:true);
  libraries.add(libConfig)
}

  Jenkins.getInstance().getDescriptor(org.jenkinsci.plugins.workflow.libs.GlobalLibraries.class).setLibraries(libraries)
  Jenkins.getInstance().save()