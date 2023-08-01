#!groovy

def validateConfig(Map config) {
    assert config.pipeline : 'pipeline must be set!'
    if (config.pipeline != "PYTHON") {
        error "Unsupported pipeline " + config.pipeline
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
                            script {
                                def pythonVersions = config.pythonVersions ?: defaults.pythonVersions
                                for (def pythonVersion : pythonVersions) {
                                    docker.image(pythonVersion).inside {
                                        stage("test-on-${pythonVersion}") {
                                            if (config.checkVersion == true) {
                                                sh(script: "python3 --version")
                                            }
                                            sh(script: "pip install .")
                                            sh(script: "pytest")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
