# Data At Rest Quality Analysis (QDAR)

QDAR provides infrastructure to sample, validate, and analyze the quality of immunization data from an Immunization Information System (IIS). It includes a CLI for data extract processing and a WebApp for visualization, analysis and management.

## Project Structure

This is a multi-module Maven project (`gov.nist.healthcare.iz:darq`) containing:

* **Core Modules:** `darq-extract-process`, `darq-analyzer`, `darq-patient-matching`, `darq-common`
* **Web Application:** `darq-webapp` (Angular 9 frontend + Spring Boot backend)
* **Clients:** `darq-aart-client`, `darq-cli-app`
* **Utilities:** `file-parser`, `fake-record-generator`, `crypto-helper`

## Prerequisites

* **Java:** JDK 8
* **Node.js:** v14.20.0
* **Maven:** 3.6+
* **Docker:** Required for containerized builds.

## Configuration

### NPM Authentication
The frontend depends on `@usnistgov/ngx-dam-framework-legacy` hosted on GitHub Packages.
1.  Generate a GitHub Personal Access Token (PAT) with `read:packages` scope.
2.  Authenticate your NPM client (via `.npmrc`) to pull packages from the `@usnistgov` scope.

### Code Sets
The system requires AIRA Compiled Code Sets (`Compiled.xml`).
* **Management:** Handled by `load-codebase.sh`.
* **Version:** Determined by the commit SHA in `codebase-compiled-xml.sha`.

## Build Instructions

### Docker Build (Recommended)
Builds the full toolchain (CLI, WebApp, and dependencies) in an isolated container.

```bash
# Usage: ./docker-build.sh <output-directory>
sh ./docker-build.sh ./dist

```

Artifacts (WARs/JARs) will be placed in the specified output directory.

### Manual Build

To build modules individually without Docker:

**1. Backend (Maven)**

```bash
mvn clean install

```

**2. Frontend (Angular)**
Located in `darq-webapp/qdar-analysis-client`.

```bash
cd darq-webapp/qdar-analysis-client
npm install
npm run build-prod

```

## Development

### Running the WebApp Locally

The frontend uses a proxy configuration to communicate with the API.

```bash
cd darq-webapp/qdar-analysis-client
npm start
# Runs: ng serve --proxy-config proxy.conf.json

```

## Release Management

Versioning is managed via `qdar-versions.sh`. This updates the Maven POMs and properties for specific project components.

**Usage:**

```bash
sh ./qdar-versions.sh -q <project-root> -t <target> -v <version>

```

**Targets (`-t`):**

* `api`: Updates root POM and API version property.
* `webapp`: Updates `darq-webapp` and web tool property.
* `cli`: Updates `darq-extract-process/darq-cli-app`.
* `client`: Updates `darq-aart-client`.
* `mqe`: Updates the MQE dependency version property.

**Example:**

```bash
sh ./qdar-versions.sh -q . -t webapp -v 3.2.0
mvn versions:commit

```

## External Dependencies

The project builds specific versions of the following tools from source (defined in `dependencies.json`):

* **Vaccine Deduplication:** `usnistgov/vaccination_deduplication`
* **Lonestar Forecaster:** `immregistries/LoneStarVaccineForecaster`
* **MISMO:** `immregistries/mismo-match`
* **MQE Suite:** `codebase-client`, `mqe-hl7-util`, `mqe-validator`

This project uses custom scripting to manage external source dependencies and standard vocabulary files to ensure deterministic builds.

### External Source Dependencies
To guarantee exact version matching, dependencies are built from source rather than pulled from public artifact repositories.

* **Manifest (`dependencies.json`):** Defines the source repository, specific Git SHA-1 commit hash, and the build command (typically Maven) for each dependency.
* **Builder (`dependencies.sh`):** A script that parses the JSON manifest to:
    1. Clone the repository to the target directory.
    2. Checkout the exact SHA-1 hash defined in the manifest.
    3. Execute the specific build command (e.g., `mvn clean install`).

### Code Set Loading (`load-codebase.sh`)
The application relies on the AIRA `Compiled.xml` file for standard code sets and validation rules. This file is fetched dynamically rather than stored in the repo.

* **Versioning:** The version is determined by the specific commit hash found in the `codebase-compiled-xml.sha` file.
* **Execution:** The script downloads the `Compiled.xml` file from the remote `immregistries/codebase` repository at that specific commit and installs it into the resource directories for both the CLI (`darq-cli-app`) and WebApp (`darq-webapp`).