def call(Map config) {

    assert config.pipeline : 'pipeline must be set!'

    if (config.pipeline != "PYTHON") {
        error "Unsupported pipeline " + config.pipeline
    }

    Map defaults = [
        check_version: true,
        python_versions: ['ubuntu-python:3.10', 'ubuntu-python:3.11.4']
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
                                def items = ['ubuntu-python:3.10', 'ubuntu-python:3.11.4']
                                for (def item : items) {
                                    docker.image(item).inside {
                                        stage("Processing ${item}") {
                                            sh(script: "python3 --version")
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
