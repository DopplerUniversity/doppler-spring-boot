# Doppler Spring Boot Sample App

Learn how to use Doppler to supply secrets to Spring Boot applications in local development and Kubernetes.

## Prerequisites

- Java 18
- Maven
- [Doppler CLI](https://docs.doppler.com/docs/install-cli)

## Setup

Create the sample **spring-boot-app** Doppler project using the following button:

[![Import to Doppler](https://raw.githubusercontent.com/DopplerUniversity/app-config-templates/main/doppler-button.svg)](https://dashboard.doppler.com/workplace/template/import?template=https://github.com/DopplerUniversity/doppler-spring-boot/blob/master/doppler-template.yaml)

Or the Doppler CLI:

```sh
doppler import
```

Select the config to use:

```sh
doppler setup
```

Check the Doppler CLI can fetch secrets:

```sh
doppler secrets
```

Then open the Project in the doppler dashboard:

```sh
doppler open dashboard
```

## Secrets Naming Convention

Doppler secret names are standardized to `UPPER_SNAKE_CASE` which is compatible with [Spring Boot's relaxed binding 2.0](https://github.com/spring-projects/spring-boot/wiki/Relaxed-Binding-2.0#environment-variables).

View the [Project in the Doppler dashboard](https://dashboard.doppler.com/workplace/projects/spring-boot-app/configs/dev) or open the [doppler-template.yaml](./doppler-template.yaml) file for examples.

## Sample App Overview

Configuration is centralized with an [AppConfig.java](./src/main/java/com/doppler/app/config/AppConfig.java) class using Spring Boot configuration properties to bind secrets injected as environment variables by Doppler.

The configuration values are set by a combination of direct environment variable injection (Redis properties) and using an [application.properties](./src/main/resources/application.properties) file with environment variable placeholders.

## Usage: Doppler CLI Env Var Injection

The Doppler CLI acts as the application runner, injecting secrets as environment variables into the Spring Boot process:

```sh
doppler run -- ./mvnw spring-boot:run --quiet
```

## Usage: Docker Env Var Injection

Coming soon!

## Usage: Kubernetes Env Var Injection

Coming soon!

> NOTE: We are working on adding Doppler as a new provider to the [Kubernetes External Secrets Operator](https://github.com/external-secrets/external-secrets/).

