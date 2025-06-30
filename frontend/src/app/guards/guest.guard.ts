import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

/**
 * Guardia de ruta que protege las rutas que solo deben ser accesibles para usuarios no autenticados (invitados).
 * Por ejemplo, las páginas de login y registro.
 */
@Injectable({
  providedIn: 'root'
})
export class GuestGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Determina si una ruta se puede activar.
   * Espera a que el servicio de autenticación se inicialice antes de comprobar el estado de la sesión.
   * @returns Un Observable<boolean> o un boolean que indica si la ruta se puede activar.
   */
  canActivate(): Observable<boolean> | boolean {
    // Si ya está inicializado, proceder inmediatamente
    if (this.authService.isAuthInitialized()) {
      return this.checkAuth();
    }

    // Si no está inicializado, esperar un poco y luego verificar
    return of(null).pipe(
      delay(50),
      map(() => this.checkAuth())
    );
  }

  /**
   * Comprueba si el usuario NO está autenticado. Si lo está, lo redirige al dashboard.
   * @returns `true` si el usuario no está autenticado, `false` en caso contrario.
   */
  private checkAuth(): boolean {
    if (!this.authService.isLoggedIn()) {
      return true; // Permitir acceso si NO está autenticado
    } else {
      console.log('GuestGuard: Usuario ya autenticado, redirigiendo a dashboard');
      this.router.navigate(['/dashboard']); // Redirigir al dashboard si está autenticado
      return false;
    }
  }
} 