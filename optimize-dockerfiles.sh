#!/bin/bash

# Script para optimizar todos los Dockerfiles con cache de Maven
set -e

echo "🔧 Optimizando Dockerfiles para cache de Maven..."

# Servicios a optimizar con sus puertos específicos
SERVICES_PORTS=(
    "ms-api-gateway:8000"
    "ms-publish:8080"
    "ms-catalogo:8080"
    "notificaciones:8080"
    "AuthService:8080"
    "sync:8085"
)

# Función para crear Dockerfile optimizado con puerto específico
create_dockerfile() {
    local port=$1
    cat << EOF
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar solo pom.xml primero para cache de dependencias
COPY pom.xml .

# Descargar dependencias (se cachea en esta capa)
RUN mvn -DskipTests dependency:go-offline

# Copiar código fuente y compilar
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el JAR construido
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto específico del servicio
EXPOSE $port

# Configurar JVM para contenedor
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
EOF
}

# Optimizar cada servicio
for service_port in "${SERVICES_PORTS[@]}"; do
    IFS=':' read -r service port <<< "$service_port"
    echo "🔧 Optimizando Dockerfile de $service (puerto $port)..."
    
    if [ -d "$service" ]; then
        # Hacer backup del Dockerfile original
        if [ -f "$service/Dockerfile" ]; then
            cp "$service/Dockerfile" "$service/Dockerfile.backup"
        fi
        
        # Crear Dockerfile optimizado con puerto específico
        create_dockerfile "$port" > "$service/Dockerfile"
        
        echo "✅ Dockerfile de $service optimizado (puerto $port)"
    else
        echo "⚠️  Directorio $service no encontrado"
    fi
done

echo ""
echo "🎉 Todos los Dockerfiles han sido optimizados!"
echo "📝 Cambios realizados:"
echo "   - Cache de dependencias Maven"
echo "   - Configuración JVM optimizada para contenedores"
echo "   - Puertos específicos por servicio:"
echo "     * ms-api-gateway: 8000"
echo "     * ms-publish: 8080"
echo "     * ms-catalogo: 8080"
echo "     * notificaciones: 8080"
echo "     * AuthService: 8080"
echo "     * sync: 8085"
echo "   - Backups creados como .backup"
