/**
 * Modelo que representa un proyecto en la sección "Nuestros Proyectos"
 */
export interface Project {
  /** ID único del proyecto */
  id: number;
  /** Nombre del proyecto */
  name: string;
  /** Descripción breve del proyecto */
  description: string;
  /** URL del repositorio de GitHub */
  githubUrl: string;
  /** Tecnologías utilizadas en el proyecto */
  technologies: string[];
  /** Icono Font Awesome para representar el proyecto */
  icon: string;
  /** Color del tema para la tarjeta */
  color: 'primary' | 'success' | 'info' | 'warning' | 'danger' | 'secondary';
  /** Fecha de creación o última actualización */
  lastUpdated: string;
  /** Estado del proyecto */
  status: 'active' | 'maintenance' | 'archived';
} 