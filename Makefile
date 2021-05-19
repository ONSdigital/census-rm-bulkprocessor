build:
	./build.sh

run-dev-api: build
	docker run --network=censusrmdockerdev_default  -p 8080:8080 eu.gcr.io/census-rm-ci/rm/census-rm-bulkprocessor:latest

run-dev-ui:
	cd ui && npm install && npm start
