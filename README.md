# CRUD Full Stack con React + Spring Boot + MySQL + MongoDB

Este repositorio contiene dos proyectos principales:

- `react_crud/`: frontend desarrollado con React + Vite
- `Spring_boot_crud/`: backend desarrollado con Spring Boot y Java 21

La aplicación permite gestionar productos con dos tipos de persistencia:

- SQL: MySQL
- NoSQL: MongoDB

## Estructura del repositorio

```text
pa2/
├── README.md
├── .gitignore
├── react_crud/
│   ├── src/
│   ├── package.json
│   └── ...
└── Spring_boot_crud/
    ├── src/
    ├── dbs/
    ├── pom.xml
    └── ...
```

## Requisitos previos

- Java 21
- Maven
- Node.js + npm
- MySQL ejecutándose localmente
- MongoDB ejecutándose localmente

## 1. Preparar MySQL

1. Ejecuta el script SQL ubicado en:

```text
Spring_boot_crud/dbs/productoscruddb.sql
```

2. Verifica que exista la base de datos `productoscruddb` y la tabla `products`.

3. La configuración por defecto está en:

```text
Spring_boot_crud/src/main/resources/application.properties
```

Por defecto usa:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/productoscruddb
spring.datasource.username=root
spring.datasource.password=admin123
```

## 2. Preparar MongoDB

La conexión por defecto del backend es:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/productoscrud
spring.data.mongodb.database=productoscrud
```

La colección usada por el modelo es `products`.

Puedes verificarla con:

```powershell
mongosh "mongodb://localhost:27017/productoscrud" --eval "db.products.find().pretty()"
```

## 3. Ejecutar el backend

Desde la carpeta `Spring_boot_crud`:

```powershell
mvn spring-boot:run
```

Si el puerto 8082 está ocupado, cambia el valor en:

```text
Spring_boot_crud/src/main/resources/application.properties
```

por ejemplo:

```properties
server.port=8083
```

## 4. Ejecutar el frontend

Desde la carpeta `react_crud`:

```powershell
npm install
npm run dev -- --host 0.0.0.0 --port 5175
```

Abre la aplicación en:

```text
http://127.0.0.1:5175
```

Si quieres cambiar la URL del backend, crea un archivo `.env.local` dentro de `react_crud`:

```env
VITE_API_URL=http://127.0.0.1:8082
```

## 5. Funcionalidades principales

### SQL (MySQL)

- listar productos
- crear producto
- buscar por ID
- editar producto
- eliminar producto

### MongoDB

- listar productos
- crear producto
- editar producto
- eliminar producto
- filtros con `aggregate`:
  - precio mayor que
  - nombre contiene
  - descripción contiene
  - rango de precio
- visualización del `pipeline` usado en el frontend

## 6. Pruebas rápidas

### Backend

Listar productos SQL:

```powershell
Invoke-RestMethod "http://localhost:8082/productos"
```

Listar productos Mongo:

```powershell
Invoke-RestMethod "http://localhost:8082/mongo/productos"
```

Crear producto Mongo:

```powershell
$body = @{ name = "Lentes"; price = 150000; description = "Gafas de ejemplo" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://localhost:8082/mongo/productos" -ContentType "application/json" -Body $body
```

Filtro Mongo por precio mayor:

```powershell
Invoke-RestMethod "http://localhost:8082/mongo/productos/filtro/precio-mayor?minimo=100000"
```

## 7. Solución de problemas comunes

### Puerto ocupado

```powershell
netstat -ano | findstr :8082
```

Si hace falta:

```powershell
taskkill /PID <PID> /F
```

### Spring Boot no arranca

- Verifica que MySQL esté activo
- Verifica que MongoDB esté activo
- Revisa que no haya otra JVM bloqueando el puerto
- Revisa la salida real del arranque en la terminal

### React no se conecta al backend

- Verifica que el backend esté corriendo
- Revisa que la URL de `VITE_API_URL` sea correcta
- Revisa que el backend esté escuchando en el puerto configurado

## 8. Comentario final

Para este tipo de proyecto, la mejor opción de organización es mantener un solo README general en la raíz del repositorio y un solo `.gitignore` raíz, porque ambos subproyectos forman parte del mismo trabajo de laboratorio y no necesitan documentación duplicada ni reglas de ignore separadas.
