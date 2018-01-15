#!/bin/sh

# Perform environment var substitution.
cp ./server.js.envplate ./server.js
/usr/local/bin/ep -v ./server.js

grunt && node server.js
