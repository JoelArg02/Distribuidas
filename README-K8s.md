# Sistema Distribuido - Despliegue en Kubernetes

Este proyecto contiene un sistema de microservicios distribuidos que se puede ejecutar en Kubernetes usando Minikube.

## 🏗️ Arquitectura

### Servicios
- **Eureka Server**: Servidor de descubrimiento de servicios
- **API Gateway**: Gateway principal para enrutar peticiones
- **ms-publish**: Microservicio de publicaciones
- **ms-catalogo**: Microservicio de catálogo
- **notificaciones**: Microservicio de notificaciones
- **servicio-auth**: Microservicio de autenticación
- **sync**: Servicio de sincronización

### Infraestructura
- **CockroachDB**: Base de datos distribuida
- **RabbitMQ**: Message broker

## 🚀 Despliegue Rápido

### Prerequisitos
- Docker instalado
- Minikube instalado
- kubectl instalado

### Opción 1: Despliegue Completo (Recomendado)
```bash
./start-all.sh
```

### Opción 2: Paso a Paso
```bash
# 1. Construir imágenes Docker
./build-images.sh

# 2. Desplegar en Kubernetes
./deploy-k8s.sh
```

### Opción 3: Solo construir imágenes
```bash
./start-all.sh --build-only
```

### Opción 4: Solo desplegar (si ya tienes las imágenes)
```bash
./start-all.sh --deploy-only
```

### Opción 5: Limpiar y redesplegar todo
```bash
./start-all.sh --clean
```

## 🔧 Configuración

### Variables de Entorno
Los servicios están configurados para usar variables de entorno que se cargan desde ConfigMaps y Secrets de Kubernetes:

- **Base de datos**: CockroachDB cluster interno
- **Message Broker**: RabbitMQ cluster interno
- **Service Discovery**: Eureka Server interno

### Bases de Datos
Se crean automáticamente las siguientes bases de datos en CockroachDB:
- `ms-publish`: Para el servicio de publicaciones
- `db-catalog`: Para el servicio de catálogo
- `db-notifications`: Para notificaciones
- `db-auth`: Para autenticación

## 🌐 Acceso a los Servicios

### NodePort URLs (disponibles inmediatamente)
```bash
# Obtener IP de Minikube
minikube ip

# URLs (reemplaza <MINIKUBE_IP> con la IP obtenida)
API Gateway: http://<MINIKUBE_IP>:30000
Eureka Server: http://<MINIKUBE_IP>:30861
CockroachDB Admin: http://<MINIKUBE_IP>:30080
RabbitMQ Management: http://<MINIKUBE_IP>:31672
```

### Ingress URLs (requiere configuración adicional)
```bash
# Habilitar tunnel de Minikube (en otra terminal)
minikube tunnel

# URLs de Ingress
http://distribuidas.local
http://gateway.127.0.0.1.sslip.io
http://eureka.127.0.0.1.sslip.io
```

Para usar `distribuidas.local`, agregar a `/etc/hosts`:
```
127.0.0.1 distribuidas.local
```

## 📊 Monitoreo y Debugging

### Ver estado de los recursos
```bash
# Pods
kubectl get pods -n distribuidas

# Servicios
kubectl get services -n distribuidas

# Deployments
kubectl get deployments -n distribuidas

# Ingress
kubectl get ingress -n distribuidas
```

### Ver logs
```bash
# Logs de un servicio específico
kubectl logs -f deployment/<service-name> -n distribuidas

# Ejemplos:
kubectl logs -f deployment/api-gateway -n distribuidas
kubectl logs -f deployment/eureka-server -n distribuidas
kubectl logs -f deployment/ms-publish -n distribuidas
```

### Dashboard de Kubernetes
```bash
minikube dashboard
```

### Eventos del cluster
```bash
kubectl get events -n distribuidas --sort-by='.lastTimestamp'
```

## 🔄 Gestión del Ciclo de Vida

### Escalar servicios
```bash
kubectl scale deployment <service-name> --replicas=3 -n distribuidas
```

### Actualizar imagen
```bash
# Reconstruir imagen
eval $(minikube docker-env)
docker build -t <image-name>:latest .

# Reiniciar deployment
kubectl rollout restart deployment/<service-name> -n distribuidas
```

### Eliminar todo
```bash
kubectl delete namespace distribuidas
```

## 🐛 Troubleshooting

### Problema: Pods en estado Pending
```bash
# Verificar recursos del cluster
kubectl describe nodes

# Verificar eventos
kubectl get events -n distribuidas
```

### Problema: Servicio no responde
```bash
# Verificar logs del pod
kubectl logs <pod-name> -n distribuidas

# Verificar conectividad
kubectl exec -it <pod-name> -n distribuidas -- curl http://service-name:port/actuator/health
```

### Problema: Error de conexión a base de datos
```bash
# Verificar que CockroachDB esté corriendo
kubectl get pods -n distribuidas | grep cockroach

# Verificar logs de CockroachDB
kubectl logs deployment/cockroachdb -n distribuidas

# Verificar conectividad desde un pod
kubectl exec -it <app-pod> -n distribuidas -- nc -zv cockroachdb-service 26257
```

### Problema: Imágenes no se encuentran
```bash
# Verificar que Docker esté configurado para Minikube
eval $(minikube docker-env)

# Listar imágenes disponibles
docker images

# Reconstruir si es necesario
./build-images.sh
```

## 📁 Estructura del Proyecto

```
├── k8s/                           # Archivos de Kubernetes
│   ├── namespace.yaml             # Namespace principal
│   ├── configmaps.yaml           # ConfigMaps y Secrets
│   ├── ingress.yaml              # Ingress principal
│   ├── cockroachdb/              # CockroachDB
│   ├── rabbitmq/                 # RabbitMQ
│   ├── cockroachdb-init/         # Job de inicialización DB
│   ├── eureka/                   # Eureka Server
│   ├── api-gateway/              # API Gateway
│   ├── ms-publish/               # Microservicio Publicaciones
│   ├── ms-catalogo/              # Microservicio Catálogo
│   ├── notificaciones/           # Microservicio Notificaciones
│   ├── servicio-auth/            # Microservicio Autenticación
│   └── sync/                     # Servicio Sincronización
├── build-images.sh               # Script construcción imágenes
├── deploy-k8s.sh                # Script despliegue K8s
└── start-all.sh                 # Script principal
```

## 🔒 Credenciales por Defecto

### RabbitMQ
- Usuario: `admin`
- Contraseña: `admin`

### CockroachDB
- Usuario: `root`
- Contraseña: (vacía)

### JWT Secret
- Configurado en Secret de Kubernetes: `jwt-secret`
