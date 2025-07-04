# ArgCloud - Plataforma de Máquinas Virtuales

ArgCloud es una plataforma completa para la gestión de máquinas virtuales en la nube, desarrollada con Angular (frontend) y Spring Boot (backend), usando PostgreSQL como base de datos. Incluye un sistema completo de planes de hardware parametrizables con pagos integrados vía Mercado Pago.

## 🚀 Características

- **Frontend Angular**: Interfaz moderna y responsiva
- **Backend Spring Boot**: API REST robusta y segura
- **Base de datos PostgreSQL**: Almacenamiento confiable
- **Autenticación JWT**: Sistema de autenticación seguro
- **Verificación por email**: Registro seguro con confirmación
- **Recuperación de contraseña**: Flujo seguro para restablecer la contraseña por email
- **Gestión completa de cuentas**: Actualización de perfil, cambio de contraseña y eliminación de cuenta
- **Gestión completa de VMs**: Crear, administrar y monitorear máquinas virtuales
- **Sistema de Planes de Hardware**: Planes parametrizables con recursos específicos
- **Pagos con Mercado Pago**: Integración completa con CardForm y webhooks
- **Gestión de Suscripciones**: Control de recursos y facturación
- **Dashboard de Recursos**: Monitoreo en tiempo real del uso de recursos
- **Panel de Cuenta Avanzado**: Vista completa de suscripción, uso de recursos y configuración
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
- Mercado Pago SDK

### Infraestructura
- Proxmox VE (virtualización)
- MikroTik (networking)
- Docker & Docker Compose

## 📋 Prerrequisitos

- Docker y Docker Compose instalados
- Git
- Cuenta de Mercado Pago (para pagos)
- Servidor Proxmox VE (opcional para funcionalidad completa)
- Router MikroTik (opcional para gestión de red)

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd LandingPage
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
# Base de datos
DB_NAME=argcloud_db
DB_USERNAME=argcloud_user
DB_PASSWORD=tu_password_seguro

# JWT
JWT_SECRET=8q2HdS}$250z
JWT_EXPIRATION=86400

# Email (para Gmail, necesitas una "App Password")
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password

# MERCADO PAGO (OBLIGATORIO para pagos)
# Obtén estas credenciales en: https://www.mercadopago.com.ar/developers/panel
MERCADOPAGO_ACCESS_TOKEN=TEST-tu-access-token
MERCADOPAGO_PUBLIC_KEY=TEST-tu-public-key
MERCADOPAGO_WEBHOOK_SECRET=tu-webhook-secret
MERCADOPAGO_ENVIRONMENT=sandbox

# URLs de tu aplicación
FRONTEND_URL=http://localhost:4200
BACKEND_URL=http://localhost:8080

# Proxmox (Opcional)
PROXMOX_URL=https://tu-servidor-proxmox:8006
PROXMOX_USERNAME=root@pam
PROXMOX_PASSWORD=tu-password

# MikroTik (Opcional)
MIKROTIK_URL=https://tu-router-mikrotik
MIKROTIK_USERNAME=admin
MIKROTIK_PASSWORD=tu-password
```

### 3. Configurar Mercado Pago

Para habilitar los pagos, necesitas:

1. **Crear cuenta en Mercado Pago**: https://www.mercadopago.com.ar
2. **Obtener credenciales**: Ve a Developers → Panel → Credenciales
3. **Configurar webhooks**: URL de notificación: `http://tu-dominio/api/payments/webhook`

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

## 💳 Sistema de Planes de Hardware

### Características del Sistema

ArgCloud incluye un sistema completo de planes de hardware parametrizables:

#### 🎯 **Gestión de Planes**
- **Planes parametrizables**: CPU, RAM, disco, VMs máximas, ancho de banda
- **Precios flexibles**: Mensual y anual con descuentos automáticos
- **Características personalizables**: Features, colores, iconos
- **Niveles de soporte**: Básico, Estándar, Premium

