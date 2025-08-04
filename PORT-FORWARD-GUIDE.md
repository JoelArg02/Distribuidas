# 🚀 Guía de Port Forwarding y Rutas del Sistema Distribuido

## 📡 Comandos de Port Forwarding

### API Gateway (Recomendado - Puerto principal)
```bash
kubectl port-forward service/api-gateway 8000:8000 -n distribuidas
```
**URL de acceso:** `http://localhost:8000`

### Servicio de Autenticación (Directo)
```bash
kubectl port-forward service/servicio-auth 8080:8080 -n distribuidas
```
**URL de acceso:** `http://localhost:8080`

### Servicio de Catálogo
```bash
kubectl port-forward service/servicio-catalogo 8081:8080 -n distribuidas
```
**URL de acceso:** `http://localhost:8081`

### Servicio de Publicaciones
```bash
kubectl port-forward service/servicio-publicaciones 8082:8080 -n distribuidas
```
**URL de acceso:** `http://localhost:8082`

### Servicio de Notificaciones
```bash
kubectl port-forward service/servicio-notificaciones 8083:8080 -n distribuidas
```
**URL de acceso:** `http://localhost:8083`

### Eureka Server (Discovery Service)
```bash
kubectl port-forward service/eureka-server 8761:8761 -n distribuidas
```
**URL de acceso:** `http://localhost:8761`

### CockroachDB (Base de Datos)
```bash
kubectl port-forward service/cockroachdb-service 26257:26257 -n distribuidas
```
**URL de acceso:** `localhost:26257`

### RabbitMQ (Message Broker)
```bash
kubectl port-forward service/rabbitmq-service 15672:15672 -n distribuidas
```
**URL de acceso (Admin UI):** `http://localhost:15672`
- **Usuario:** admin
- **Contraseña:** admin

---

## 🌐 Rutas del API Gateway

### 🔐 Autenticación (Puerto 8000)
```bash
# Registro de usuario
POST http://localhost:8000/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}

# Login
POST http://localhost:8000/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

# Validación de token
POST http://localhost:8000/auth/validate
Authorization: Bearer <tu-token>

# Información de usuario
GET http://localhost:8000/auth/user/{username}

# Health check del servicio auth
GET http://localhost:8000/auth/health
```

### 📚 Catálogo (Puerto 8000)
```bash
# Listar productos/artículos
GET http://localhost:8000/catalogo/articulos
Authorization: Bearer <tu-token>

# Obtener artículo específico
GET http://localhost:8000/catalogo/articulos/{id}
Authorization: Bearer <tu-token>

# Crear nuevo artículo
POST http://localhost:8000/catalogo/articulos
Authorization: Bearer <tu-token>
Content-Type: application/json

{
  "nombre": "Producto Test",
  "descripcion": "Descripción del producto",
  "precio": 99.99
}

# Actualizar artículo
PUT http://localhost:8000/catalogo/articulos/{id}
Authorization: Bearer <tu-token>

# Eliminar artículo
DELETE http://localhost:8000/catalogo/articulos/{id}
Authorization: Bearer <tu-token>
```

### 📝 Publicaciones (Puerto 8000)
```bash
# Listar publicaciones
GET http://localhost:8000/publicaciones/
Authorization: Bearer <tu-token>

# Crear nueva publicación
POST http://localhost:8000/publicaciones/
Authorization: Bearer <tu-token>
Content-Type: application/json

{
  "titulo": "Mi Publicación",
  "contenido": "Contenido de la publicación",
  "autor": "username"
}

# Obtener publicación específica
GET http://localhost:8000/publicaciones/{id}
Authorization: Bearer <tu-token>

# Actualizar publicación
PUT http://localhost:8000/publicaciones/{id}
Authorization: Bearer <tu-token>

# Eliminar publicación
DELETE http://localhost:8000/publicaciones/{id}
Authorization: Bearer <tu-token>
```

### 🔔 Notificaciones (Puerto 8000)
```bash
# Listar notificaciones del usuario
GET http://localhost:8000/notificaciones/user/{username}
Authorization: Bearer <tu-token>

# Crear notificación
POST http://localhost:8000/notificaciones/
Authorization: Bearer <tu-token>
Content-Type: application/json

{
  "mensaje": "Nueva notificación",
  "usuario": "username",
  "tipo": "INFO"
}

# Marcar como leída
PUT http://localhost:8000/notificaciones/{id}/read
Authorization: Bearer <tu-token>
```

### ⚡ Actuator y Health Checks (Puerto 8000)
```bash
# Health check del API Gateway
GET http://localhost:8000/actuator/health

# Información del Gateway
GET http://localhost:8000/actuator/gateway/routes

# Métricas del Gateway
GET http://localhost:8000/actuator/gateway/filters
```

