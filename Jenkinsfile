def version = "latest"
def appName = $ { env.APP_NAME }
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
//        stage('clean') {
//            steps {
//                sh "gradle clean --no-daemon"
//            }
//        }
//        stage('npm install') {
//            steps {
//                sh "gradle npm_install -PnodeInstall --no-daemon"
//            }
//        }
//        stage('Run Tests') {
//            parallel {
//                stage('backend tests') {
//                    steps {
//                        script {
//                            try {
//                                sh "gradle test -PnodeInstall --no-daemon"
//                            } catch (err) {
//                                throw err
//                            } finally {
//                                junit '**/build/**/TEST-*.xml'
//                            }
//                        }
//                    }
//                }
//
//                stage('frontend tests') {
//                    steps {
//                        script {
//                            try {
//                                sh "gradle npm_run_test-ci -PnodeInstall --no-daemon"
//                            } catch (err) {
//                                throw err
//                            } finally {
//                                junit '**/build/test-results/jest/TESTS-*.xml'
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        stage('packaging') {
//            steps {
//                sh "gradle bootWar -x test -Pprod -PnodeInstall --no-daemon"
//                //   archiveArtifacts artifacts: '**/build/libs/*.war', fingerprint: true
//            }
//        }
//
//        stage('Create Image Builder') {
//            when {
//                expression {
//                    openshift.withCluster() {
//                        openshift.withProject(env.DEV_PROJECT) {
//                            return !openshift.selector("bc", appName).exists();
//                        }
//                    }
//                }
//            }
//            steps {
//                script {
//                    openshift.withCluster() {
//                        openshift.withProject(env.DEV_PROJECT) {
//                            openshift.newBuild("--name=tie", "--strategy docker", "--binary=true", "--docker-image openjdk:8-jre-alpine")
//                        }
//                    }
//                }
//            }
//        }
//
//        stage('Build Image') {
//            steps {
//                sh "rm -rf oc-build && mkdir -p oc-build"
//                sh "cp src/main/docker/Dockerfile src/main/docker/entrypoint.sh  oc-build/"
//                sh "cp build/libs/*-SNAPSHOT.war oc-build/app.war"
//                script {
//                    openshift.withCluster() {
//                        openshift.withProject(env.DEV_PROJECT) {
//                            openshift.selector("bc", appName).startBuild("--from-dir=oc-build/", "--wait=true", "--follow")
//                        }
//                    }
//                }
//            }
//        }

        stage('Deploy STAGE') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject(env.STAGE_PROJECT) {
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

                            def deployment = openshift.process("-f", "src/main/docker/openshift/tie-deployment.yml", "-p", "APP_NAME=${appName}", "-p", "DOCKER_IMAGE=DOCKER_IMAGE=${env.DEV_PROJECT}/${appName}:latest")
                            def deploymentCreated = openshift.create(deployment)
                            echo "Instantiated Deployment: ${deploymentCreated.names()}"

                            openshift.newApp("${appName}:${version}").narrow("svc").expose()
                            openshift.set("probe dc/tasks --readiness --get-url=http://:8080/ --initial-delay-seconds=20 --failure-threshold=10 --period-seconds=10")
                            openshift.set("probe dc/tasks --liveness  --get-url=http://:8080/ --initial-delay-seconds=60 --failure-threshold=10 --period-seconds=10")
                        }
                    }
                }
            }
        }


        stage('Promote to STAGE?') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    input message: "Promote to STAGE?", ok: "Promote"
                }
                script {
                    openshift.withCluster() {
                        openshift.tag("${env.DEV_PROJECT}/${appName}:latest", "${env.STAGE_PROJECT}/${appName}:${version}")
                    }
                }
            }
        }
    }
}
