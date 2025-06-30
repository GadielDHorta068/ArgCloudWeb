# ArgCloud - Plataforma de Máquinas Virtuales

ArgCloud es una plataforma completa para la gestión de máquinas virtuales en la nube, desarrollada con Angular (frontend) y Spring Boot (backend), usando PostgreSQL como base de datos.

## 🚀 Características

- **Frontend Angular**: Interfaz moderna y responsiva
- **Backend Spring Boot**: API REST robusta y segura
- **Base de datos PostgreSQL**: Almacenamiento confiable
- **Autenticación JWT**: Sistema de autenticación seguro
- **Verificación por email**: Registro seguro con confirmación
- **Gestión completa de VMs**: Crear, administrar y monitorear máquinas virtuales
- **Integración con Proxmox**: Conectividad directa con servidores Proxmox VE
- **Docker**: Despliegue con contenedores
- **Arquitectura Business-Repository-Presenter**: Código limpio y mantenible

## 🛠️ Tecnologías

### Frontend
- Angular 17
- TypeScript
- Bootstrap 5
- FontAwesome
- RxJS
- NGX-Toastr (notificaciones)

### Backend
- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- BCrypt para hash de contraseñas
- JavaMail para envío de emails

### Infraestructura
- Proxmox VE (virtualización)
- MikroTik (networking)
- Docker & Docker Compose

## 📋 Prerrequisitos

- Docker y Docker Compose instalados
- Git
- Servidor Proxmox VE (opcional para funcionalidad completa)
- Router MikroTik (opcional para gestión de red)

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd LandingPage
```

### 2. Configurar variables de entorno

Edita el archivo `docker-compose.yml` y configura las variables de email:

```yaml
- MAIL_HOST=smtp.gmail.com
- MAIL_PORT=587
- MAIL_USERNAME=tu-email@gmail.com
- MAIL_PASSWORD=tu-app-password
```

**Nota**: Para Gmail, necesitas generar una "Contraseña de aplicación" en la configuración de seguridad de tu cuenta.

### 3. Configurar APIs externas (Opcional)

Edita `backend/src/main/resources/application.properties` para configurar la integración con Proxmox y MikroTik:

```properties
# Proxmox VE Configuration
external.api.proxmox.url=https://tu-servidor-proxmox:8006
external.api.proxmox.username=root@pam
external.api.proxmox.password=tu-password

# MikroTik Configuration
external.api.mikrotik.url=https://tu-router-mikrotik
external.api.mikrotik.username=admin
external.api.mikrotik.password=tu-password

