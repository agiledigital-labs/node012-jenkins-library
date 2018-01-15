/*
 * Toolform-compatible builder for NodeJS Express 4 based components using Yarn
 */
/*
 * Toolform-compatible Jenkins build step for NodeJS 0.12 based components built using Grunt
 * Expects the following scripts:
 *  - grunt clean;
 *  - grunt test; producing junit compatible test results
 *  - grunt build;
 */

def call(Map config) {
  final npm = { cmd ->
    ansiColor('xterm') {
      dir(baseDir) {
        sh "npm ${cmd}"
      }
    }
  }

  final bower = { cmd ->
    ansiColor('xterm') {
      dir(baseDir) {
        sh "bower ${cmd}"
      }
    }
  }

  final grunt = { cmd ->
    ansiColor('xterm') {
      dir(baseDir) {
        sh "grunt ${cmd}"
      }
    }
  }

  stage('Verify Environment') {
    // Check node deps
    assert sh(script: 'node --version', returnStdout: true).trim() == "v4.8.7": "expected node version 4.8.7"
    assert sh(script: 'npm --version', returnStdout: true).trim() == "2.15.11": "expected npm version 2.15.9"
    assert sh(script: 'bower --version', returnStdout: true).trim() == "1.8.2": "expected bower version 1.8.2"
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
    junit "$baseDir/test-output/**/*.xml"
  }
  stage('Build artifacts') {
    grunt "build"
  }
  stage('Copy artifacts to staging area') {
    sh "mkdir -p docker/app"
    sh "mkdir -p docker/assets"
    sh "mkdir -p docker/config"
    sh "cp -r \"${baseDir}/dist\" docker/assets"
    sh "cp *.conf docker/config/"
  }

  stage('Generate Dockerfile and associated scripts') {
    dir('docker') {
      writeFile(file: "Dockerfile", 
        text: libraryResource('au/com/agiledigital/jenkins-pipelines/build-npm-grunt04/Dockerfile'))
      writeFile(file: "confy-assembly-2.3.jar", 
        text: libraryResource('au/com/agiledigital/jenkins-pipelines/build-npm-grunt04/confy-assembly-2.3.jar'))
      writeFile(file: "generate_config_json.sh", 
        text: libraryResource('au/com/agiledigital/jenkins-pipelines/build-npm-grunt04/generate_config_json.sh'))
      writeFile(file: "app/Gruntfile.js", 
        text: libraryResource('au/com/agiledigital/jenkins-pipelines/build-npm-grunt04/app/Gruntfile.js'))
      writeFile(file: "app/package.json", 
        text: libraryResource('au/com/agiledigital/jenkins-pipelines/build-npm-grunt04/app/package.json'))
      writeFile(file: "app/run.sh", 
        text: libraryResource('au/com/agiledigital/jenkins-pipelines/build-npm-grunt04/run.sh'))
      writeFile(file: "app/server.js.envplate", 
        text: libraryResource('au/com/agiledigital/jenkins-pipelines/build-npm-grunt04/app/server.js.envplate'))
     
      sh 'chmod +x generate_config_json.sh'
      sh 'chmod +x app/run.sh'
    }
    stage('Archive to Jenkins') {
      sh "zip -r artifacts.zip docker"
      sh "find docker"
      archiveArtifacts "artifacts.zip"

    }
    stage('Build docker image') {
      dir('docker') {
        sh "docker build -t ${dockerImageName} ."
      }
    }
    stage('Push to docker registry') {
      sh "docker tag ${config.dockerImageName}:latest ${config.dockerRegistry}/${config.dockerImageName}:git-sha-${gitCommitHash}"
      sh "docker tag ${config.dockerImageName}:latest ${config.dockerRegistry}/${config.dockerImageName}:${buildImageTag}"
      sh "docker push ${config.dockerRegistry}/${config.dockerImageName}:git-sha-${gitCommitHash}"
      sh "docker push ${config.dockerRegistry}/${config.dockerImageName}:${buildImageTag}"
    }
  }