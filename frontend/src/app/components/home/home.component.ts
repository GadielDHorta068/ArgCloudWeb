import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Project } from '../../models/project.model';
import { PROJECTS_CONFIG, getProjectsByStatus } from '../../config/projects.config';

/**
 * Componente de la página de inicio.
 * Muestra la sección principal de bienvenida, características, proyectos y una llamada a la acción.
 * También gestiona la lógica de verificación de correo electrónico si se proporciona un token en la URL.
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  /** Indica si se debe mostrar el mensaje de verificación de email. */
  showVerificationMessage = false;
  /** El mensaje de verificación de email a mostrar. */
  verificationMessage = '';
  /** Lista de proyectos a mostrar en la sección "Nuestros Proyectos" */
  projects: Project[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  /**
   * Método del ciclo de vida de Angular. Se ejecuta al inicializar el componente.
   * Comprueba si existe un token de verificación en los parámetros de la URL para iniciar el proceso.
   * También carga la lista de proyectos activos.
   */
  ngOnInit(): void {
    // Verificar si hay un token de verificación en la URL
    this.route.queryParams.subscribe(params => {
      if (params['token']) {
        this.verifyEmail(params['token']);
      }
    });

    // Cargar proyectos activos
    this.loadProjects();
  }

  /**
   * Carga los proyectos desde la configuración.
   * Solo muestra proyectos con estado 'active'.
   */
  private loadProjects(): void {
    this.projects = getProjectsByStatus('active');
  }

  /**
   * Se desplaza suavemente a la sección de características de la página.
   */
  scrollToFeatures(): void {
    document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' });
  }

  /**
   * Se desplaza suavemente a la sección de proyectos de la página.
   */
  scrollToProjects(): void {
    document.getElementById('projects')?.scrollIntoView({ behavior: 'smooth' });
  }

  /**
   * Navega a la página de registro.
   */
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }

  /**
   * Abre el repositorio de GitHub del proyecto en una nueva pestaña.
   * @param project El proyecto cuyo repositorio se abrirá.
   * @param event El evento del click (opcional).
   */
  openGithubRepo(project: Project, event?: Event): void {
    if (event) {
      event.preventDefault();
    }
    window.open(project.githubUrl, '_blank', 'noopener,noreferrer');
  }

  /**
   * Llama al servicio de autenticación para verificar un email utilizando un token.
   * Muestra un mensaje con el resultado de la operación.
   * @param token El token de verificación de email.
   */
  verifyEmail(token: string): void {
    this.authService.verifyEmail(token).subscribe({
      next: (response) => {
        this.verificationMessage = response.message;
        this.showVerificationMessage = true;
      },
      error: (error) => {
        this.verificationMessage = error.error?.message || 'Error al verificar el email';
        this.showVerificationMessage = true;
      }
    });
  }
} 