import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

/**
 * Guardia de ruta que protege las rutas que requieren autenticación.
 * Solo permite el acceso si el usuario ha iniciado sesión.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

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
   * Comprueba si el usuario está autenticado. Si no lo está, lo redirige a la página de login.
   * @returns `true` si el usuario está autenticado, `false` en caso contrario.
   */
  private checkAuth(): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    } else {
      console.log('AuthGuard: Usuario no autenticado, redirigiendo a login');
      this.router.navigate(['/login']);
      return false;
    }
  }
} 