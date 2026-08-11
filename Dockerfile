FROM eclipse-temurin:8-jdk-focal as qdar-builder

# 1. Setup Environment
ENV NVM_DIR /root/.nvm
ENV NODE_VERSION 14.20.0
RUN mkdir /output
RUN mkdir /dependencies
WORKDIR /app

SHELL ["/bin/bash", "-c"]

# 2. Install System Tools
RUN apt-get update && apt-get install -y \
    git curl jq maven \
    && rm -rf /var/lib/apt/lists/*

# 3. Install NVM & Node 15
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash \
    && . $NVM_DIR/nvm.sh \
    && nvm install $NODE_VERSION \
    && nvm alias default $NODE_VERSION \
    && nvm use default

# Add Node to PATH for subsequent RUN commands
ENV PATH $NVM_DIR/versions/node/v$NODE_VERSION/bin:$PATH

# 4. Copy Source Code
COPY . .

# 5. Run the Build
RUN chmod +x ./load-codebase.sh
RUN chmod +x ./qdar-build.sh
RUN chmod +x ./dependencies.sh
RUN ./qdar-build.sh -q $(pwd) -o /output -d /dependencies


# Final: Isolate the single output folder
FROM scratch AS export-stage
COPY --from=qdar-builder /output .

FROM tomcat:9.0.108-jdk8-temurin-noble AS deploy
RUN rm -rf /usr/local/tomcat/webapps/*
RUN rm -rf /usr/local/tomcat/webapps.dist
RUN sed -i '/<\/web-app>/i \
    <error-page>\n\
      <exception-type>java.lang.Throwable<\/exception-type>\n\
      <location>/error.html<\/location>\n\
    <\/error-page>\n \
    <error-page>\n\
      <error-code>0<\/error-code>\n\
      <location>/error.html<\/location>\n\
    <\/error-page>\n' /usr/local/tomcat/conf/web.xml
COPY --from=qdar-builder /output/qdar.war /usr/local/tomcat/webapps/qdar.war
