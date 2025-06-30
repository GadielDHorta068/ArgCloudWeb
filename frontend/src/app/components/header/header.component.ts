import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { Subscription } from 'rxjs';

/**
 * Componente del encabezado de la aplicación.
 * Muestra los enlaces de navegación, el nombre del usuario y las opciones de sesión.
 */
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  /** El usuario actualmente autenticado, o null si no hay sesión activa. */
  currentUser: User | null = null;
  /** Indica si el usuario ha iniciado sesión. */
  isLoggedIn: boolean = false;
  private userSubscription: Subscription = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Método del ciclo de vida de Angular. Se ejecuta al inicializar el componente.
   * Se suscribe a los cambios en el usuario actual para actualizar la vista.
   */
  ngOnInit(): void {
    // Inicializar estado
    this.currentUser = this.authService.getCurrentUser();
    this.isLoggedIn = this.authService.isLoggedIn();
    
    // Suscribirse a cambios
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
      this.cdr.detectChanges(); // Forzar detección de cambios
    });
  }

  /**
   * Método del ciclo de vida de Angular. Se ejecuta al destruir el componente.
   * Cancela la suscripción al observable del usuario para evitar fugas de memoria.
   */
  ngOnDestroy(): void {
    this.userSubscription.unsubscribe();
  }

  /** Navega a la página de inicio. */
  navigateHome(): void {
    this.router.navigate(['/']);
  }

  /** Navega a la página de inicio de sesión. */
  navigateLogin(): void {
    this.router.navigate(['/login']);
  }

  /** Navega a la página de registro. */
  navigateRegister(): void {
    this.router.navigate(['/register']);
  }

  /** Navega al dashboard del usuario. */
  navigateDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  /** Navega a la página de máquinas virtuales. */
  navigateVirtualMachines(): void {
    this.router.navigate(['/virtual-machines']);
  }

  /** Cierra la sesión del usuario y lo redirige a la página de inicio. */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
} 