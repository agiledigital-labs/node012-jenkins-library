/*
 * Toolform-compatible Jenkins 2 Pipeline build step for NodeJS 0.12 based components built using Grunt
 * Expects the following scripts:
 *  - grunt clean;
 *  - grunt test; producing junit compatible test results
 *  - grunt build;
 */

def call(Map config) {
  final npm = { cmd ->
    ansiColor('xterm') {
      dir(config.baseDir) {
        sh "npm ${cmd}"
      }
    }
  }

  final bower = { cmd ->
    ansiColor('xterm') {
      dir(config.baseDir) {
        sh "bower ${cmd}"
      }
    }
  }

  final grunt = { cmd ->
    ansiColor('xterm') {
      dir(config.baseDir) {
        sh "grunt ${cmd}"
      }
    }
  }

  def artifactDir = "${config.project}-${config.component}-artifacts"

  container('node012-builder') {

    stage('Build Details') {
      sh "echo Project:   ${config.project}"
      sh "echo Component: ${config.component}"
      sh "echo BuildNumber: ${config.buildNumber}"
    }

    stage('Verify Environment') {
      // Check node deps
      assert sh(script: 'node --version', returnStdout: true).trim() == "v0.12.18": "expected node version 0.12.18"
      assert sh(script: 'npm --version', returnStdout: true).trim() == "2.15.11": "expected npm version 2.15.11"
      assert sh(script: 'bower --version', returnStdout: true).trim() == "1.8.2": "expected bower version 1.8.2"
      assert sh(script: 'grunt --version', returnStdout: true).trim() == "grunt-cli v1.2.0": "expected grunt version 1.2.0"
    }

    stage('Fetch dependencies') {
      bower "--allow-root install"
      npm "install"
    }

    stage('Clean') {
      grunt "clean"
    }

    stage('Test') {
      grunt "test"
      junit "${config.baseDir}/PhantomJS*/test-output/**/*.xml"
    }

  }

  if(config.stage == 'dist') {

    container('node012-builder') {
      stage('Build artifacts') {
        grunt "build"
      }

      stage('Copy artifacts to staging area') {
        sh "mkdir -p ${artifactDir}/assets"
        sh "mkdir -p ${artifactDir}/config"
        sh "cp -r \"${config.baseDir}/dist\" ${artifactDir}/assets"
        sh "cp *.conf ${artifactDir}/config/"
      }
    }

    stage('Archive to Jenkins') {
      def tarName = "${config.project}-${config.component}-${config.buildNumber}.tar.gz"
      sh "tar -czvf \"${tarName}\" -C \"${artifactDir}\" ."
      archiveArtifacts tarName
    }

  }

}