---

## 🔧 Comandos Útiles para Port Forwarding

### Ejecutar múltiples port forwards en background
```bash
# API Gateway (principal)
kubectl port-forward service/api-gateway 8000:8000 -n distribuidas &

# Auth Service (directo para debug)
kubectl port-forward service/servicio-auth 8080:8080 -n distribuidas &

# Eureka Dashboard
kubectl port-forward service/eureka-server 8761:8761 -n distribuidas &

# RabbitMQ Admin
kubectl port-forward service/rabbitmq-service 15672:15672 -n distribuidas &
```

### Ver procesos de port forward activos
```bash
ps aux | grep "kubectl port-forward"
```

### Matar todos los port forwards
```bash
pkill -f "kubectl port-forward"
```

### Port forward específico por PID
```bash
# Encontrar el PID
ps aux | grep "kubectl port-forward"

# Matar por PID
kill <PID>
```

---

## 🧪 Ejemplos de Pruebas Completas

### 1. Flujo de Autenticación Completo
```bash
# 1. Registrar usuario
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "password123"
  }'

# 2. Hacer login (guardar el token de la respuesta)
curl -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 3. Usar el token para acceder a recursos protegidos
curl -X GET http://localhost:8000/catalogo/articulos \
  -H "Authorization: Bearer <token-aqui>"
```

### 2. Crear y Gestionar Contenido
```bash
# Crear artículo
curl -X POST http://localhost:8000/catalogo/articulos \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Gaming",
    "descripcion": "Laptop para gaming de alta gama",
    "precio": 1299.99
  }'

# Crear publicación
curl -X POST http://localhost:8000/publicaciones/ \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Review Laptop Gaming",
    "contenido": "Esta laptop es excelente para gaming...",
    "autor": "testuser"
  }'
```

---

## 🔍 Troubleshooting

### Verificar que los servicios estén corriendo
```bash
kubectl get pods -n distribuidas
kubectl get services -n distribuidas
```

### Ver logs de un servicio específico
```bash
kubectl logs -f deployment/api-gateway -n distribuidas
kubectl logs -f deployment/servicio-auth -n distribuidas
```

### Verificar conexión a la base de datos
```bash
kubectl port-forward service/cockroachdb-service 26257:26257 -n distribuidas
# En otra terminal:
psql postgresql://root@localhost:26257/defaultdb?sslmode=disable
```

### Verificar RabbitMQ
```bash
kubectl port-forward service/rabbitmq-service 15672:15672 -n distribuidas
# Abrir http://localhost:15672 (admin/admin)
```

---

## 📝 Notas Importantes

1. **Puerto Principal:** Usa siempre el puerto 8000 (API Gateway) para acceder a todos los servicios
2. **Autenticación:** Todos los endpoints (excepto auth) requieren el header `Authorization: Bearer <token>`
3. **CORS:** El API Gateway está configurado para permitir CORS desde cualquier origen
4. **Health Checks:** Todos los servicios tienen endpoints `/actuator/health`
5. **Logs:** Los logs están configurados en nivel DEBUG para desarrollo

---

## 🚀 Scripts de Inicio Rápido

### start-port-forwards.sh
```bash
#!/bin/bash
echo "🚀 Iniciando port forwards..."

kubectl port-forward service/api-gateway 8000:8000 -n distribuidas &
echo "✅ API Gateway: http://localhost:8000"

kubectl port-forward service/eureka-server 8761:8761 -n distribuidas &
echo "✅ Eureka: http://localhost:8761"

kubectl port-forward service/rabbitmq-service 15672:15672 -n distribuidas &
echo "✅ RabbitMQ: http://localhost:15672"

echo "🎉 Port forwards iniciados en background"
echo "💡 Para parar todos: pkill -f 'kubectl port-forward'"
```

### test-auth.sh
```bash
#!/bin/bash
echo "🔐 Probando autenticación..."

# Registrar
echo "📝 Registrando usuario..."
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

echo -e "\n"

# Login
echo "🔑 Haciendo login..."
RESPONSE=$(curl -s -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}')

echo $RESPONSE

# Extraer token (requiere jq)
if command -v jq &> /dev/null; then
    TOKEN=$(echo $RESPONSE | jq -r '.token')
    echo "🎫 Token obtenido: $TOKEN"
    
    # Probar endpoint protegido
    echo "🔒 Probando endpoint protegido..."
    curl -X GET http://localhost:8000/catalogo/articulos \
      -H "Authorization: Bearer $TOKEN"
fi
```
