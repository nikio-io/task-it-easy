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
               // sh "chmod +x gradlew"
                sh "gradle clean --no-daemon"
            }
        }
        stage('npm install') {
            steps {
                sh "gradle npm_install -PnodeInstall --no-daemon"
            }
        }
        stage('Run Tests') {
            parallel {
                stage('backend tests') {
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

                stage('frontend tests') {
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

        stage('packaging') {
            steps {
                sh "gradle bootWar -x test -Pprod -PnodeInstall --no-daemon"
                archiveArtifacts artifacts: '**/build/libs/*.war', fingerprint: true
            }
        }

        stage('Create Image Builder') {
            when {
                expression {
                    openshift.withCluster() {
                        openshift.withProject("tie-dev") {
                            return !openshift.selector("bc", "tie").exists();
                        }
                    }
                }
            }
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject("tie-dev") {
                            openshift.newBuild("--name=tie", "--strategy docker", "--binary=true", "--docker-image centos:centos7")
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
                        openshift.withProject("tie-dev") {
                            openshift.selector("bc", "tie").startBuild("--from-dir=oc-build/", "--wait=true", "--follow")
                        }
                    }
                }
            }
        }

    }
}