#### 💰 **Sistema de Pagos**
- **Integración con Mercado Pago**: CardForm oficial con validación automática
- **Múltiples métodos**: Tarjetas de crédito/débito, transferencias
- **Webhooks**: Sincronización automática de estados de pago
- **Protocolo 3DS 2.0**: Seguridad avanzada

#### 📊 **Gestión de Suscripciones**
- **Control de recursos**: Tracking en tiempo real de CPU, RAM, disco, VMs
- **Distribución inteligente**: Un plan de 4GB permite 4 VMs de 1GB cada una
- **Facturación automática**: Renovación mensual/anual
- **Estados de suscripción**: Activa, Pendiente, Cancelada, Expirada

### Acceso a Planes y Pagos

#### 🔗 **Puntos de entrada:**
1. **Header**: Clic en "Planes" (visible desde cualquier página)
2. **Dashboard**: Botón "Ver Planes" en acciones rápidas
3. **URL directa**: `/pricing`

#### 💳 **Flujo de compra:**
1. **Explorar planes** → Página con comparación de planes
2. **Seleccionar plan** → Clic en "Seleccionar Plan"
3. **Pago seguro** → CardForm de Mercado Pago integrado
4. **Confirmación** → Activación automática del plan

## 🖥️ Gestión de Máquinas Virtuales

### Características Principales

ArgCloud ofrece una gestión completa de máquinas virtuales con las siguientes funcionalidades:

#### 🔧 **Operaciones de VM**
- **Crear VM**: Interfaz intuitiva con validación de recursos disponibles
- **Iniciar/Parar**: Control completo del estado de las VMs
- **Reiniciar**: Reinicio seguro de máquinas virtuales
- **Eliminar**: Eliminación segura con confirmación
- **Sincronizar**: Sincronización con el servidor Proxmox

#### 📊 **Monitoreo y Estadísticas**
- **Dashboard de recursos**: Vista en tiempo real del uso vs disponible
- **Alertas críticas**: Notificaciones cuando los recursos están al límite
- **Verificador de VMs**: Comprueba si puedes crear una VM con recursos específicos
- **Recomendaciones**: Sugerencias para optimizar el uso

#### 🎨 **Interfaz de Usuario**
- **Dashboard integrado**: Vista general de todas las VMs
- **Resource Summary**: Componente dedicado para monitorear recursos
- **Tarjetas informativas**: Cada VM se muestra con información completa
- **Responsive**: Funciona perfectamente en desktop y móvil

## 👤 Gestión de Cuentas de Usuario

### Características del Sistema de Cuentas

ArgCloud incluye un sistema completo de gestión de cuentas de usuario:

#### 🔧 **Panel de Cuenta**
- **Información personal**: Actualización de nombre y apellido
- **Gestión de contraseña**: Cambio seguro con verificación de contraseña actual
- **Vista de suscripción**: Información detallada del plan activo y uso de recursos
- **Eliminación de cuenta**: Proceso seguro con confirmación y limpieza completa de datos

#### 📊 **Información de Suscripción**
- **Estado en tiempo real**: Visualización del estado actual de la suscripción
- **Uso de recursos**: Monitoreo detallado de CPU, RAM, disco y VMs activas
- **Fechas importantes**: Inicio de suscripción y próxima renovación
- **Gestión de plan**: Acceso directo para cambiar o actualizar el plan

#### 🔐 **Seguridad y Privacidad**
- **Validación de contraseña**: Verificación de contraseña actual antes de cambios
- **Eliminación segura**: Proceso de eliminación que incluye todas las dependencias
- **Transacciones atómicas**: Operaciones de base de datos seguras y consistentes
- **Auditoría de cambios**: Registro de modificaciones importantes

### Acceso al Panel de Cuenta

#### 🔗 **Puntos de entrada:**
1. **Header**: Menú de usuario → "Mi Cuenta"
2. **Dashboard**: Sección de información personal
3. **URL directa**: `/account`

