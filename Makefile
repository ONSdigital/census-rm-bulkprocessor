build:
	cd ui && npm install && npm run build

run-dev-ui:
	cd ui && npm install && npm start

run-dev-api:
	pipenv install && pipenv run python3 main.py

docker: build
	docker build -t eu.gcr.io/census-rm-ci/rm/census-rm-bulkprocessor .

docker-run: docker
	docker run --network=censusrmdockerdev_default  -p 5000:5000 eu.gcr.io/census-rm-ci/rm/census-rm-bulkprocessor:latest
