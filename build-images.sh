#!/bin/bash

# Script para construir todas las imágenes Docker para Kubernetes
set -e

echo "🔨 Iniciando construcción de todas las imágenes Docker..."

# Configurar Minikube para usar el daemon de Docker local
echo "📦 Configurando entorno Docker para Minikube..."
eval $(minikube docker-env)

# Crear volumen compartido para cache de Maven si no existe
echo "📚 Configurando cache de Maven..."
if ! docker volume ls | grep -q maven-cache; then
    docker volume create maven-cache
    echo "✅ Volumen maven-cache creado"
else
    echo "✅ Volumen maven-cache ya existe"
fi

# Servicios a construir
SERVICES=(
    "ms-eureka-server:ms-eureka-server"
    "ms-api-gateway:ms-api-gateway" 
    "ms-publish:ms-publish"
    "ms-catalogo:ms-catalogo"
    "notificaciones:notificaciones"
    "AuthService:authservice"
    "sync:sync"
)

# Construir cada servicio
for service in "${SERVICES[@]}"; do
    IFS=':' read -r directory image_name <<< "$service"
    
    echo "🔨 Construyendo imagen $image_name desde directorio $directory..."
    
    if [ -d "$directory" ]; then
        cd "$directory"
        
        # Verificar si existe Dockerfile
        if [ ! -f "Dockerfile" ]; then
            echo "❌ No se encontró Dockerfile en $directory"
            cd ..
            continue
        fi
        
        # Construir la imagen con BuildKit para usar cache de Maven
        DOCKER_BUILDKIT=1 docker build \
            --build-arg BUILDKIT_INLINE_CACHE=1 \
            -t "$image_name:latest" .
        
        if [ $? -eq 0 ]; then
            echo "✅ Imagen $image_name construida exitosamente"
        else
            echo "❌ Error construyendo imagen $image_name"
            cd ..
            exit 1
        fi
        
        cd ..
    else
        echo "❌ Directorio $directory no encontrado"
        exit 1
    fi
done

echo "🎉 Todas las imágenes Docker han sido construidas exitosamente!"
echo "📝 Imágenes disponibles:"
docker images | grep -E "(ms-eureka-server|ms-api-gateway|ms-publish|ms-catalogo|notificaciones|authservice|sync)"

echo ""
echo "📊 Espacio usado por el cache de Maven:"
docker system df -v | grep maven-cache || echo "Cache info no disponible"

echo ""
echo "💡 Ahora puedes ejecutar ./deploy-k8s.sh para desplegar en Kubernetes"
