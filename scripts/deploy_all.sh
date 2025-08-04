#!/usr/bin/env bash
set -euo pipefail

NAMESPACE=distribuidas
BASE=~/Documents/GitHub/Distribuidas
K8S_DIR="$BASE/k8s"

minikube status >/dev/null 2>&1 || minikube start --driver=docker

IMAGES=(
  "ms-eureka-server:latest"
  "ms-api-gateway:latest"
  "authservice:latest"
  "ms-catalogo:latest"
  "ms-publish:latest"
  "notificaciones:latest"
  "sync:latest"
)

for img in "${IMAGES[@]}"; do
  minikube image load "$img" || true
done

kubectl apply -f "$K8S_DIR/namespace.yaml" || true

[ -f "$K8S_DIR/rabbitmq.yaml" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/rabbitmq.yaml" || true

[ -d "$K8S_DIR/eureka" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/eureka"
[ -d "$K8S_DIR/api-gateway" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/api-gateway"
[ -d "$K8S_DIR/servicio-auth" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/servicio-auth"
[ -d "$K8S_DIR/ms-catalogo" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/ms-catalogo"
[ -d "$K8S_DIR/ms-publish" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/ms-publish"
[ -d "$K8S_DIR/notificaciones" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/notificaciones"
[ -d "$K8S_DIR/sync" ] && kubectl -n "$NAMESPACE" apply -f "$K8S_DIR/sync"

DEPLOYMENTS=(
  "eureka-server"
  "api-gateway"
  "servicio-auth"
  "ms-catalogo"
  "ms-publish"
  "notificaciones"
  "sync"
)

for dep in "${DEPLOYMENTS[@]}"; do
  kubectl -n "$NAMESPACE" rollout status deployment/"$dep" --timeout=180s || true
done

kubectl -n "$NAMESPACE" get pods
kubectl -n "$NAMESPACE" get svc

SERVICES=(
  "eureka-server"
  "api-gateway"
  "servicio-auth"
  "ms-catalogo"
  "ms-publish"
  "notificaciones"
  "sync"
)

for svc in "${SERVICES[@]}"; do
  minikube service "$svc" -n "$NAMESPACE" --url || true
done
