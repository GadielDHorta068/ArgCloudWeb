import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

/**
 * Componente del panel de control del usuario.
 * Muestra información de la cuenta, estadísticas (actualmente placeholders)
 * y una sección para la gestión de máquinas virtuales.
 */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  /** El usuario actualmente autenticado. */
  currentUser: User | null = null;

  constructor(
    private authService: AuthService,
    private http: HttpClient
  ) {}

  /**
   * Método del ciclo de vida de Angular. Se ejecuta al inicializar el componente.
   * Obtiene la información del usuario actual y solicita los datos del dashboard al backend.
   */
  ngOnInit(): void {
    this.currentUser = this.authService.getUser();
    
    // Obtener mensaje de bienvenida del backend
    this.getDashboardData();
  }

  /**
   * Obtiene datos del backend para el dashboard.
   * Actualmente solo solicita un mensaje de bienvenida.
   */
  private getDashboardData(): void {
    // El interceptor agregará automáticamente el token Bearer
    this.http.get('http://localhost:8080/api/dashboard/welcome')
      .subscribe({
        next: (response: any) => {
          console.log('Dashboard data:', response);
        },
        error: (error) => {
          console.error('Error fetching dashboard data:', error);
          // El interceptor manejará automáticamente los errores 401
        }
      });
  }
} 