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
