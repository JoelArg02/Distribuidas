#!/bin/bash

# Script principal para desplegar todos los servicios en Kubernetes
set -e

echo "🚀 Iniciando despliegue de servicios distribuidos en Kubernetes..."

# Verificar que Minikube esté ejecutándose
if ! minikube status > /dev/null 2>&1; then
    echo "❌ Minikube no está ejecutándose. Iniciando Minikube..."
    minikube start --driver=docker --memory=4096 --cpus=2
else
    echo "✅ Minikube está ejecutándose"
fi

# Habilitar Ingress en Minikube
echo "🔧 Habilitando Ingress Controller en Minikube..."
minikube addons enable ingress

# Configurar context de kubectl a minikube
echo "⚙️ Configurando kubectl para usar Minikube..."
kubectl config use-context minikube

echo "📦 Configurando entorno Docker para Minikube..."
eval $(minikube docker-env)

# Función para esperar a que un deployment esté listo
wait_for_deployment() {
    local namespace=$1
    local deployment=$2
    local timeout=${3:-300}
    
    echo "⏳ Esperando a que $deployment esté listo en namespace $namespace..."
    kubectl wait --for=condition=available --timeout=${timeout}s deployment/$deployment -n $namespace
    
    if [ $? -eq 0 ]; then
        echo "✅ $deployment está listo"
    else
        echo "❌ Timeout esperando a $deployment"
        return 1
    fi
}

# Función para verificar que un pod esté corriendo
wait_for_pods() {
    local namespace=$1
    local selector=$2
    local timeout=${3:-300}
    
    echo "⏳ Esperando a que los pods con selector '$selector' estén corriendo en namespace $namespace..."
    kubectl wait --for=condition=ready --timeout=${timeout}s pod -l $selector -n $namespace
}

echo ""
echo "🏗️  PASO 1: Creando namespace y configuraciones base..."

# Crear namespace
kubectl apply -f k8s/namespace.yaml

# Aplicar ConfigMaps y Secrets
kubectl apply -f k8s/configmaps.yaml

echo "✅ Namespace y configuraciones base creadas"

echo ""
echo "🗄️  PASO 2: Desplegando infraestructura (CockroachDB y RabbitMQ)..."

# Desplegar CockroachDB
kubectl apply -f k8s/cockroachdb/deployment.yaml
wait_for_deployment "distribuidas" "cockroachdb" 180

# Desplegar RabbitMQ
kubectl apply -f k8s/rabbitmq/deployment.yaml
wait_for_deployment "distribuidas" "rabbitmq" 120

echo "✅ Infraestructura desplegada"

echo ""
echo "🔧 PASO 3: Inicializando bases de datos..."

# Ejecutar job de inicialización de CockroachDB
kubectl delete job cockroachdb-init -n distribuidas 2>/dev/null || true
kubectl apply -f k8s/cockroachdb-init/job.yaml

# Esperar a que el job termine
echo "⏳ Esperando a que la inicialización de la base de datos termine..."
kubectl wait --for=condition=complete --timeout=300s job/cockroachdb-init -n distribuidas

if [ $? -eq 0 ]; then
    echo "✅ Bases de datos inicializadas exitosamente"
else
    echo "⚠️  Job de inicialización no completó en el tiempo esperado, continuando..."
fi

echo ""
echo "🔍 PASO 4: Desplegando Eureka Server..."

# Desplegar Eureka Server
kubectl apply -f k8s/eureka/deployment.yaml
kubectl apply -f k8s/eureka/service.yaml
wait_for_deployment "distribuidas" "eureka-server" 120

echo "✅ Eureka Server desplegado"

echo ""
echo "🌐 PASO 5: Desplegando API Gateway..."

# Desplegar API Gateway
kubectl apply -f k8s/api-gateway/deployment.yaml
kubectl apply -f k8s/api-gateway/service.yaml
wait_for_deployment "distribuidas" "api-gateway" 180

echo "✅ API Gateway desplegado"

echo ""
echo "🔧 PASO 6: Desplegando microservicios..."

# Desplegar ms-publish
echo "📝 Desplegando ms-publish..."
kubectl apply -f k8s/ms-publish/deployment.yaml
kubectl apply -f k8s/ms-publish/service.yaml

# Desplegar ms-catalogo
echo "📚 Desplegando ms-catalogo..."
kubectl apply -f k8s/ms-catalogo/deployment.yaml
kubectl apply -f k8s/ms-catalogo/service.yaml

# Desplegar notificaciones
echo "📧 Desplegando notificaciones..."
kubectl apply -f k8s/notificaciones/deployment.yaml
kubectl apply -f k8s/notificaciones/service.yaml

# Desplegar servicio-auth
echo "🔐 Desplegando servicio-auth..."
kubectl apply -f k8s/servicio-auth/deployment-new.yaml
kubectl apply -f k8s/servicio-auth/service.yaml

# Desplegar sync
echo "🔄 Desplegando sync..."
kubectl apply -f k8s/sync/deployment-new.yaml
kubectl apply -f k8s/sync/service.yaml

# Esperar a que todos los microservicios estén listos
echo "⏳ Esperando a que todos los microservicios estén listos..."

wait_for_deployment "distribuidas" "ms-publish" 180
wait_for_deployment "distribuidas" "ms-catalogo" 180  
wait_for_deployment "distribuidas" "notificaciones" 180
wait_for_deployment "distribuidas" "servicio-auth" 180
wait_for_deployment "distribuidas" "sync" 180

echo "✅ Todos los microservicios desplegados"

echo ""
echo "🌍 PASO 7: Configurando Ingress..."

# Aplicar Ingress
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/api-gateway/ingress.yaml
kubectl apply -f k8s/eureka/eureka-ingress.yml

echo "✅ Ingress configurado"

echo ""
echo "📊 PASO 8: Verificando estado del deployment..."

# Mostrar estado de todos los recursos
echo "📋 Estado de todos los pods:"
kubectl get pods -n distribuidas -o wide

echo ""
echo "📋 Estado de todos los servicios:"
kubectl get services -n distribuidas

echo ""
echo "📋 Estado de todos los deployments:"
kubectl get deployments -n distribuidas

echo ""
echo "📋 Estado de los ingress:"
kubectl get ingress -n distribuidas

echo ""
echo "🎉 ¡DESPLIEGUE COMPLETADO EXITOSAMENTE!"
echo ""
echo "🔗 URLs de acceso:"
echo "   API Gateway: http://$(minikube ip):$(kubectl get service api-gateway -n distribuidas -o jsonpath='{.spec.ports[0].nodePort}')"
echo "   Eureka Server: http://$(minikube ip):$(kubectl get service eureka-server -n distribuidas -o jsonpath='{.spec.ports[0].nodePort}')"
echo "   CockroachDB Admin: http://$(minikube ip):$(kubectl get service cockroachdb-nodeport -n distribuidas -o jsonpath='{.spec.ports[1].nodePort}')"
echo "   RabbitMQ Management: http://$(minikube ip):$(kubectl get service rabbitmq-nodeport -n distribuidas -o jsonpath='{.spec.ports[1].nodePort}')"
echo ""
echo "🚀 También puedes usar los Ingress (requiere configurar /etc/hosts):"
echo "   http://distribuidas.local"
echo "   http://gateway.127.0.0.1.sslip.io"
echo "   http://eureka.127.0.0.1.sslip.io"
echo ""
echo "🔧 Para ver logs de un servicio específico:"
echo "   kubectl logs -f deployment/<service-name> -n distribuidas"
echo ""
echo "⭐ Para abrir el tunnel de Minikube (en otra terminal):"
echo "   minikube tunnel"