#### 📱 **Secciones del Panel:**
1. **Mis Datos** → Actualización de información personal
2. **Mi Suscripción** → Vista completa del plan y recursos
3. **Cambiar Contraseña** → Gestión segura de credenciales
4. **Eliminar Cuenta** → Proceso de eliminación con advertencias

## �� Estructura del Proyecto


```

## 📁 Estructura del Proyecto

```
LandingPage/
├── backend/                    # Aplicación Spring Boot
│   ├── src/main/java/com/argcloud/vm/
│   │   ├── controller/        # Controladores REST
│   │   │   ├── AuthController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── HardwarePlanController.java
│   │   │   ├── PaymentController.java
│   │   │   ├── SubscriptionController.java
│   │   │   ├── UserController.java          # NUEVO - Gestión de usuarios
│   │   │   └── VirtualMachineController.java
│   │   ├── service/          # Lógica de negocio
│   │   │   ├── EmailService.java
│   │   │   ├── MercadoPagoService.java
│   │   │   ├── UserService.java             # ACTUALIZADO - Nuevos métodos
│   │   │   └── VirtualMachineService.java
│   │   ├── repository/       # Acceso a datos
│   │   │   ├── HardwarePlanRepository.java
│   │   │   ├── PaymentRepository.java
│   │   │   ├── UserRepository.java
│   │   │   ├── UserSubscriptionRepository.java
│   │   │   └── VirtualMachineRepository.java
│   │   ├── entity/           # Entidades JPA
│   │   │   ├── HardwarePlan.java
│   │   │   ├── Payment.java
│   │   │   ├── User.java
│   │   │   ├── UserSubscription.java
│   │   │   └── VirtualMachine.java
│   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── ChangePasswordRequest.java   # NUEVO - Cambio de contraseña
│   │   │   ├── HardwarePlanResponse.java
│   │   │   ├── PaymentRequest.java
│   │   │   ├── PaymentResponse.java
│   │   │   ├── UpdateProfileRequest.java    # NUEVO - Actualización de perfil
│   │   │   ├── UserProfileResponse.java     # NUEVO - Respuesta de perfil
│   │   │   ├── UserSubscriptionResponse.java
│   │   │   └── ...
│   │   ├── config/           # Configuraciones
│   │   └── util/             # Utilidades
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # Aplicación Angular
│   ├── src/app/
│   │   ├── components/       # Componentes Angular
│   │   │   ├── account/             # NUEVO - Panel de gestión de cuenta
│   │   │   ├── checkout/            # Checkout con Mercado Pago
│   │   │   ├── dashboard/           # Dashboard principal
│   │   │   ├── pricing/             # Página de planes
│   │   │   ├── resource-summary/    # Dashboard de recursos
│   │   │   ├── virtual-machines/    # Gestión de VMs
│   │   │   └── ...
│   │   ├── services/         # Servicios
│   │   │   ├── hardware-plan.service.ts # Servicio de planes
│   │   │   ├── user.service.ts          # NUEVO - Servicio de usuarios
│   │   │   ├── virtual-machine.service.ts
│   │   │   └── ...
│   │   ├── models/           # Modelos TypeScript
│   │   │   ├── hardware-plan.model.ts   # Modelos de planes
│   │   │   ├── user.model.ts            # NUEVO - Modelos de usuario
│   │   │   ├── virtual-machine.model.ts
│   │   │   └── ...
│   │   ├── guards/           # Guards de rutas
│   │   └── interceptors/     # Interceptores HTTP
│   ├── package.json
│   ├── angular.json
│   ├── Dockerfile
│   └── nginx.conf
├── .env                       # Variables de entorno
├── docker-compose.yml         # Configuración Docker
└── README.md                 # Documentación
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

