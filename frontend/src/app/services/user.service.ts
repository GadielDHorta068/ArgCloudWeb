import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/**
 * DTOs para las peticiones del usuario
 */
export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface UserProfileResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  emailVerified: boolean;
}

/**
 * Servicio para gestionar operaciones de usuario.
 * Conecta con los endpoints de UserController en el backend.
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  private readonly apiUrl = `${environment.apiUrl || 'http://localhost:8080'}/api/users`;

  constructor(private http: HttpClient) { }

  /**
   * Obtiene el perfil del usuario actual.
   * @returns Observable con la información del perfil del usuario
   */
  getUserProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/profile`);
  }

  /**
   * Actualiza la información del perfil del usuario.
   * @param updateData Datos para actualizar el perfil
   * @returns Observable con el perfil actualizado
   */
  updateProfile(updateData: UpdateProfileRequest): Observable<UserProfileResponse> {
    return this.http.put<UserProfileResponse>(`${this.apiUrl}/profile`, updateData);
  }

  /**
   * Cambia la contraseña del usuario.
   * @param passwordData Datos para cambiar la contraseña
   * @returns Observable con el mensaje de confirmación
   */
  changePassword(passwordData: ChangePasswordRequest): Observable<string> {
    return this.http.put(`${this.apiUrl}/change-password`, passwordData, { responseType: 'text' });
  }

  /**
   * Elimina la cuenta del usuario.
   * @returns Observable con el mensaje de confirmación
   */
  deleteAccount(): Observable<string> {
    return this.http.delete(`${this.apiUrl}/account`, { responseType: 'text' });
  }
} 