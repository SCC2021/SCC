#!/usr/bin/sh

kubectl delete --all deployments --namespace=default
kubectl delete --all pods --namespace=default
kubectl delete --all svc --namespace=default
kubectl delete --all pvc --namespace=default