### Flujo de Recuperación de Contraseña
1. Usuario ingresa su email en el formulario de "Olvidé mi contraseña"
2. El sistema genera un token de restablecimiento único y lo envía al email del usuario
3. Usuario hace clic en el enlace del email, que lo redirige a una página para crear una nueva contraseña
4. El usuario ingresa y confirma la nueva contraseña
5. La contraseña se actualiza en la base de datos de forma segura

## 🌐 API Endpoints

### Autenticación (`/api/auth`)
- `POST /login` - Iniciar sesión
- `POST /register` - Registrar usuario
- `GET /verify-email?token=...` - Verificar email
- `POST /forgot-password` - Solicitar token para restablecer contraseña
- `POST /reset-password` - Restablecer contraseña con token

### Gestión de Usuarios (`/api/users`) - **NUEVO** - Requiere autenticación
- `GET /profile` - Obtener perfil del usuario actual
- `PUT /profile` - Actualizar información del perfil
- `PUT /change-password` - Cambiar contraseña del usuario
- `DELETE /account` - Eliminar cuenta permanentemente

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

### Planes de Hardware (`/api/plans`) - Nuevos endpoints
- `GET /` - Listar todos los planes activos
- `GET /{id}` - Obtener detalles de un plan específico
- `GET /active` - Obtener solo planes activos
- `GET /filter` - Filtrar planes por criterios

### Suscripciones (`/api/subscriptions`) - Nuevos endpoints
- `GET /current` - Obtener suscripción actual del usuario
- `GET /history` - Historial de suscripciones
- `GET /resources` - Resumen de recursos disponibles/utilizados
- `POST /can-create-vm` - Verificar si se puede crear una VM
- `GET /usage` - Estadísticas de uso de recursos

### Pagos (`/api/payments`) - Nuevos endpoints
- `GET /public-key` - Obtener clave pública de Mercado Pago
- `POST /create` - Crear nuevo pago
- `GET /{id}/status` - Verificar estado de un pago
- `POST /webhook` - Webhook para notificaciones de Mercado Pago
- `GET /history` - Historial de pagos del usuario

### Ejemplo de Request - Crear Pago

```json
POST /api/payments/create
{
  "planId": 1,
  "subscriptionType": "monthly",
  "email": "usuario@example.com",
  "additionalInfo": "Suscripción a plan Básico - monthly"
}
```

### Ejemplo de Response - Crear Pago

```json
{
  "checkoutUrl": "https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=123456",
  "paymentId": "pay_123456",
  "transactionToken": "token_123456",
  "status": "pending",
  "subscriptionId": 1
}
```

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

### Ejemplo de Response - Resumen de Recursos

```json
{
  "total": {
    "cpu": 4,
    "memory": 8192,
    "disk": 200,
    "maxVMs": 5
  },
  "used": {
    "cpu": 2,
    "memory": 2048,
    "disk": 50,
    "currentVMs": 1
  },
  "available": {
    "cpu": 2,
    "memory": 6144,
    "disk": 150,
    "availableVMs": 4
  },
  "usage": {
    "cpu": 50,
    "memory": 25,
    "disk": 25,
    "vms": 20
  }
}
```

### Ejemplo de Request - Actualizar Perfil

```json
PUT /api/users/profile
{
  "firstName": "Juan Carlos",
  "lastName": "Pérez"
}
```

### Ejemplo de Response - Perfil Actualizado

```json
{
  "id": 1,
  "email": "juan@example.com",
  "firstName": "Juan Carlos",
  "lastName": "Pérez",
  "emailVerified": true
}
```

### Ejemplo de Request - Cambiar Contraseña

```json
PUT /api/users/change-password
{
  "currentPassword": "contraseña_actual",
  "newPassword": "nueva_contraseña_segura",
  "confirmPassword": "nueva_contraseña_segura"
}
```

## 🎨 Funcionalidades del Frontend

