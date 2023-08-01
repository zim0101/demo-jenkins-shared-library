#!groovy

def call(Map config) {
    assert config.pipeline : 'pipeline must be set!'
    if (config.pipeline != 'PYTHON') {
        error 'Unsupported pipeline ' + config.pipeline
    }

    Map defaults = [
        checkVersion: true,
        pythonVersions: ['ubuntu-python:3.10']
    ]
    config = defaults + config

    pipeline {
        agent any
        stages {
            stage('tests') {
                steps {
                    script {
                        def pythonVersions = config.pythonVersions
                        for (def pythonVersion : pythonVersions) {
                            docker.image(pythonVersion).inside {
                                stage("test-on-${pythonVersion}") {
                                    if (config.checkVersion == true) {
                                        sh(script: 'python3 --version')
                                    }
                                    sh(script: 'pip install .')
                                    sh(script: 'pytest')
                                }
                            }
                        }
                    }
                }
            }
        }
        post {
            cleanup {
                cleanWs()
            }
        }
    }
}

