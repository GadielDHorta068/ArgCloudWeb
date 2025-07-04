import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

/**
 * Componente para el formulario de registro de nuevos usuarios.
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {
  /** Formulario reactivo para gestionar los datos de registro. */
  registerForm!: FormGroup;
  /** Indica si hay una operación de registro en curso. */
  isLoading = false;
  /** Mensaje de error a mostrar en caso de fallo en el registro. */
  errorMessage = '';
  /** Mensaje de éxito a mostrar tras un registro exitoso. */
  successMessage = '';
  /** Controla la visibilidad del modal de términos y condiciones. */
  showTermsModal = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Método del ciclo de vida de Angular. Se ejecuta al inicializar el componente.
   * Crea el formulario de registro con sus validaciones.
   */
  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      acceptTerms: [false, [Validators.requiredTrue]] // Campo requerido para aceptar términos
    });
  }

  /** Getter para acceder fácilmente al control del campo de nombre. */
  get firstName() { return this.registerForm.get('firstName'); }
  /** Getter para acceder fácilmente al control del campo de apellido. */
  get lastName() { return this.registerForm.get('lastName'); }
  /** Getter para acceder fácilmente al control del campo de email. */
  get email() { return this.registerForm.get('email'); }
  /** Getter para acceder fácilmente al control del campo de contraseña. */
  get password() { return this.registerForm.get('password'); }
  /** Getter para acceder fácilmente al control del campo de aceptación de términos. */
  get acceptTerms() { return this.registerForm.get('acceptTerms'); }

  /**
   * Se ejecuta al enviar el formulario.
   * Si el formulario es válido, llama al servicio de autenticación para registrar al usuario.
   */
  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.authService.register(this.registerForm.value).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = response.message;
          this.registerForm.reset();
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Error al crear la cuenta';
        }
      });
    }
  }

  /**
   * Abre el modal de términos y condiciones para que el usuario pueda revisarlos.
   */
  openTermsAndConditions(): void {
    this.showTermsModal = true;
  }

  /**
   * Se ejecuta cuando el usuario cierra el modal sin aceptar los términos.
   */
  onTermsModalClose(): void {
    this.showTermsModal = false;
  }

  /**
   * Se ejecuta cuando el usuario acepta los términos desde el modal.
   * Marca automáticamente el checkbox de aceptación de términos.
   */
  onTermsAccepted(): void {
    this.showTermsModal = false;
    // Marcar automáticamente el checkbox de términos como aceptado
    this.registerForm.patchValue({ acceptTerms: true });
  }
} 