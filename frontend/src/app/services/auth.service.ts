import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { 
  LoginRequest, 
  RegisterRequest, 
  AuthResponse, 
  MessageResponse, 
  User,
  ResetPasswordRequest
} from '../models/user.model';

const AUTH_API = 'http://localhost:8080/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

/**
 * Servicio para gestionar la autenticación de usuarios.
 * Proporciona métodos para el inicio de sesión, registro, cierre de sesión,
 * y manejo de la información del usuario y el token JWT.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  /** Observable que emite la información del usuario actual. */
  public currentUser$ = this.currentUserSubject.asObservable();
  private isInitialized = false;

  constructor(private http: HttpClient) {
    this.initializeAuth();
  }

  /**
   * Inicializa el estado de autenticación al cargar el servicio,
   * comprobando si hay un usuario y un token en el almacenamiento local.
   */
  private initializeAuth(): void {
    const storedUser = localStorage.getItem('user');
    const storedToken = localStorage.getItem('token');
    
    if (storedUser && storedToken) {
      try {
        const user = JSON.parse(storedUser);
        // Verificar que el token no esté obviamente expirado
        if (this.isTokenExpired(storedToken)) {
          this.clearAuthData();
        } else {
          this.currentUserSubject.next(user);
        }
      } catch (error) {
        this.clearAuthData();
      }
    }
    this.isInitialized = true;
  }

  /**
   * Comprueba si un token JWT ha expirado.
   * @param token El token JWT a verificar.
   * @returns `true` si el token ha expirado, `false` en caso contrario.
   */
  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);
      return payload.exp < now;
    } catch (error) {
      return true; // Si no se puede validar, considerarlo expirado
    }
  }

  /**
   * Limpia los datos de autenticación del almacenamiento local y del estado del servicio.
   */
  private clearAuthData(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  /**
   * Envía una solicitud de inicio de sesión al backend.
   * @param credentials Las credenciales de inicio de sesión del usuario.
   * @returns Un Observable con la respuesta de autenticación.
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(AUTH_API + 'login', credentials, httpOptions)
      .pipe(
        tap(response => {
          this.saveToken(response.token);
          const user: User = {
            id: response.id,
            email: response.email,
            firstName: response.firstName,
            lastName: response.lastName,
            emailVerified: response.emailVerified
          };
          this.saveUser(user);
          this.currentUserSubject.next(user);
        })
      );
  }

  /**
   * Envía una solicitud de registro de un nuevo usuario al backend.
   * @param userData Los datos del usuario a registrar.
   * @returns Un Observable con la respuesta del mensaje.
   */
  register(userData: RegisterRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(AUTH_API + 'register', userData, httpOptions);
  }

  /**
   * Envía una solicitud para iniciar el proceso de recuperación de contraseña.
   * @param credentials Un objeto que contiene el email del usuario.
   * @returns Un Observable con la respuesta del mensaje.
   */
  forgotPassword(credentials: { email: string }): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(AUTH_API + 'forgot-password', credentials, httpOptions);
  }

  /**
   * Envía una solicitud para restablecer la contraseña de un usuario.
   * @param data Los datos para el restablecimiento, incluyendo token y nueva contraseña.
   * @returns Un Observable con la respuesta del mensaje.
   */
  resetPassword(data: ResetPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(AUTH_API + 'reset-password', data, httpOptions);
  }

  /**
   * Envía una solicitud para verificar un correo electrónico a través de un token.
   * @param token El token de verificación.
   * @returns Un Observable con la respuesta del mensaje.
   */
  verifyEmail(token: string): Observable<MessageResponse> {
    return this.http.get<MessageResponse>(AUTH_API + 'verify-email?token=' + token);
  }

  /**
   * Elimina el token y los datos del usuario del almacenamiento local y actualiza el estado de autenticación.
   */
  public logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  /**
   * Guarda el token JWT en el almacenamiento local.
   * @param token El token a guardar.
   */
  public saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  /**
   * Obtiene el token JWT del almacenamiento local.
   * @returns El token JWT o `null` si no se encuentra.
   */
  public getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Guarda la información del usuario en el almacenamiento local.
   * @param user El objeto de usuario a guardar.
   */
  public saveUser(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  /**
   * Obtiene la información del usuario del almacenamiento local.
   * @returns El objeto de usuario o `null` si no se encuentra.
   */
  public getUser(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  /**
   * Comprueba si el usuario está actualmente autenticado.
   * @returns `true` si el usuario tiene una sesión activa y un token válido, `false` en caso contrario.
   */
  public isLoggedIn(): boolean {
    const token = this.getToken();
    const user = this.currentUserSubject.value;
    
    // Verificar que existe token, usuario en memoria, y que el token no esté expirado
    if (!token || !user) {
      return false;
    }
    
    if (this.isTokenExpired(token)) {
      this.clearAuthData();
      return false;
    }
    
    return true;
  }

  /**
   * Obtiene el usuario actualmente autenticado desde el BehaviorSubject.
   * @returns El usuario actual o `null` si no hay nadie autenticado.
   */
  public getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Comprueba si el servicio de autenticación está inicializado.
   * @returns `true` si el servicio está inicializado, `false` en caso contrario.
   */
  public isAuthInitialized(): boolean {
    return this.isInitialized;
  }

} 