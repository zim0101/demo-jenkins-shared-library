#!groovy

/**
 * Jenkins Pipeline for testing Python projects using different Python versions.
 *
 * This Jenkins Pipeline defines a workflow for testing Python projects. It runs tests using
 * multiple Python versions in dynamic Docker containers.
 *
 * @param Map config A map containing configuration parameters for the pipeline.
 *     - pipeline: A string indicating the pipeline type. Must be set, and only "PYTHON" is supported.
 *     - checkVersion: A boolean indicating whether to check the Python version before running tests.
 *         Defaults to 'true'.
 *     - pythonVersions: A list of strings indicating the Docker image tags for different Python versions.
 *         Defaults to ['ubuntu-python:3.10'] if not specified.
 *
 * @throws AssertionError if the 'pipeline' parameter is missing or different from "PYTHON".
 * @throws groovy.lang.MissingPropertyException if any required configuration parameter is missing.
 * @throws groovy.lang.MissingMethodException if the 'error' method is not available (check Groovy version).
 *
 * @returns A Jenkins pipeline that runs tests using multiple Python versions in dynamic Docker containers.
 */
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

