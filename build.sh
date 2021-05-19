#!/bin/sh
mkdir -p src/main/resources/static
rm -r src/main/resources/static/*
cd ui
npm install
npm run build
cd ..
cp -r ui/build/* src/main/resources/static

mvn clean install