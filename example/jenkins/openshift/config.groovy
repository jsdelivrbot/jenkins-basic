
app {
    name = 'jenkins-atomic'
    namespaces { //can't call environments :(
        'build'{
            namespace = 'csnr-devops-lab-tools'
            disposable = true
        }
        'dev' {
            namespace = app.namespaces.'build'.namespace
            disposable = false
        }
    }

    git {
        workDir = ['git', 'rev-parse', '--show-toplevel'].execute().text.trim()
        uri = ['git', 'config', '--get', 'remote.origin.url'].execute().text.trim()
        ref = ['bash','-c', 'git config branch.`git name-rev --name-only HEAD`.merge'].execute().text.trim()
        commit = ['git', 'rev-parse', 'HEAD'].execute().text.trim()
    }

    build {
        env {
            name = "build"
            id = "pr-1"
        }
        id = "${app.name}"
        version = "${app.build.env.name}-v1"
        name = "${app.name}" //

        namespace = app.namespaces.'build'.namespace
        timeoutInSeconds = 60*20 // 20 minutes
        templates = [
                [
                    'file':'example/jenkins/openshift/jenkins.bc.json',
                    'params':[
                        'NAME': "${app.build.name}",
                        'VERSION': app.build.version,
                        'SOURCE_REPOSITORY_URL': "${app.git.uri}"
                        'SOURCE_REPOSITORY_REF': "${app.git.ref}"
                    ]
                ]
        ]
    }

    deployment {
        env {
            name = vars.deployment.env.name // env-name
            id = vars.deployment.env.id
        }
        id = "${app.name}" // app (unique name across all deployments int he namespace)
        version = "${app.deployment.env.name}-v1" //app-version  and tag
        name = "${app.name}" //app-name   (same name accross all deployments)

        namespace = "${vars.deployment.namespace}"
        timeoutInSeconds = 60*20 // 20 minutes
        templates = [
                [
                    'file':'example/jenkins/openshift/jenkins.dc.json',
                    'params':[
                        'NAME':app.deployment.name,
                        'VERSION': app.deployment.version
                    ]
                ]
        ]
    }
}

environments {
    'dev' {
        vars {
            deployment {
                env {
                    name ="dev"
                    id = "pr-1"
                }
                id = "${app.name}-dev"
                name = "${app.name}"
                namespace = app.namespaces[env.name].namespace
            }
        }
    }
}