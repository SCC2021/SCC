#!/usr/bin/sh

kubectl apply -f mongodb-secrets.yaml

kubectl create -f mongodb-pvc.yaml

kubectl apply -f mongodb-deployment.yaml

#kubectl create -f mongodb-client.yaml


# kubectl exec deployment/mongo-client -it -- /bin/bash
# mongo --host mongo-nodeport-svc --port 27017 -u admin -p admin

kubectl create -f mongodb-nodeport-svc.yaml