# API Client Configuration
external.api.timeout=30000
```

### 4. Ejecutar con Docker Compose

```bash
docker-compose up --build
```

Este comando:
- Construye las imágenes de backend y frontend
- Inicia PostgreSQL, backend y frontend
- Configura la red entre servicios

### 5. Acceder a la aplicación

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Base de datos**: localhost:5432

## 🖥️ Gestión de Máquinas Virtuales

### Características Principales

ArgCloud ofrece una gestión completa de máquinas virtuales con las siguientes funcionalidades:

#### 🔧 **Operaciones de VM**
- **Crear VM**: Interfaz intuitiva para crear nuevas máquinas virtuales
- **Iniciar/Parar**: Control completo del estado de las VMs
- **Reiniciar**: Reinicio seguro de máquinas virtuales
- **Eliminar**: Eliminación segura con confirmación
- **Sincronizar**: Sincronización con el servidor Proxmox

#### 📊 **Monitoreo y Estadísticas**
- **Estado en tiempo real**: Visualización del estado actual de cada VM
- **Recursos**: Monitoreo de CPU, RAM y almacenamiento
- **Red**: Información de IP y MAC address
- **Historial**: Fechas de creación y última actualización

#### 🎨 **Interfaz de Usuario**
- **Dashboard integrado**: Vista general de todas las VMs
- **Tarjetas informativas**: Cada VM se muestra en una tarjeta con información clave
- **Filtros y búsqueda**: Encuentra rápidamente las VMs que necesitas
- **Responsive**: Funciona perfectamente en desktop y móvil

### Modelo de Datos

```typescript
interface VirtualMachine {
  id: number;
  name: string;
  status: 'running' | 'stopped' | 'restarting' | 'creating' | 'deleting' | 'error';
  os: string;
  cpu: number;           // Número de cores
  memory: number;        // RAM en MB
  disk: number;          // Almacenamiento en GB
  ipAddress?: string;    // Dirección IP asignada
  macAddress?: string;   // Dirección MAC
  nodeName?: string;     // Nodo Proxmox donde se ejecuta
  createdAt: string;     // Fecha de creación
  updatedAt?: string;    // Última actualización
  userName?: string;     // Usuario propietario
}
```

### Funcionalidades por Pantalla

#### 🏠 **Dashboard**
- Vista general con estadísticas
- Tarjetas de resumen (VMs activas, CPU total, RAM total, almacenamiento)
- Lista rápida de VMs con acciones básicas
- Consola simulada para VMs seleccionadas

#### 🖥️ **Gestión de VMs**
- **Crear Nueva VM**:
  - Formulario con validación
  - Selección de SO, recursos y nodo
  - Configuración de red automática
- **Lista de VMs**:
  - Vista en tarjetas con información completa
  - Botones de acción contextuales
  - Estados visuales con colores
- **Acciones Disponibles**:
  - ▶️ Iniciar VM
  - ⏹️ Parar VM
  - 🔄 Reiniciar VM
  - 🗑️ Eliminar VM
  - 🔄 Sincronizar con Proxmox

### Estados de VM

| Estado | Descripción | Color | Acciones Disponibles |
|--------|-------------|-------|---------------------|
| `running` | VM en ejecución | 🟢 Verde | Parar, Reiniciar, Eliminar |
| `stopped` | VM detenida | 🔴 Rojo | Iniciar, Eliminar |
| `restarting` | VM reiniciándose | 🟡 Amarillo | Ninguna (temporal) |
| `creating` | VM en creación | 🔵 Azul | Ninguna (temporal) |
| `deleting` | VM eliminándose | 🟠 Naranja | Ninguna (temporal) |
| `error` | Error en la VM | ⚫ Gris | Reiniciar, Eliminar |

### Integración con Proxmox

ArgCloud se integra directamente con servidores Proxmox VE para:

- **Sincronización automática**: Las VMs se sincronizan automáticamente
- **Gestión remota**: Control completo desde la interfaz web
- **Monitoreo en tiempo real**: Estados actualizados automáticamente
- **Gestión de recursos**: Asignación dinámica de CPU, RAM y almacenamiento

## 📁 Estructura del Proyecto

```
LandingPage/
├── backend/                    # Aplicación Spring Boot
│   ├── src/main/java/com/argcloud/vm/
│   │   ├── controller/        # Controladores REST
│   │   ├── service/          # Lógica de negocio
│   │   ├── repository/       # Acceso a datos
│   │   ├── entity/           # Entidades JPA
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── config/           # Configuraciones
│   │   └── util/             # Utilidades
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # Aplicación Angular
│   ├── src/app/
│   │   ├── components/       # Componentes Angular
│   │   │   ├── virtual-machines/  # Gestión de VMs
│   │   │   ├── dashboard/         # Dashboard principal
│   │   │   └── ...
│   │   ├── services/         # Servicios
│   │   │   └── virtual-machine.service.ts  # Servicio de VMs
│   │   ├── models/           # Modelos TypeScript
│   │   │   └── virtual-machine.model.ts    # Modelo de VM
│   │   ├── guards/           # Guards de rutas
│   │   └── interceptors/     # Interceptores HTTP
│   ├── package.json
│   ├── angular.json
│   ├── Dockerfile
│   └── nginx.conf
└── docker-compose.yml
```

## 🔐 Autenticación y Seguridad

### Flujo de Registro
1. Usuario se registra con email, nombre, apellido y contraseña
2. La contraseña se hashea con BCrypt
3. Se genera un token de verificación único
4. Se envía email de confirmación
5. Usuario hace clic en el enlace del email para verificar su cuenta

### Flujo de Login
1. Usuario ingresa email y contraseña
2. Backend verifica credenciales y estado de verificación
3. Se genera JWT token
4. Token se usa para autenticar requests posteriores

## 🌐 API Endpoints

### Autenticación (`/api/auth`)
- `POST /login` - Iniciar sesión
- `POST /register` - Registrar usuario
- `GET /verify-email?token=...` - Verificar email

### Dashboard (`/api/dashboard`) - Requiere autenticación
- `GET /welcome` - Mensaje de bienvenida

### Máquinas Virtuales (`/api/vms`) - Requiere autenticación
- `GET /` - Listar todas las VMs del usuario
- `POST /` - Crear nueva VM
- `GET /{id}` - Obtener detalles de una VM específica
- `PUT /{id}/start` - Iniciar VM
- `PUT /{id}/stop` - Parar VM
- `PUT /{id}/restart` - Reiniciar VM
- `DELETE /{id}` - Eliminar VM
- `POST /{id}/sync` - Sincronizar con Proxmox
- `GET /nodes` - Obtener nodos Proxmox disponibles

### Ejemplo de Request - Crear VM

```json
POST /api/vms
{
  "name": "Servidor Web Ubuntu",
  "os": "Ubuntu 22.04",
  "cpu": 2,
  "memory": 2048,
  "disk": 50,
  "nodeName": "proxmox-node-1"
}
```

### Ejemplo de Response - Lista de VMs

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Servidor Web Ubuntu",
      "status": "running",
      "os": "Ubuntu 22.04",
      "cpu": 2,
      "memory": 2048,
      "disk": 50,
      "ipAddress": "192.168.1.100",
      "macAddress": "00:16:3e:12:34:56",
      "nodeName": "proxmox-node-1",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-20T14:45:00Z",
      "userName": "admin"
    }
  ]
}
```

