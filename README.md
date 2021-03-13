[![Quarkus](docs/images/quarkus-logo.png)](https://quarkus.io/)

# Quarkus PetClinic Sample Application using AWS Lambda

This project is a modified version
of [mpbauer/quarkus-petclinic-rest](https://github.com/mpbauer/quarkus-petclinic-rest) and splits the monolithic app
into several modules by using the `Services Pattern` according to
this [article](https://www.serverless.com/blog/serverless-architecture-code-patterns). Each module can de deployed
individually.

## Understanding the Spring Petclinic application with a few diagrams

[See the presentation of the Spring Petclinic Framework version](http://slideshare.net/AntoineRey/spring-framework-petclinic-sample-application)

A local copy of the presentation can be found [here](docs/misc/springframeworkpetclinic-presentation.pdf)

### Petclinic ER Model

![alt petclinic-ermodel](docs/diagrams/petclinic-ermodel.png)

## Running the petclinic application locally

You can run your application in dev mode that enables live coding using:

```shell script
mvn quarkus:dev
```

## Packaging and running the application with JVM

The application can be packaged using:

```shell script
mvn package
```

It produces the `quarkus-petclinic-rest-aws-lambda-<submodule>-1.0.0-SNAPSHOT-runner.jar` file in the `/target`
directory. Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.
Furthermore the included dependency `quarkus-amazon-lambda-http` produces a file called `function.zip` which contains
the application packaged and ready to deploy on
[AWS Lambda](https://aws.amazon.com/lambda/) as well as template files to deploy the application as a lambda function
with [AWS SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html).

## Creating a native executable

Creating a native executable requires [GraalVM](https://www.graalvm.org/). Make sure to install and configure a
compatible version of GraalVM before you try to build a native image. For more information on how to build native
executables check
out [this](https://quarkus.io/guides/building-native-image#:~:text=While%20Oracle%20GraalVM%20is%20available,install%20the%20Java%2011%20version.)
link.

You can create a native executable using:

```shell script
mvn package -Pnative
```

You can then execute your native executable with:

```shell script
./target/quarkus-petclinic-rest-1.0.0-SNAPSHOT-runner
```

**Optional:** If you don't have [GraalVM](https://www.graalvm.org/) installed, you can run the native executable build
in a container using:

```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

:exclamation: Building native images with `-Dquarkus.native.container-build=true` requires a running Docker
installation. Keep in mind that the `Docker` build will be performed in a Linux container and will not be executable on
a MacOS or Windows machine.

## Health Checks

The `smallrye-health` dependency provides health checks out of the box. The following endpoints are provided:

- `/q/health/live` - The application is up and running (liveness probe)
- `/q/health/ready` - The application is ready to serve requests (readiness probe)
- `/q/health` - Accumulating all health check procedures in the application
- `q/health-ui` - Provides a graphical user interface for health information

Example:

```
http://localhost:8080/petclinic/q/health
```

## Metrics

The `smallrye-metrics` dependency provides metrics which can be accessed through the `/q/metrics` endpoint.

Example:

```
http://localhost:8080/petclinic/q/metrics
```

## Database configuration

The database support for this version of
the [spring-petlinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest) project was significantly reduced.
As of now this project only supports [PostgreSQL](https://www.postgresql.org/)
and [H2](https://www.h2database.com/html/main.html).

In its default configuration a `PostgreSQL` database is required to run the application. For the execution of tests an
embedded `H2` is started.

For local development you may want to start a `PostgreSQL` database with `Docker`:

````
docker run --name petclinic -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
````

## Security configuration

A Role Based Access Control is enabled by default when running the application with the `prod` and `test` profile. When
you start the application in `dev` through `./mvnw quarkus:dev` authentication is disabled by default.

### Disable Authentication

In order to disable access control, you can turn it off by setting the following property in
the `application.properties` file:

```
petclinic.security.enable=false
```

### Authorization

This will secure all APIs and in order to access them, basic authentication is required. Apart from authentication, APIs
also require authorization. This is done via roles that a user can have. The existing roles are listed below with the
corresponding permissions:

Role         | Controller
----------   | ----------------
OWNER_ADMIN  | OwnerController<br/>PetController<br/>PetTypeController (`getAllPetTypes()` & `getPetType()`)
VET_ADMIN    | PetTypeController<br/>SpecialityController</br>VetController
ADMIN        | UserController

### Roles Based Access Control with predefined JSON Web Tokens

To make the use of this sample application as easy as possible a set of fixed JSON Web Tokens with different roles were
generated and signed with a private key that is located in `src/test/resources/privateKey.pem`. You can copy the token
and pass it via the `Authorization` header to the application.

**Example:**

```shell
curl --location --request GET 'http://localhost:8080/petclinic/api/owners' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJncm91cHMiOlsiUk9MRV9PV05FUl9BRE1JTiJdLCJpYXQiOjE2MTQ4NTMxNDksImV4cCI6NDc3MDUyNjc0OSwianRpIjoiMGEwODZmZjItYjVmNC00MGQxLWEyZDUtMmM0YjA0ZTBkMDBhIiwiaXNzIjoiaHR0cHM6Ly9zcHJpbmctcGV0Y2xpbmljLmdpdGh1Yi5pby9pc3N1ZXIifQ.V0qEDupr5uOdi-re233jMEOWhP4w_yvCgWUrEapOcz-2WLe64fjvGFAXlpvdfjcslCGCSofB97CnQ8xzx0QPdWpDKN7E2b_JTYb7wFsrI71nIblw-2n7uyKFGKgSbjd4L7BNIbUP6Pcodgn1FsDZ6HPfJlEqWMf_ZEyZi9hA3qfPgpSgt9iQgW88gkyRZv7tJUwhr0ZY4qNB6ujbZtBF4Er0Wvm8VqR2_KK8PaKk2ydGnsTDmrPaXSVmTH0ZMMwMRrmM4OvT-WZdpcAzakA4adDBPC6URM_GIzKfVMZ3oVbUwBj6HOybbX8R9VSXegTfZTio1J-dF5fXlFYYILktpg'
```

> :exclamation: IMPORTANT: Never push a private key in a Git repository. In case you do, make sure do add additional
> security by encrypting security relevant data first before you push them. This application is just for showcasing
> the use of JSON Web Tokens in the Petclinic application but it should never be done this way for real applications.

#### List of valid JSON Web Tokens:

Role: `OWNER_ADMIN`

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJncm91cHMiOlsiUk9MRV9PV05FUl9BRE1JTiJdLCJzdWIiOiJYMDAwMDEiLCJpYXQiOjE2MTQ4Nzg2NjgsImV4cCI6NDc3MDU1MjI2OCwianRpIjoiZDM0ZjYwM2ItNjhiYy00NTZmLThlNDUtMGY2M2FkMTIxODc0IiwiaXNzIjoiaHR0cHM6Ly9zcHJpbmctcGV0Y2xpbmljLmdpdGh1Yi5pby9pc3N1ZXIifQ.r3xIQDYMlOVqj4dxJvHAIgh7XTafxeE0vJ2SkVlZng85lH-OM-sCGlpgy1uCjUKggmx3BmvX86F0GNfDVSfntfTUM_N4Uq27_wOZQjQYPCI3FxDub2kvhf0Wz59_Ed1Ip6TgkIVJaQrdCnfMr8F8eRbXZnKUwkcndb1Z_PLIp8JPP095VGyax_ezGiM1171t5UcrD4rUpmN5LYJvN_DGpfscf71Z7hG9KUPuspEO-Tld-7_hrPQkHh6FFkOiPbIirzWtgSmDaEjnijQoBhDxco8Y_wXolmwLnIhAJeT0zYE3eXmMKbU6H59799AGrGm2kU7hHuDkQEx0OretgxOmpA
```

Role: `VET_ADMIN`

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJncm91cHMiOlsiUk9MRV9WRVRfQURNSU4iXSwic3ViIjoiWDAwMDAyIiwiaWF0IjoxNjE0ODc4NjY5LCJleHAiOjQ3NzA1NTIyNjksImp0aSI6ImYzNDQxMjM0LWY1ZmYtNDdmYy1hMzgwLWVmMTNlZmU5ODBiNSIsImlzcyI6Imh0dHBzOi8vc3ByaW5nLXBldGNsaW5pYy5naXRodWIuaW8vaXNzdWVyIn0.nmn-DHn4Zkps2tqTz6hrsR94IO2Qfsbyio_EApEJ6BzReQ8wr4LBFHH391fWvYfFPXXg2_q1J8sL8RGRv76vfoR5S3BWCmrBkxkdGINS5R5r1xc7TljDyL2mX6a6MH1f8rUl_jZsieEwcpzZSB_d_aC1Uk8wCxS1DCpPWP2nLtzex_G1890V93wxoSVJpI9a9XcQhWPT-OkFU5gIw2H9GRTqFQGVYInEZv76oBUU0x52kSVqLuf-PJfFCaIEHnFdo8cxZOf4wOL6rXO0vEoV3tpzdKr1XNIgChgtW2gH16Di-r5_p9hc5zDgieYICgy8kEF4tA2yOuWtq3W2bOmutg
```

Role: `ADMIN`

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJncm91cHMiOlsiUk9MRV9BRE1JTiJdLCJzdWIiOiJYMDAwMDMiLCJpYXQiOjE2MTQ4Nzg2NjksImV4cCI6NDc3MDU1MjI2OSwianRpIjoiOWM0YjhmZGEtYjViYS00YmY0LWEzNjMtNGRhNzg3YWEyY2JjIiwiaXNzIjoiaHR0cHM6Ly9zcHJpbmctcGV0Y2xpbmljLmdpdGh1Yi5pby9pc3N1ZXIifQ.pFRf_BiIJyHVWynlZIi_MHZY5KDHu09VG4Eb4GrvFopure1cZiviva-yrrY7IdmwNkf1aM991YApCiFnjl9D40c0PG5G5_734LU5uhAKe8ACBAIVG8mWtiGbBpCAdg_ghWCjIhmP17xnpyTpfHeJLqeIDDbxJ75tFnFV4Lu1R-JyhdEZjX8Ulsls2sNde4iBOl1ZILT2CuBYueL070qcrowOBnh6POnHacp8Kt2ZSWq6wAqcgIJGYYY2S7JKau2Qv8ZF_lUmyP58pUYrwkgxl95zrQypjQaYjUVAJosayutjKJwEpDfptvo_9s0kgns1R2EF7XN8NyNCQxyO9Cdgyw
```

Roles: `OWNER_ADMIN`, `VET_ADMIN`, `ADMIN`

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJncm91cHMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVkVUX0FETUlOIiwiUk9MRV9PV05FUl9BRE1JTiJdLCJzdWIiOiJYMDAwMDQiLCJpYXQiOjE2MTQ4Nzg2NjksImV4cCI6NDc3MDU1MjI2OSwianRpIjoiZjUwM2FkMWItMTkyNy00ZDQ0LTlhNjAtOWU4ZThmNDM2YTA2IiwiaXNzIjoiaHR0cHM6Ly9zcHJpbmctcGV0Y2xpbmljLmdpdGh1Yi5pby9pc3N1ZXIifQ.w2-po_4b9S6Y08tNuv1dn0J0jyd84QiP2qTlWKgj3pGY9VUeXefIKRq9-MTyP-LvkDXNgCyQ6DbDRUt-A7uRdxiDiteirdvg0-7aRKDQBMWtJmEIDAVqZfz20q7ycGMmu2wOEPnDxtSyWJoHLacDIHDQPXyR2AnUHZ1weuFnlipI5OGjeFw7aqT5QXhnj8RwMpw77bqofgIWBTRLLe-VJUYrP8ARiqpoqfPUM6oiS7spSMvItu9IFKWpbzHd0aEHxjfMk6gI8E0q8FEsqgn9l60vV0_FGQNiEXorgGZmPDXSCCK29kgGesvCBk0RYBKrR7LkNEj3uAsXITZKAs9bMA
```

To see more details about the token you can decode on https://jwt.io

### Create your own JWT Tokens

If you don't want to use the already generated tokens you generate tokens with either your existing private key or by
creating a new keypair.

1) Generating Keys with OpenSSL

   It is also possible to generate a public and private key pair using the OpenSSL command line tool.

   openssl commands for generating keys
    ```shell
    openssl genrsa -out rsaPrivateKey.pem 2048
    openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
    ```

   An additional step is needed for generating the private key for converting it into the PKCS#8 format.

   openssl command for converting private key
    ```shell
    openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
    ```

2) Place the public key `publicKey.pem` to a point that is in the classpath like `META-INF/ressources` and configure the
   following property in `application.properties`:

    ```
    mp.jwt.verify.publickey.location=META-INF/resources/publicKey.pem 
    ```

3) Generate a JWT Token

   You can now generate your own JSON Web Token and sign it with your private key `privateKey.pem`. There are a lot of
   different options available on how to generate a JSON Web Token.

   **GeneratorTokenTest**:

   For this application a test class was written to generate new JSON Web Tokens on demand. You can generate new JSON
   Web Tokens by enabling the test class `com.mpbauer.serverless.samples.petclinic.security.GeneratorTokenTest.java`.
   The test classes will generate a new token with specific roles and print it to the console output.

For more details check out the Quarkus [Security-JWT-Quickstart](https://quarkus.io/guides/security-jwt) guide.

## GitHub Actions CI/CD configuration

This section explains the necessary setup details to build and deploy the `quarkus-petclinic-rest-aws-lambda`
application to AWS Lambda.

### IAM Accounts

GitHub Actions is deploying the built applications to AWS Lambda. To do this with a CI/CD tool like
[GitHub Actions](https://docs.github.com/en/actions) we need to create an IAM accounts to manage and regulate access to
AWS ressources. You follow this [guide](https://www.serverless.com/framework/docs/providers/aws/guide/credentials/) in
order to create AWS credentials for AWS Lambda deployments.

### GitHub configuration

In GitHub, you need to set up the following secrets via your repositories settings tab:

- `AWS_ACCESS_KEY_ID` - The AWS access key ID from the AWS credentials.csv file
- `AWS_SECRET_ACCESS_KEY` - Your AWS secret access key
- `AWS_REGION` - The AWS region of your Lambda function
- `AWS_DB_HOST` - The hostname or public IP address of the database server
- `AWS_DB_PORT` - The port of the database server
- `AWS_DB_DATABASE` - The database of your db server you are using for the application
- `AWS_DB_USERNAME` - The db username that is used within your application
- `AWS_DB_PASSWORD` - The db user password that is used within your application
- `AWS_LAMBDA_S3_BUCKET_DEV` - S3 Bucket that is used to upload your lambda code and templates for the `dev` environment
- `AWS_LAMBDA_S3_BUCKET_PROD`- S3 Bucket that is used to upload your lambda code and templates for the `prod`
  environment

In the end your secrets should look like this:

[![SETUP_GITHUB_AWS_SECRETS_00](docs/screenshots/setup-github-secrets/setup_github_aws_secrets_00.png)](docs/screenshots/setup-github-secrets/setup_github_aws_secrets_00.png)

## URL Endpoints - Development

### Quarkus Lambda Functions running on JVM

| Service          | URL
| ---------------- |:-------------:
| Owner API        | https://mgw7duxp6l.execute-api.eu-central-1.amazonaws.com/petclinic/api/owners
| Pet API          | https://9bb7ibso0f.execute-api.eu-central-1.amazonaws.com/petclinic/api/pets
| Pet Types API    | https://8fv85mksp7.execute-api.eu-central-1.amazonaws.com/petclinic/api/pettypes
| Specialties API  | https://9wgevfz04k.execute-api.eu-central-1.amazonaws.com/petclinic/api/specialties
| User API         | https://6fnceo26we.execute-api.eu-central-1.amazonaws.com/petclinic/api/users
| Vet API          | https://im8083pax8.execute-api.eu-central-1.amazonaws.com/petclinic/api/vets
| Visit API        | https://itek5pcax7.execute-api.eu-central-1.amazonaws.com/petclinic/api/visits

### Quarkus Lambda Functions running in Native Mode

| Service          | URL
| ---------------- |:-------------:
| Owner API        | https://qeh5i8z4ya.execute-api.eu-central-1.amazonaws.com/petclinic/api/owners
| Pet API          | https://wtdvyz0mrj.execute-api.eu-central-1.amazonaws.com/petclinic/api/pets
| Pet Types API    | https://7p5d9yrv06.execute-api.eu-central-1.amazonaws.com/petclinic/api/pettypes
| Specialties API  | https://gl94w80lt5.execute-api.eu-central-1.amazonaws.com/petclinic/api/specialties
| User API         | https://xirmkhop1c.execute-api.eu-central-1.amazonaws.com/petclinic/api/users
| Vet API          | https://xtfhwvw0p6.execute-api.eu-central-1.amazonaws.com/petclinic/api/vets
| Visit API        | https://21ejizzesi.execute-api.eu-central-1.amazonaws.com/petclinic/api/visits

## URL Endpoints - Production

### Quarkus Lambda Functions running on JVM

| Service          | URL
| ---------------- |:-------------:
| Owner API        | https://eotgj6qbt7.execute-api.eu-central-1.amazonaws.com/petclinic/api/owners
| Pet API          | https://4pga3amkn2.execute-api.eu-central-1.amazonaws.com/petclinic/api/pets
| Pet Types API    | https://yurnhe26y0.execute-api.eu-central-1.amazonaws.com/petclinic/api/pettypes
| Specialties API  | https://okxv7jysc3.execute-api.eu-central-1.amazonaws.com/petclinic/api/specialties
| User API         | https://chzfnqks24.execute-api.eu-central-1.amazonaws.com/petclinic/api/users
| Vet API          | https://xv542c2711.execute-api.eu-central-1.amazonaws.com/petclinic/api/vets
| Visit API        | https://rr6w41v3zh.execute-api.eu-central-1.amazonaws.com/petclinic/api/visits

### Quarkus Lambda Functions running in Native Mode

| Service          | URL
| ---------------- |:-------------:
| Owner API        | https://fapt1ip2ll.execute-api.eu-central-1.amazonaws.com/petclinic/api/owners
| Pet API          | https://4acnwr9r91.execute-api.eu-central-1.amazonaws.com/petclinic/api/pets
| Pet Types API    | https://z9vi81ziil.execute-api.eu-central-1.amazonaws.com/petclinic/api/pettypes
| Specialties API  | https://37ygmicgm0.execute-api.eu-central-1.amazonaws.com/petclinic/api/specialties
| User API         | https://flm2o00cjh.execute-api.eu-central-1.amazonaws.com/petclinic/api/users
| Vet API          | https://yvg4on8oz6.execute-api.eu-central-1.amazonaws.com/petclinic/api/vets
| Visit API        | https://d7rgyyzjle.execute-api.eu-central-1.amazonaws.com/petclinic/api/visits
