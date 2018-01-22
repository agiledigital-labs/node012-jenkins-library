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


  container('build-grunt-node012') {

    stage('temp testing') {
      sh "echo Project:   ${project}"
      sh "echo Component: ${component}"
      sh "echo BuildNumber: ${buildNumber}"
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

    stage('Build artifacts') {
      grunt "build"
    }

    stage('Copy artifacts to staging area') {
      def artifactDir = "${project}-${component}-artifacts"
      sh "mkdir -p ${artifactDir}/assets"
      sh "mkdir -p ${artifactDir}/config"
      sh "cp -r \"${config.baseDir}/dist\" ${artifactDir}/assets"
      sh "cp *.conf ${artifactDir}/config/"
    }

  }

  stage('Archive to Jenkins') {
    def artifactDir = "${project}-${component}-artifacts"
    def zipName = "${project}-${component}-${buildNumber}.zip"
    sh "zip -r \"${zipName}\" \"${artifactDir}\""
    archiveArtifacts zipName
  }

}