## 🎨 Funcionalidades del Frontend

### Páginas
- **Home**: Landing page con información del servicio
- **Login**: Formulario de inicio de sesión
- **Register**: Formulario de registro
- **Dashboard**: Panel de administración con vista general de VMs
- **Virtual Machines**: Gestión completa de máquinas virtuales

### Características
- Diseño responsivo con Bootstrap
- Validación de formularios reactivos
- Manejo de estados de carga
- Navegación con guards de autenticación
- Verificación de email integrada
- Notificaciones toast con NGX-Toastr
- Iconos FontAwesome
- Animaciones y transiciones suaves

## ⚙️ Configuración de Desarrollo

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

### Base de datos
```bash
docker run --name argcloud-postgres \
  -e POSTGRES_DB=argcloud_db \
  -e POSTGRES_USER=argcloud_user \
  -e POSTGRES_PASSWORD=argcloud_password \
  -p 5432:5432 -d postgres:15-alpine
```

## 🔧 Configuración de Email

Para habilitar el envío de emails de verificación:

1. **Gmail**: Habilita la autenticación de 2 factores y genera una "Contraseña de aplicación"
2. **Outlook**: Configura SMTP con tu contraseña normal
3. **Otros**: Configura según el proveedor

## 🚀 Próximas Funcionalidades

- [x] Gestión completa de máquinas virtuales
- [x] Interfaz grafica para VMs
- [x] Integración con Proxmox preparada
- [ ] Monitoreo en tiempo real con WebSockets
- [ ] Configuración avanzada de recursos
- [ ] Sistema de plantillas de VM
- [ ] Backup y restauración automática
- [ ] Dashboard con métricas avanzadas
- [ ] API para gestión programática
- [ ] Sistema de facturación
- [ ] Snapshots de VMs
- [ ] Migración entre nodos
- [ ] Consola VNC integrada

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 📞 Soporte

Para soporte técnico o consultas:
- Email: soporte@argcloud.com
- Documentación: [Wiki del proyecto]
- Issues: [GitHub Issues]

---

Desarrollado con ❤️ por el equipo de ArgCloud 