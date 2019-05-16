#!/bin/bash
sudo helm install --name tika tika/tika-helm/
sudo helm install --name mongodb mongodb/mongodb-helm/
sudo helm install --name text-processor text-processor/text-processor-helm/
