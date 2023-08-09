#!groovy
package com.zim0101.jenkins.pipeline

def execute(String pythonVersion) {
    catchError () {
        ansiColor('xterm') {
            sh """#!/usr/bin/env bash
                set -ex
                mkdir ${pythonVersion}
                cd ${pythonVersion}
                python3 -m venv env
                source env/bin/activate
                pip install .
                cd ../
                pytest
            """
        }
    }
}
