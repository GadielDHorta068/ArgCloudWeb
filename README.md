# ArgCloud - Plataforma de Máquinas Virtuales

ArgCloud es una plataforma completa para la gestión de máquinas virtuales en la nube, desarrollada con Angular (frontend) y Spring Boot (backend), usando PostgreSQL como base de datos.

## 🚀 Características

- **Frontend Angular**: Interfaz moderna y responsiva
- **Backend Spring Boot**: API REST robusta y segura
- **Base de datos PostgreSQL**: Almacenamiento confiable
- **Autenticación JWT**: Sistema de autenticación seguro
- **Verificación por email**: Registro seguro con confirmación
- **Docker**: Despliegue con contenedores
- **Arquitectura Business-Repository-Presenter**: Código limpio y mantenible

## 🛠️ Tecnologías

### Frontend
- Angular 17
- TypeScript
- Bootstrap 5
- FontAwesome
- RxJS

### Backend
- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- BCrypt para hash de contraseñas
- JavaMail para envío de emails

## 📋 Prerrequisitos

- Docker y Docker Compose instalados
- Git

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

### 3. Ejecutar con Docker Compose

```bash
docker-compose up --build
```

Este comando:
- Construye las imágenes de backend y frontend
- Inicia PostgreSQL, backend y frontend
- Configura la red entre servicios

### 4. Acceder a la aplicación

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Base de datos**: localhost:5432

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
│   │   ├── services/         # Servicios
│   │   ├── guards/           # Guards de rutas
│   │   └── models/           # Modelos TypeScript
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
- `GET /vms` - Lista de máquinas virtuales (próximamente)

## 🎨 Funcionalidades del Frontend

### Páginas
- **Home**: Landing page con información del servicio
- **Login**: Formulario de inicio de sesión
- **Register**: Formulario de registro
- **Dashboard**: Panel de administración (funcionalidad básica)

### Características
- Diseño responsivo con Bootstrap
- Validación de formularios reactivos
- Manejo de estados de carga
- Navegación con guards de autenticación
- Verificación de email integrada

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

- [ ] Gestión completa de máquinas virtuales
- [ ] Monitoreo en tiempo real
- [ ] Configuración de recursos (CPU, RAM, Almacenamiento)
- [ ] Sistema de facturación
- [ ] Dashboard con métricas avanzadas
- [ ] API para gestión programática
- [ ] Backup y restauración automática

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 📞 Soporte

Para soporte técnico o consultas:
- Email: soporte@argcloud.com
- Documentación: [Wiki del proyecto]
- Issues: [GitHub Issues]

---

Desarrollado con ❤️ por el equipo de ArgCloud 