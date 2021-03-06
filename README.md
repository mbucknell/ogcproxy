# Water Quality Portal OGC Proxy
[![Build Status](https://travis-ci.org/NWQMC/ogcproxy.svg?branch=master)](https://travis-ci.org/NWQMC/ogcproxy)
[![codecov](https://codecov.io/gh/NWQMC/ogcproxy/branch/master/graph/badge.svg)](https://codecov.io/gh/NWQMC/ogcproxy)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1d214ec705a841aaa9a2046d4f4b0815)](https://www.codacy.com/app/usgs_wma_dev/ogcproxy?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=NWQMC/ogcproxy&amp;utm_campaign=Badge_Grade)

## Development
This is a Spring Boot project. All of the normal caveats relating to a Spring Boot application apply.

### Dependencies
This application retrieves site data from the WQP-WQX-Services Station/search service in order to generate shapefiles.
The shapefiles are then sent to a geoserver instance via the geoserver's REST API.

### Environment Variables
This application has numerous environment variables to define the actual runtime environment. They can be defined in 
multiple locations depending on how you choose to run the application.

*   Docker Compose: The substitution variables in docker-compose.yml should be defined in a .env file located in the project's root directory. Example
```env
SERVER_CONTEXT_PATH=/ogcservices
SERVER_PORT=8080

WQP_GEOSERVER_HOST=<url of geoserver instance>
WQP_GEOSERVER_PASSWORD=<geoserver admin password>

WQP_LAYERBUILDER_URL=<url of Station/search service>
```

If the above definitions for SERVER_CONTEXT_PATH and SERVER_PORT are used with the docker-compose script, the application 
will be available at localhost:8080/ogcservices

*   Command line or IDE: Create an application.yml file located in the project's root directory containing the substituation variabels. 
Example:
```yaml
SERVER_CONTEXT_PATH: /ogcservices
SERVER_PORT: 8080

WQP_GEOSERVER_HOST: <url of geoserver instance>
WQP_GEOSERVER_PASSWORD: <geoserver admin password>

WQP_LAYERBUILDER_URL: <url of Station/search service>

LAYERBUILDER_WORKING_DIR: </path/to/data/working>
LAYERBUILDER_SHAPEFILES_DIR: </path/to/data/shapefiles>
```