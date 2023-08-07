#!groovy
package com.zim0101.jenkins.pipeline

def execute(String pythonVersion) {
    catchError () {
        ansiColor('xterm') {
            sh '''#!/usr/bin/env bash
                set -ex
                cd ${pythonVersion}
                pip install .
                pytest
            '''
        }
    }
}
