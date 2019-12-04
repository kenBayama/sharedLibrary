package org.shared
class Utilities implements Serializable {

    def sbtHome = tool 'sbt-0.13.11'

    def steps
    Utilities(steps) {this.steps = steps}

    def checkOutFrom(sourceBranch, credential, url) {
        steps.git branch: "${sourceBranch}", credentialsId: "${credential}", url: "${url}"
    }

    def compileService() {
        try{
            steps.sh "java -XX:+CMSClassUnloadingEnabled -Xms1024m -Xmx4096m -Xss4m -XX:PermSize=1024m -Dsbt.log.noformat=true -jar ${sbtHome}/bin/sbt-launch.jar clean pack"
        }
        catch(error){
            throw error
        }
    }

    def archiving = steps.archiveArtifacts artifacts: 'target/pack/**'

    def deploy (workspace,service,catchup_start_date,catchup_end_date,DeployOn,platform) {
        try{
            steps.sh "ssh -t -T devops@localhost 'cd ${workspace}/ansible && /var/lib/jenkins/env/bin/ansible-playbook deploy.yml --tags all -e \"service=${service}\" -e \"catchup_start_date=${catchup_start_date}\" -e \"catchup_end_date=${catchup_end_date}\" -e \"platforms_env=${DeployOn}\" -i platforms/${platform}/inventory.ini'"
        }
        catch(error){
            throw error
        }
    }




}