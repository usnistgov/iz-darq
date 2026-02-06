FROM eclipse-temurin:8-jdk-focal

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