def version = "latest"
def appName = env.APP_NAME
def devProject = env.DEV_PROJECT
def stageProject = env.STAGE_PROJECT
def dockerRegistry = env.DOCKER_REGISTRY

pipeline {
    agent {
        label 'gradle'
    }
    stages {
        stage('checkout') {
            steps {
                checkout scm
            }
        }
        stage('clean') {
            steps {
                sh "gradle clean --no-daemon"
            }
        }
        stage('Determine project version') {
            steps {   
                script {
                    version = sh returnStdout: true, script: 'gradle properties -q | grep "^version:" | awk \'{print $2}\' | tr -d \'[:space:]\''
                    echo "Current project version is ${version}"
                }
            }
        }
        stage('NPM Install') {
            steps {
                sh "gradle npm_install -PnodeInstall --no-daemon"
            }
        }
        stage('Run Tests') {
            parallel {
                stage('Backend tests') {
                    steps {
                        script {
                            try {
                                sh "gradle test -PnodeInstall --no-daemon"
                            } catch (err) {
                                throw err
                            } finally {
                                junit '**/build/**/TEST-*.xml'
                            }
                        }
                    }
                }

                stage('Frontend tests') {
                    steps {
                        script {
                            try {
                                sh "gradle npm_run_test-ci -PnodeInstall --no-daemon"
                            } catch (err) {
                                throw err
                            } finally {
                                junit '**/build/test-results/jest/TESTS-*.xml'
                            }
                        }
                    }
                }
            }
        }

        stage('Packaging') {
            steps {
                sh "gradle bootWar -x test -Pprod -PnodeInstall --no-daemon"
                //   archiveArtifacts artifacts: '**/build/libs/*.war', fingerprint: true
            }
        }

        stage('Create DEV/Build Project') {
            when {
                expression {
                    openshift.withCluster() {
                        return !openshift.selector("project", devProject).exists();
                    }
                }
            }
            steps {
                script {
                    openshift.withCluster() {
                        openshift.newProject(devProject);
                    }
                }
            }
        }

        stage('Create Image Builder') {
            when {
                expression {
                    openshift.withCluster() {
                        openshift.withProject(devProject) {
                            return !openshift.selector("bc", appName).exists();
                        }
                    }
                }
            }
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(devProject) {
                            openshift.newBuild("--name=tie", "--strategy docker", "--binary=true", "--docker-image openjdk:8-jre-alpine")
                        }
                    }
                }
            }
        }

        stage('Build Image') {
            steps {
                sh "rm -rf oc-build && mkdir -p oc-build"
                sh "cp src/main/docker/Dockerfile src/main/docker/entrypoint.sh  oc-build/"
                sh "cp build/libs/*-SNAPSHOT.war oc-build/app.war"
                script {
                    openshift.withCluster() {
                        openshift.withProject(devProject) {
                            openshift.selector("bc", appName).startBuild("--from-dir=oc-build/", "--wait=true", "--follow")
                            openshift.tag("${devProject}/${appName}:latest", "${devProject}/${appName}:${version}")
                        }
                    }
                }
            }
        }

        stage('Deploy DEV') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(devProject) {
                            if (!openshift.selector('dc', appName + "-mysqldb").exists()) {
                                def db = openshift.process("-f", "src/main/docker/openshift/tie-mysql.yml", "-p", "DB_APP_NAME=${appName}-mysqldb", "-p", "DATABASE_NAME=${appName}")
                                def dbCreate = openshift.create(db)
                                echo "Instantiated DB: ${dbCreate.names()}"
                            }
                            if (openshift.selector('dc', appName).exists()) {
                                openshift.selector('dc', appName).delete()
                                openshift.selector('svc', appName).delete()
                                openshift.selector('route', appName).delete()
                            }
                            def deployment = openshift.process("-f", "src/main/docker/openshift/tie-deployment.yml", "-p", "APP_NAME=${appName}", "-p", "DOCKER_IMAGE=${dockerRegistry}/${devProject}/${appName}:${version}")
                            def deploymentCreated = openshift.create(deployment)
                            echo "Instantiated Deployment: ${deploymentCreated.names()}"
                        }
                    }
                }
            }
        }

        stage('Promote to STAGE?') {
            steps {
                timeout(time:15, unit:'MINUTES') {
                    input message: "Promote to STAGE?", ok: "Promote"
                }
                script {
                    openshift.withCluster() {
                        if(!openshift.selector("project", stageProject).exists()) {
                            openshift.newProject(stageProject)
                        }
                        openshift.tag("${devProject}/${appName}:latest", "${stageProject}/${appName}:${version}")
                        openshift.withProject(stageProject) {
                            if (!openshift.selector('dc', appName + "-mysqldb").exists()) {
                                def db = openshift.process("-f", "src/main/docker/openshift/tie-mysql.yml", "-p", "DB_APP_NAME=${appName}-mysqldb", "-p", "DATABASE_NAME=${appName}")
                                def dbCreate = openshift.create(db)
                                echo "Instantiated DB: ${dbCreate.names()}"
                            }
                            if (openshift.selector('dc', appName).exists()) {
                                openshift.selector('dc', appName).delete()
                                openshift.selector('svc', appName).delete()
                                openshift.selector('route', appName).delete()
                            }
                            def deployment = openshift.process("-f", "src/main/docker/openshift/tie-deployment.yml", "-p", "APP_NAME=${appName}", "-p", "DOCKER_IMAGE=${dockerRegistry}/${stageProject}/${appName}:${version}")
                            def deploymentCreated = openshift.create(deployment)
                            echo "Instantiated Deployment: ${deploymentCreated.names()}"
                        }
                    }
                }
            }
        }
    }
}
