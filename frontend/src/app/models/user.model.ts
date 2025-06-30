/**
 * Interfaz que representa la estructura de un objeto de Usuario.
 */
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  emailVerified: boolean;
}

/**
 * Interfaz para la solicitud de inicio de sesión.
 */
export interface LoginRequest {
  email: string;
  password: string;
}

/**
 * Interfaz para la solicitud de registro.
 */
export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

/**
 * Interfaz para la respuesta de autenticación del backend.
 */
export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  emailVerified: boolean;
}

/**
 * Interfaz para una respuesta genérica con un mensaje.
 */
export interface MessageResponse {
  message: string;
} 