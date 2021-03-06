# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [Unreleased](https://github.com/NWQMC/ogcproxy/compare/ogcproxy-1.1.0...master)
### Changed
-   Use new deployment pipeline library

## [1.1.0](https://github.com/NWQMC/ogcproxy/compare/ogcproxy-0.9.1...ogcproxy-1.1.0)
### Added
-   CHANGELOG.md
-   README.md
-   Converted project to Spring Boot 2.
-   OAuth2 Security for Internal App.
-   Jenkins configuration

### Changed
-   Dockerized ogcproxy
-   Added jenkins deploy script
-   Using url environment variables for geoserver and the layerbuilder rather than defining separate host, port variables.