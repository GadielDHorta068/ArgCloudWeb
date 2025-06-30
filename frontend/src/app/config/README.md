# Configuración de Proyectos - ArgCloud

## Principio OCP (Open/Closed Principle)

Esta configuración sigue el **Principio Abierto/Cerrado**: está **abierta para extensión** (agregar nuevos proyectos) pero **cerrada para modificación** (no necesitas cambiar código existente).

## Cómo agregar un nuevo proyecto

### 1. Edita el archivo `projects.config.ts`

Simplemente agrega un nuevo objeto `Project` al array `PROJECTS_CONFIG`:

```typescript
{
  id: 7, // ID único (siguiente número disponible)
  name: 'Mi Nuevo Proyecto',
  description: 'Descripción breve del proyecto',
  githubUrl: 'https://github.com/usuario/repo',
  technologies: ['Vue.js', 'Express', 'MongoDB'],
  icon: 'fas fa-code', // Icono de Font Awesome
  color: 'primary', // primary | success | info | warning | danger | secondary
  lastUpdated: '2024-01-20',
  status: 'active' // active | maintenance | archived
}
```

### 2. ¡Eso es todo!

No necesitas:
- ❌ Modificar componentes existentes
- ❌ Cambiar templates HTML
- ❌ Actualizar estilos CSS
- ❌ Tocar la lógica de navegación

El sistema automáticamente:
- ✅ Mostrará el nuevo proyecto en la página
- ✅ Aplicará las animaciones correspondientes
- ✅ Configurará el enlace a GitHub
- ✅ Usará el color y estilo apropiados

## Propiedades del modelo Project

| Propiedad | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `id` | `number` | Identificador único | `1` |
| `name` | `string` | Nombre del proyecto | `"ArgCloud Platform"` |
| `description` | `string` | Descripción breve | `"Plataforma de gestión..."` |
| `githubUrl` | `string` | URL del repositorio | `"https://github.com/..."` |
| `technologies` | `string[]` | Tecnologías usadas | `["Angular", "Spring Boot"]` |
| `icon` | `string` | Clase de Font Awesome | `"fas fa-cloud"` |
| `color` | `string` | Color del tema | `"primary"` |
| `lastUpdated` | `string` | Fecha (YYYY-MM-DD) | `"2024-01-15"` |
| `status` | `string` | Estado del proyecto | `"active"` |

## Colores disponibles

- `primary` - Azul
- `success` - Verde  
- `info` - Cian
- `warning` - Amarillo
- `danger` - Rojo
- `secondary` - Gris

## Estados disponibles

- `active` - Proyecto activo (se muestra)
- `maintenance` - En mantenimiento (se muestra)
- `archived` - Archivado (no se muestra)

## Iconos recomendados

Usa iconos de [Font Awesome](https://fontawesome.com/icons):

- `fas fa-cloud` - Para plataformas cloud
- `fas fa-server` - Para herramientas de servidor
- `fas fa-chart-line` - Para dashboards/analytics
- `fas fa-shield-alt` - Para herramientas de seguridad
- `fas fa-database` - Para herramientas de datos
- `fas fa-terminal` - Para herramientas CLI
- `fas fa-code` - Para librerías/frameworks
- `fas fa-mobile-alt` - Para apps móviles
- `fas fa-globe` - Para aplicaciones web

## Ejemplo completo

```typescript
export const PROJECTS_CONFIG: Project[] = [
  // ... proyectos existentes ...
  {
    id: 7,
    name: 'Mobile App',
    description: 'Aplicación móvil para gestionar VMs desde dispositivos móviles con notificaciones push.',
    githubUrl: 'https://github.com/argcloud/mobile-app',
    technologies: ['React Native', 'TypeScript', 'Firebase'],
    icon: 'fas fa-mobile-alt',
    color: 'info',
    lastUpdated: '2024-01-22',
    status: 'active'
  }
];
```

## Funciones helper disponibles

- `getProjectsByStatus('active')` - Obtener solo proyectos activos
- `getProjectById(1)` - Obtener proyecto por ID

¡Es así de simple! 🚀 