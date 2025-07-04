import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP que adjunta el token de autenticación a las solicitudes
 * y maneja los errores de autenticación (401 Unauthorized).
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Intercepta las solicitudes HTTP salientes para añadir el token JWT
   * en la cabecera de autorización. Si se recibe un error 401,
   * se cierra la sesión del usuario y se le redirige al login.
   * @param req La solicitud HTTP saliente.
   * @param next El siguiente manejador de la cadena de interceptores.
   * @returns Un Observable del evento HTTP.
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

    let authReq = req;
    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
} 