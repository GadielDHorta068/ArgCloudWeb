import { Project } from '../models/project.model';

/**
 * Configuración de proyectos para la sección "Nuestros Proyectos"
 * 
 * Para agregar un nuevo proyecto, simplemente añade un nuevo objeto Project al array.
 * Para quitar un proyecto, elimina el objeto correspondiente del array.
 * 
 * Principio OCP (Open/Closed): Esta configuración está abierta para extensión
 * (agregar nuevos proyectos) pero cerrada para modificación (no necesitas
 * cambiar código existente para agregar proyectos).
 */
export const PROJECTS_CONFIG: Project[] = [
  {
    id: 1,
    name: 'ArgCloud Platform',
    description: 'Plataforma completa de gestión de máquinas virtuales en la nube con interfaz moderna y API REST.',
    githubUrl: 'https://github.com/argcloud/platform',
    technologies: ['Angular', 'Spring Boot', 'PostgreSQL', 'Docker'],
    icon: 'fas fa-cloud',
    color: 'primary',
    lastUpdated: '2024-01-15',
    status: 'active'
  },
  {
    id: 2,
    name: 'PrivacyMe',
    description: 'PrivacyMe es una aplicación de Android que permite seleccionar archivos multimedia (imágenes y videos) y eliminar sus metadatos, como la ubicación, la fecha y la información del dispositivo, para proteger la privacidad del usuario.',
    githubUrl: 'https://github.com/GadielDHorta068/PrivacyMe',
    technologies: ['Kotlin', 'Android Studio', 'Java', 'XML'],
    icon: 'fas fa-mobile-alt',
    color: 'success',
    lastUpdated: '2024-01-12',
    status: 'active'
  },
  {
    id: 3,
    name: 'SortMethods',
    description: 'SortMethods es una aplicación de Android que permite ordenar archivos multimedia (imágenes y videos) por fecha, ubicación, nombre y tamaño, para facilitar la organización del usuario.',
    githubUrl: 'https://github.com/GadielDHorta068/SortMethods',
    technologies: ['HTML', 'CSS', 'JavaScript'],
    icon: 'fas fa-mobile-alt',
    color: 'info',
    lastUpdated: '2024-01-10',
    status: 'active'
  },
  {
    id: 4,
    name: 'Security Scanner',
    description: 'Escáner de vulnerabilidades automatizado para máquinas virtuales y contenedores.',
    githubUrl: 'https://github.com/argcloud/security-scanner',
    technologies: ['Go', 'Docker', 'OpenVAS', 'NMAP'],
    icon: 'fas fa-shield-alt',
    color: 'warning',
    lastUpdated: '2024-01-08',
    status: 'maintenance'
  },
  {
    id: 5,
    name: 'Backup Manager',
    description: 'Sistema automatizado de respaldos incrementales para máquinas virtuales.',
    githubUrl: 'https://github.com/argcloud/backup-manager',
    technologies: ['Bash', 'Python', 'Cron', 'AWS S3'],
    icon: 'fas fa-database',
    color: 'secondary',
    lastUpdated: '2024-01-05',
    status: 'active'
  },
  {
    id: 6,
    name: 'CLI Tools',
    description: 'Herramientas de línea de comandos para interactuar con la API de ArgCloud.',
    githubUrl: 'https://github.com/argcloud/cli-tools',
    technologies: ['TypeScript', 'Commander.js', 'Axios'],
    icon: 'fas fa-terminal',
    color: 'danger',
    lastUpdated: '2024-01-03',
    status: 'active'
  }
];

/**
 * Función helper para obtener proyectos filtrados por estado
 * @param status Estado de los proyectos a filtrar
 * @returns Array de proyectos filtrados
 */
export function getProjectsByStatus(status?: Project['status']): Project[] {
  if (!status) {
    return PROJECTS_CONFIG;
  }
  return PROJECTS_CONFIG.filter(project => project.status === status);
}

/**
 * Función helper para obtener un proyecto por ID
 * @param id ID del proyecto
 * @returns Proyecto encontrado o undefined
 */
export function getProjectById(id: number): Project | undefined {
  return PROJECTS_CONFIG.find(project => project.id === id);
} 