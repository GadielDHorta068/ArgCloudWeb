import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

/**
 * Componente para el formulario de inicio de sesión.
 * Permite a los usuarios autenticarse en la aplicación.
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  /** Formulario reactivo para gestionar los datos de inicio de sesión. */
  loginForm!: FormGroup;
  /** Indica si hay una operación de inicio de sesión en curso. */
  isLoading = false;
  /** Mensaje de error a mostrar en caso de fallo en el inicio de sesión. */
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Método del ciclo de vida de Angular. Se ejecuta al inicializar el componente.
   * Crea el formulario de inicio de sesión con sus validaciones.
   */
  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  /** Getter para acceder fácilmente al control del campo de email. */
  get email() { return this.loginForm.get('email'); }
  /** Getter para acceder fácilmente al control del campo de contraseña. */
  get password() { return this.loginForm.get('password'); }

  /**
   * Se ejecuta al enviar el formulario.
   * Si el formulario es válido, llama al servicio de autenticación para iniciar sesión.
   */
  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Error al iniciar sesión';
        }
      });
    }
  }

  /**
   * Navega a la página de recuperación de contraseña.
   */
  navigateToForgotPassword(): void {
    this.router.navigate(['/forgot-password']);
  }
} 