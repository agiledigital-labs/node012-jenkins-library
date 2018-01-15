#
# WWW container
#
# This container supports static WWW applications

# www container contains:
# node

FROM mhart/alpine-node:0.12
MAINTAINER Agile Digital <info@agiledigital.com.au>

RUN apk update && apk add bash && apk add openjdk7-jre curl

# Install envplate for stamping out the conf files at run time.
RUN curl -sLo /usr/local/bin/ep https://github.com/kreuzwerker/envplate/releases/download/v0.0.8/ep-linux && chmod +x /usr/local/bin/ep

WORKDIR /usr/src/app

COPY app/package.json /usr/src/app/

RUN npm install

RUN npm install -g grunt-cli

# Must match the port in server.js
EXPOSE 8000

COPY app /usr/src/app/

RUN chmod +x /usr/src/app/run.sh

COPY assets /usr/src/assets/

COPY config /usr/src/config/

COPY generate_config_json.sh /var/lib/confy/bin/

RUN chmod +x /var/lib/confy/bin/generate_config_json.sh

COPY confy-assembly-2.3.jar /var/lib/confy/bin/

CMD [ "/usr/src/app/run.sh" ]