#!groovy

def validateConfig(Map config) {
    assert config.pipeline : 'pipeline must be set!'
    if (config.pipeline != "PYTHON") {
        error "Unsupported pipeline " + config.pipeline
    }
}

def executeTestCommands(boolean checkVersion) {
    if (checkVersion == true) {
        sh(script: "python3 --version")
    }
    sh(script: "pip install .")
    sh(script: "pytest")
}

def testOnMultiplePythonImage(String pythonVersions) {
    for (def pythonVersion : pythonVersions) {
        docker.image(pythonVersion).inside {
            stage("test-on-${pythonVersion}") {
                executeTestCommands(config.checkVersion)
            }
        }
    }
}

def call(Map config) {
    validateConfig(config)
    Map defaults = [
        checkVersion: true,
        pythonVersions: ['ubuntu-python:3.10']
    ]
    config = defaults + config

    pipeline {
        agent any
        stages {
            stage('tests') {
                stages {
                    stage('test-on-dynamic-docker-image') {
                        steps {
                            testOnMultiplePythonImage(config.pythonVersions)
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
