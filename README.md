# Doppler Spring Boot Sample App

Learn how to use Doppler to supply secrets to Spring Boot applications in local development and Kubernetes.

## Prerequisites

- Java 18
- Maven
- [Doppler CLI](https://docs.doppler.com/docs/install-cli)

## Setup

Create the sample **spring-boot-app** Doppler project using the following button:

[![Import to Doppler](https://raw.githubusercontent.com/DopplerUniversity/app-config-templates/main/doppler-button.svg)](https://dashboard.doppler.com/workplace/template/import?template=https://github.com/DopplerUniversity/doppler-spring-boot/blob/main/doppler-template.yaml)

Or the Doppler CLI:

```sh
doppler import
```

Auto-select the project and dev config using the [doppler.yaml](./doppler.yaml] file:

```sh
doppler setup --no-interactive
```

Then verify you can fetch secrets:

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

## Local Development

In local development, the Doppler CLI acts as an application runner, injecting secrets as environment variables into the Spring Boot process:

```sh
doppler run -- ./mvnw spring-boot:run --quiet
```

### IDE Debugging

Because the Doppler CLI is required for injecting secrets into the Spring process, running and debugging your application is handled slightly differently.

Using IntelliJ IDEA to demonstrate, create a new **Shell Script** configuration and use the following as the **Script text**:

```
 doppler run -- ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"
```

<img width="898" alt="Spring Run" src="https://user-images.githubusercontent.com/133014/187382418-1b3374cf-042b-46f1-b023-50b7e506067a.png">

We recommend saving the [run configuration as a project file](./.run/Spring_Run.run.xml) so team members don't have to configure this individually.

Now that the server can be run in debug mode, the final step is creating a [Remote JVM Debug configuration](./.run/Spring_Debug.run.xml):

<img width="898" alt="Spring Debug" src="https://user-images.githubusercontent.com/133014/187384711-a14b2960-5d36-41a3-9ef9-f7d8e51d2478.png">

Running and debugging an application is then as follows.

https://user-images.githubusercontent.com/133014/187386403-64d7affa-14df-4dba-961e-9b6ebccc7c52.mp4

## Usage: Docker

First build the image:

```sh
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=doppler/spring-boot-app
```

Then use the Doppler CLI to set container environment variables via the `--env-file` option:

```sh
docker run \
  --rm \
  --name doppler-spring-boot \
  --env-file <(doppler secrets download --no-file --format docker) \
  -p $(doppler secrets get SERVER_PORT --plain):$(doppler secrets get SERVER_PORT --plain) \
  doppler-spring-boot
```

## Usage: Kubernetes

### Doppler CLI with kubectl

The quickest way to create and sync secrets to Kubernetes is using the Doppler CLI with `kubectl`.

To create a or update a secret named `spring-boot-app-secret`:

```sh
kubectl create secret generic spring-boot-app-secret \
  	--save-config \
  	--dry-run=client \
  	--from-env-file <(doppler secrets download --no-file --format docker) \
  	-o yaml | \
  	kubectl apply -f -
```

Then use the handy [get-kube-secret.sh](./bin/get-kube-secret.sh) script to view the secrets contents:

```sh
./bin/get-kube-secret.sh spring-boot-app-secret
```

This adhoc approach is great for getting started but for production usage to sync secrets at scale, we recommend our [Kubernetes Operator](https://docs.doppler.com/docs/kubernetes-operator).  

### Kubernetes Operator

The [Doppler Kubernetes Operator](https://docs.doppler.com/docs/kubernetes-operator) is a background service that automatically syncs secrets to Kubernetes with (optional) automatic redeployment of services when the secrets they consume are updated.

The Operator uses a `DopplerSecret` [CRD (custom resource definition)](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/) which provides the auth token required for fetching secrets as well as the name and namespace the Operator will create  

Check out the [Operator Secrets Sync Guide](https://docs.doppler.com/docs/kubernetes-operator#secrets-sync) for more detailed instructions but to quickly demonstrate, first create a [Service Token](https://docs.doppler.com/docs/service-tokens) for the Production environment to grant read-only access:

```sh
doppler setup # Select the prd environment

DOPPLER_TOKEN="$(doppler configs tokens create "Doppler Kubernetes Operator" --plain)"
```

Then create a Kubernetes secret containing the Service Token:

```sh
kubectl create secret generic spring-boot-app-doppler-token \
  --namespace doppler-operator-system \
  --from-literal=serviceToken=$DOPPLER_TOKEN
```

Confirm the secret was created successfully:

```sh
./bin/get-kube-secret.sh spring-boot-app-doppler-token --namespace doppler-operator-system
```

Next, define the `DopplerSecret` (see ./kubernetes/doppler-secret.yaml):

```yaml
apiVersion: secrets.doppler.com/v1alpha1
kind: DopplerSecret
metadata:
  # DopplerSecret Name
  name: spring-boot-app-doppler-secret

  # Namespace (only create DopplerSecret resources in the  doppler-operator-system namespace)
  namespace: doppler-operator-system

spec:
  tokenSecret:
    # Doppler token secret reference
    name: spring-boot-app-doppler-token

  managedSecret:
    # Synced secret name
    name: spring-boot-app-secret

    # Synced secret namespace (the namespace of the deployment that will consume this secret)
    namespace: default
```

Create the `DopplerSecret` by running:

```sh
kubectl apply -f kubernetes/doppler-secret.yaml
```

Then verify Operator created the secret:

```sh
# View secret and Doppler-specific annotations
kubectl describe secret spring-boot-app-secret

# View secrets
./bin/get-kube-secret.sh spring-boot-app-secret
```

Now that the Kubernetes secret is in place, let's take care of deployment.

You'll notice that the below Deployment spec is pretty standard except for the `secrets.doppler.com/reload: 'true'` annotation which will trigger a redeploy to update the application with the latest secrets.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: doppler-spring-boot
  annotations:
    # The Operator's real superpower
    secrets.doppler.com/reload: 'true'
spec:
  replicas: 1
  selector:
    matchLabels:
      app: doppler-spring-boot
  template:
    metadata:
      labels:
        app: doppler-spring-boot
    spec:
      containers:
        - name: doppler-spring-boot
          image: doppler/spring-boot-app
          imagePullPolicy: IfNotPresent

          # envFrom injects all secret values as environment variables
          envFrom:
            - secretRef:
                # Operator created secret
                name: spring-boot-app-secret

          ports:
            - name: app
              containerPort: 8080

          resources:
            requests:
              memory: '1024Mi'
              cpu: '250m'
            limits:
              memory: '1024Mi'
              cpu: '500m'
```

Deploy the application:

```sh
kubectl apply -f kubernetes/deployment.yaml
```

Then tail the deployment logs to verify the secrets were injected successfully:

```sh
kubectl logs -f deployment/doppler-spring-boot --tail 50
```

To test the auto-deployment reload on secrets change, update a secret (e.g APP_APPLICATION_NAME) in the dashboard, then tail the logs again to observe deployment being updated.

If the log tail command is killed, it's because the Pod it was tailing was deleted. Simply re-run the above `kubectl logs` command.

## Help and Support

You can get help and support in our [Doppler community forum](https://community.doppler.com/), find us on [Twitter](https://twitter.com/doppler), or Team and Enterprise customers can use our in-product support.
