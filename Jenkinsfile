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
                sh "chmod +x gradlew"
                sh "./gradlew clean --no-daemon"
            }
        }
        stage('npm install') {
            steps {
                sh "./gradlew npm_install -PnodeInstall --no-daemon"
            }
        }

        stage('backend tests') {
            steps {
                try {
                    sh "./gradlew test -PnodeInstall --no-daemon"
                } catch (err) {
                    throw err
                } finally {
                    junit '**/build/**/TEST-*.xml'
                }
            }
        }

        stage('frontend tests') {
            steps {
                try {
                    sh "./gradlew npm_run_test-ci -PnodeInstall --no-daemon"
                } catch (err) {
                    throw err
                } finally {
                    junit '**/build/test-results/jest/TESTS-*.xml'
                }
            }
        }

        stage('packaging') {
            steps {
                sh "./gradlew bootWar -x test -Pprod -PnodeInstall --no-daemon"
                archiveArtifacts artifacts: '**/build/libs/*.war', fingerprint: true
            }
        }
    }
}
