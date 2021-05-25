build:
	./build.sh

test:
	cd ui && npm install && npm test -- --watchAll=false

run-dev-api: build
	docker run -e spring_profiles_active=docker --network=censusrmdockerdev_default --link ons-postgres:postgres --link rabbitmq:rabbitmq -p 8080:8080 eu.gcr.io/census-rm-ci/rm/census-rm-bulkprocessor:latest

run-dev-ui:
	cd ui && npm install && npm start