### Páginas
- **Home**: Landing page con información del servicio
- **Login**: Formulario de inicio de sesión
- **Register**: Formulario de registro
- **Dashboard**: Panel de administración con vista general y acciones rápidas
- **Virtual Machines**: Gestión completa de máquinas virtuales
- **Pricing**: Página de planes de hardware con comparación y selección
- **Checkout**: Formulario de pago integrado con Mercado Pago CardForm
- **Account**: **NUEVO** - Panel completo de gestión de cuenta de usuario

### Características
- Diseño responsivo con Bootstrap
- Validación de formularios reactivos
- Manejo de estados de carga
- Navegación con guards de autenticación
- Verificación de email integrada
- Notificaciones toast con NGX-Toastr
- Iconos FontAwesome
- Animaciones y transiciones suaves
- Integración completa con Mercado Pago
- Dashboard de recursos en tiempo real
- **Panel de gestión de cuenta avanzado**
- **Vista detallada de suscripción y recursos**
- **Gestión segura de credenciales**

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

## 💳 Configuración de Mercado Pago

### Para Sandbox (Testing)
1. Ve a: https://www.mercadopago.com.ar/developers/panel
2. Crea una aplicación
3. Obtén las credenciales de **Sandbox**
4. Configura las variables de entorno con el prefijo `TEST-`

### Para Producción
1. Completa la verificación de tu cuenta
2. Obtén las credenciales de **Producción**
3. Cambia `MERCADOPAGO_ENVIRONMENT=production`
4. Configura webhooks en tu dominio real

## 🔧 Configuración de Email

Para habilitar el envío de emails de verificación:

1. **Gmail**: Habilita la autenticación de 2 factores y genera una "Contraseña de aplicación"
2. **Outlook**: Configura SMTP con tu contraseña normal
3. **Otros**: Configura según el proveedor

## 🚀 Funcionalidades Completadas

- [x] **Gestión completa de máquinas virtuales**
- [x] **Interfaz gráfica para VMs**
- [x] **Integración con Proxmox preparada**
- [x] **Recuperación de contraseña por email**
- [x] **Sistema de planes de hardware parametrizables**
- [x] **Integración completa con Mercado Pago**
- [x] **Gestión de suscripciones y facturación**
- [x] **Dashboard de recursos en tiempo real**
- [x] **Control de límites por usuario**
- [x] **CardForm de Mercado Pago con validación automática**
- [x] **Webhooks para sincronización de pagos**
- [x] **Sistema de distribución inteligente de recursos**
- [x] **Verificación de recursos antes de crear VMs**
- [x] **Panel completo de gestión de cuenta de usuario**
- [x] **Actualización de perfil de usuario**
- [x] **Cambio seguro de contraseña**
- [x] **Eliminación segura de cuenta**
- [x] **Vista detallada de suscripción en tiempo real**
- [x] **Monitoreo de uso de recursos por usuario**

## 🔮 Próximas Funcionalidades

- [ ] Monitoreo en tiempo real con WebSockets
- [ ] Configuración avanzada de recursos
- [ ] Sistema de plantillas de VM
- [ ] Backup y restauración automática
- [ ] Dashboard con métricas avanzadas
- [ ] API para gestión programática
- [ ] Snapshots de VMs
- [ ] Migración entre nodos
- [ ] Consola VNC integrada
- [ ] Alertas automáticas por email/WhatsApp
- [ ] Sistema de tickets de soporte
- [ ] Métricas de performance en tiempo real
- [ ] Historial de cambios en la cuenta
- [ ] Autenticación de dos factores (2FA)
- [ ] Exportación de datos personales
- [ ] Notificaciones push para cambios de cuenta

## 📝 Licencia

Este proyecto está bajo la Licencia MIT.

## 📞 Soporte

Para soporte técnico o consultas:
- Email: soporte@argcloud.com
- WhatsApp: +54 9 11 2345-6789
- Documentación: [Wiki del proyecto]
- Issues: [GitHub Issues]

---

Desarrollado con ❤️ por el equipo de ArgCloud 