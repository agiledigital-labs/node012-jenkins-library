'use strict';

/**
 * Defines the tasks that will get run prior to the application being served
 * by node.
 *
 * These tasks process the environment.conf file in the config directory and
 * converts it into an angular module that provides the safe (properties whose
 * key starts with 'safe') properties at runtime.
 */
module.exports = function (grunt) {
  grunt.initConfig({
    exec: {
      generate_config_json: {
        command: '/var/lib/confy/bin/generate_config_json.sh /usr/src/config/environment.conf /tmp/project.json',
        callback: function () {

        }
      }
    },
    ngconstant: {
      options: {
        name: 'config',
        dest: '/usr/src/assets/scripts/config.js',
        constants: function () {
          return {
            projectSettings: grunt.file.readJSON('/tmp/project.json').safe
          };
        },
        values: {
        }
      },
      build: {}
    }
  });

  grunt.loadNpmTasks('grunt-exec');
  grunt.loadNpmTasks('grunt-ng-constant');

  grunt.registerTask('default', ['exec', 'ngconstant']);
};
