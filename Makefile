build:
	cd ui && npm install && npm run build

run-dev-ui:
	cd ui && npm install && npm start

run-dev-api:
	pipenv install && pipenv run python3 main.py