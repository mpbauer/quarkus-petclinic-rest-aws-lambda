[![Quarkus](docs/images/quarkus-logo.png)](https://quarkus.io/)

# Quarkus PetClinic Sample Application using AWS Lambda

Build with a specific Quarkus profile:
```
mvn clean package -Dquarkus-profile=<profile>
```

### GitHub configuration

In GitHub, you need to set up the following secrets via your repositories settings tab:

- `AWS_ACCESS_KEY_ID` - The AWS access key ID from the AWS credentials.csv file
- `AWS_SECRET_ACCESS_KEY` - Your AWS secret access key
- `AWS_REGION` - The AWS region of your Lambda function
- `AWS_DB_HOST` - The host name of the database
- `AWS_DB_PORT` - The port of the database it is running on
- `AWS_DB_DATABASE` - The name of the database
- `AWS_DB_USERNAME` - The username of the database
- `AWS_DB_PASSWORD` - The password of the database


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
