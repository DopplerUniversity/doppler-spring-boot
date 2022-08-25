SHELL := /usr/bin/env bash

package:
	./mvnw clean package spring-boot:repackage

dev:
	doppler run -- ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005"

docker-build:
	./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=doppler/spring-boot-app

docker-run:
	docker run \
	--rm \
	--name doppler-spring-boot \
	--env-file <(doppler secrets download --no-file --format docker) \
	-p $$(doppler secrets get SERVER_PORT --plain):$$(doppler secrets get SERVER_PORT --plain) \
	doppler/spring-boot-app

create-sync-kube-secret:
	kubectl create secret generic spring-boot-app \
  	--save-config \
  	--dry-run=client \
  	--from-env-file <(doppler secrets download --no-file --format docker) \
  	-o yaml | \
  	kubectl apply -f -

delete-kube-secret:
	kubectl delete secret spring-boot-app

create-doppler-token-secret:
	kubectl create secret generic spring-boot-app-doppler-token \
	--namespace doppler-operator-system \
	--from-literal=serviceToken=$$(doppler configs tokens create "Doppler Kubernetes Operator" --plain)

create-doppler-secret:
	kubectl apply -f kubernetes/doppler-secret.yaml

create-deployment:
	kubectl apply -f kubernetes/deployment.yaml

tail-deployment:
	kubectl logs -f deployment/doppler-spring-boot --tail 50
