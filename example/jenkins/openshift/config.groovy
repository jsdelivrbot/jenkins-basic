
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
        'test' {
            namespace = app.namespaces.'build'.namespace
            disposable = false
        }
        'prod' {
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
            id = "pr-${opt.'pr'}"
        }
        suffix = "-build-${opt.'pr'}"
        id = "${app.name}${app.build.suffix}"
        version = "${app.build.env.name}-v${opt.'pr'}"
        name = "${app.name}" //

        namespace = app.namespaces.'build'.namespace
        timeoutInSeconds = 60*20 // 20 minutes
        templates = [
                [
                    'file':'example/jenkins/openshift/jenkins.bc.json',
                    'params':[
                        'NAME': "${app.build.name}",
                        'SUFFIX': "${app.build.suffix}",
                        'VERSION': app.build.version,
                        'SOURCE_REPOSITORY_URL': "${app.git.uri}",
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
        suffix = "${vars.deployment.suffix}" // app (unique name across all deployments int he namespace)
        version = "${app.deployment.env.name}-v${opt.'pr'}" //app-version  and tag
        name = "${app.name}" //app-name   (same name accross all deployments)
        id = "${app.deployment.name}${app.deployment.suffix}" // app (unique name across all deployments int he namespace)

        namespace = "${vars.deployment.namespace}"
        timeoutInSeconds = 60*20 // 20 minutes
        host = "${app.deployment.id}-${app.deployment.namespace}.pathfinder.gov.bc.ca"

        templates = [
                [
                    'file':'example/jenkins/openshift/jenkins.dc.json',
                    'params':[
                        'NAME': "${app.deployment.name}",
                        'SUFFIX': "${app.deployment.suffix}",
                        'VERSION': app.deployment.version,
                        'ROUTE_HOST': app.deployment.host,
                        'ENV_NAME':app.deployment.env.name,
                        'ENV_ID':app.deployment.env.id
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
                    id = "pr-${opt.'pr'}"
                }
                suffix = "-dev-${opt.'pr'}"
                name = "${app.name}"
                namespace = app.namespaces[env.name].namespace
            }
        }
    }
    'test' {
        vars {
            deployment {
                env {
                    name ="test"
                    id = "pr-${opt.'pr'}"
                }
                suffix = '-test'
                name = "${app.name}"
                namespace = app.namespaces[env.name].namespace
            }
        }
    }
    'prod' {
        vars {
            deployment {
                env {
                    name ="prod"
                    id = "pr-${opt.'pr'}"
                }
                suffix = ''
                id = "${app.name}${vars.deployment.suffix}"
                name = "${app.name}"
                namespace = app.namespaces[env.name].namespace
            }
        }
    }
}