pipeline {
    agent{
		label 'master'
	}
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '15', numToKeepStr: '200')
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
        timeout(20)
    }
	environment {
		cnvdata = "/u03/safe/dependency-check/data"
		EMAIL_RECIPIENTS = 'wangzhi@youzhao.io, wangbing@youzhao.io,wanghuiyong@youzhao.io,huanglian@youzhao.io,fandongfang@youzhao.io,lininshan@youzhao.io'
    }

	stages{
		stage('拉取代码'){
			steps{
				script{
				    BRANCH_NAME = "master"
				    git branch: "${BRANCH_NAME}", credentialsId: '1', url: 'https://github.com/100offer/hcm.git'
				    //setGitHubPullRequestStatus context: '', message: 'start deploy', state: 'PENDING'
				    echo "当前分支:${BRANCH_NAME}"
				    if("${BRANCH_NAME}"!='master' && "${BRANCH_NAME}"!='release' && "${BRANCH_NAME}"!='unittest'){
				        error "当前${BRANCH_NAME}分支不允许发布，请联系智哥"
				    }
				}
		    }
	    }
	    stage('编译'){
	        steps{
	            script{
	                sh "cp src/test/resources/application-test.yml src/test/resources/application.yml"
	                sh './gradlew clean build -x test'
        		}
        	}
        }
        stage('接口测试'){
            steps{
                script{
                    sh './gradlew test jacocoTestReport'
        		    junit 'build/test-results/test/*.xml'
                }
        	}
        }
        stage('打包'){
            steps{
                script{
                    sh './gradlew bootJar'
        			sh 'rm -rf /home/docker/hcm.jar && cp build/libs/hcm-0.0.1-SNAPSHOT.jar /home/docker/hcm.jar'
        		}
        	}
        }
        stage('质量检查'){
        	environment {
        	    scannerHome = tool 'sonarscanner'
            }
            steps{
                withSonarQubeEnv('SonarQube') {
                    //sh './gradlew sonarqube -x test'
                    sh "cp src/test/resources/sonar-project.properties ."
                    sh "${scannerHome}/bin/sonar-scanner"
                }
                script {
                    sleep 20
                    timeout(time: 1, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        stage('生成镜像'){
            steps{
                script{
                    time = sh(returnStdout: true ,script: "date +%m%d%H%M").trim()
                    //image = "10.27.115.224:5000/hcm:${currentBuild.timeInMillis}"
                    image = "10.27.115.224:5000/hcm:${time}"
                    currentBuild.description = "${image}"
                    sh "cp src/test/resources/Dockerfile Dockerfile && cp /root/agent.zip ."
                    sh "docker build -t ${image} . && docker push ${image}"
                }
            }
        }
        stage('服务启动'){
            steps{
                script{
                    if('master'=="${BRANCH_NAME}"){
                        build job: 'ServiceManage', parameters: [string(name: 'env', value: 'DEV'), string(name: 'ImageNo', value: "${time}")]
                    } else if ('release'=="${BRANCH_NAME}"){
                        build job: 'ServiceManage', parameters: [string(name: 'env', value: 'UAT'), string(name: 'ImageNo', value: "${time}")]
                    }
                }
            }
        }
    }
    post {
        always {
            sendDD("${currentBuild.currentResult}")
        }
    }
}

@NonCPS
def getChangeString() {
    MAX_MSG_LEN = 100
    def changeString = ""

    echo "Gathering SCM changes"
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            changeString += " - ${truncated_msg} [${entry.author}]\n"
        }
    }

    if (!changeString) {
        changeString = " - No new changes"
    }
    return changeString
}

def sendDD(status) {
    def image = 'red.png'
    if(status == 'SUCCESS'){
        image = 'blue.png'
        //setGitHubPullRequestStatus context: '', message: 'deploy success', state: 'SUCCESS'
    }else{
        //setGitHubPullRequestStatus context: '', message: 'deploy error', state: 'FAILURE'
    }
    dingTalk accessToken: '90b367795a9c68bda3a8021488bcfc1f854b44f5ef9ca820cdb7b2dbe13a4f1f', imageUrl: "http://47.102.20.44:8080/static/b570d509/images/32x32/${image}", jenkinsUrl: 'http://47.102.20.44:8080/', message: "Changes:\n " + getChangeString(), notifyPeople: ''
}