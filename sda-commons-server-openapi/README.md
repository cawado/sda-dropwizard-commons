# SDA Commons OpenApi

The module `sda-commons-server-swagger` is the base module to add
[Swagger](https://github.com/swagger-api/swagger-core) support for applications in the
SDA infrastructure.
This package produces [OpenApi 3.0 definitions](https://swagger.io/docs/specification/basic-structure/).

## Usage

...

### Documentation Location
 
The Swagger documentation base path is dependant on DropWizard's [server.rootPath](https://www.dropwizard.io/0.9.1/docs/manual/configuration.html#man-configuration-all):

- as JSON: ```<server.rootPath>/swagger.json``` 
- as YAML: ```<server.rootPath>/swagger.yaml```

### Customizaton Options
