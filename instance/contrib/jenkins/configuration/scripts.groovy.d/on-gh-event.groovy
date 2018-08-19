import groovy.json.*


static Map exec(List args, File workingDirectory=null, Appendable stdout=null, Appendable stderr=null, Closure stdin=null){
    ProcessBuilder builder = new ProcessBuilder(args as String[])
    if (stderr ==null){
        builder.redirectErrorStream(true)
    }
    if (workingDirectory!=null){
        builder.directory(workingDirectory)
    }
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

    proc.waitForProcessOutput(stdout, stderr)
    int exitValue= proc.exitValue()

    Map ret = ['out': stdout, 'err': stderr, 'status':exitValue, 'cmd':args]

    return ret
}

String ghPayload = build.buildVariableResolver.resolve("payload")
String ghEventType = build.buildVariableResolver.resolve("x_github_event")

//println "ghEventType:"
//println "${ghEventType}"

//println "ghPayload:"
//println "${ghPayload}"

//binding.variables.each{ 
//  println "${it.key}:${it.value}"
//}

if ("pull_request" == ghEventType){
    def payload = new JsonSlurper().parseText(ghPayload)
    if ("closed" == payload.action){
        File gitWorkDir = new File("tmp")
        println "git root:${gitWorkDir.getAbsolutePath()}"

        println exec(['rm', '-rf', gitWorkDir.getAbsolutePath()])
        println exec(['git', 'init', gitWorkDir.getAbsolutePath()])
        println exec(['git', 'remote', 'add', 'origin', payload.repository.clone_url], gitWorkDir)
        println exec(['git', 'fetch', '--no-tags', payload.repository.clone_url, "+refs/pull/${payload.number}/head:PR-${payload.number}"], gitWorkDir)
        println exec(['git', 'checkout', "PR-${payload.number}"] , gitWorkDir)

        ///Users/cvarjao/Documents/GitHub/mds/
        exec(['sh', '-c', "pipeline/gradlew --no-build-cache --console=plain --no-daemon -b pipeline/build.gradle cd-clean -Pargs.--config=pipeline/config.groovy -Pargs.--pr=${payload.number}"] , gitWorkDir, binding.variables.out)
    }
}