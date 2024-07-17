# voloOk apiGateway
voloOk apiGateway is an apiGateway configured with springboot and grpc used by voloOk client to access the system business logic by http protocol.

### Environment Configuration
The following params must be specified in the src/main/resources/env.properties file (as specified in the env.properties.example):
* APPLICATION_NAME: the name of your application;
* GRPC_SERVER_PORT: the api-gateway grpc port(where the microservices response to api-gateway requests)

####Grpc microservices configurations

For each grpc microservice the api-gateway have to communicate with, it's necessary to set the following configurations.

1. set the following parameters in the env.properties:

    * GRPC_GENERIC_CLIENT_ADDRESS: it contains the ip address and port of the server microservice; (example: static://127.0.0.1:5000)
    * GRPC_GENERIC_CLIENT_NEGOTIATION_TYPE: it specifies the client negotiation type; (example: plaintext)
    
2. map the previous parameters in the application.properties as follow:
    * grpc.client.generic-client-name.address=${GRPC_GENERIC_CLIENT_ADDRESS}
    * grpc.client.generic-client-name.negotiation-type=${GRPC_GENERIC_CLIENT_NEGOTIATION_TYPE}

####Stub generation

1. Launch the Maven clean command.
2. Launch the Maven install command.
3. Build the project as a war and use an Apache Tomcat for the deploy.
