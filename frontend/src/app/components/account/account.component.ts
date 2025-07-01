import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/auth.service';
import { HardwarePlanService } from '../../services/hardware-plan.service';
import { UserService, UpdateProfileRequest, ChangePasswordRequest, UserProfileResponse } from '../../services/user.service';
import { User } from '../../models/user.model';
import { UserSubscription } from '../../models/hardware-plan.model';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit, OnDestroy {
  activeSection = 'details';
  currentUser: User | null = null;
  currentSubscription: UserSubscription | null = null;
  isLoadingSubscription = false;
  isUpdatingPassword = false;
  isUpdatingProfile = false;
  isDeletingAccount = false;
  
  // Formulario de cambio de contraseña
  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  
  // Formulario de edición de usuario
  userForm = {
    firstName: '',
    lastName: '',
    email: ''
  };
  
  private userSubscription: Subscription | undefined;
  private subscriptionSubscription: Subscription | undefined;

  constructor(
    private authService: AuthService,
    private hardwarePlanService: HardwarePlanService,
    private userService: UserService,
    private toastr: ToastrService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.userForm = {
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email
        };
        this.loadCurrentSubscription();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
    if (this.subscriptionSubscription) {
      this.subscriptionSubscription.unsubscribe();
    }
  }

  /**
   * Carga la suscripción actual del usuario
   */
  loadCurrentSubscription(): void {
    this.isLoadingSubscription = true;
    this.subscriptionSubscription = this.hardwarePlanService.getCurrentSubscription().subscribe({
      next: (subscription) => {
        this.currentSubscription = subscription;
        this.isLoadingSubscription = false;
      },
      error: (error) => {
        console.error('Error cargando suscripción:', error);
        this.isLoadingSubscription = false;
        // No mostrar error toast aquí, es normal no tener suscripción
      }
    });
  }

  /**
   * Cambia de sección en el menú lateral
   */
  showSection(section: string, event: Event): void {
    event.preventDefault();
    this.activeSection = section;

    // Lógica para la clase 'active' en los enlaces de navegación
    const links = document.querySelectorAll('.list-group-item');
    links.forEach(link => link.classList.remove('active'));
    (event.currentTarget as HTMLElement).classList.add('active');
  }

  /**
   * Actualiza la información del usuario
   */
  updateUserInfo(): void {
    if (this.isUpdatingProfile) {
      return;
    }

    // Validaciones
    if (!this.userForm.firstName || this.userForm.firstName.trim().length < 2) {
      this.toastr.error('El nombre debe tener al menos 2 caracteres');
      return;
    }

    if (!this.userForm.lastName || this.userForm.lastName.trim().length < 2) {
      this.toastr.error('El apellido debe tener al menos 2 caracteres');
      return;
    }

    this.isUpdatingProfile = true;

    const updateData: UpdateProfileRequest = {
      firstName: this.userForm.firstName.trim(),
      lastName: this.userForm.lastName.trim()
    };

    this.userService.updateProfile(updateData).subscribe({
      next: (updatedProfile: UserProfileResponse) => {
        this.isUpdatingProfile = false;
        this.toastr.success('Perfil actualizado correctamente');
        
        // Actualizar el usuario en el AuthService
        const updatedUser: User = {
          id: updatedProfile.id,
          email: updatedProfile.email,
          firstName: updatedProfile.firstName,
          lastName: updatedProfile.lastName,
          emailVerified: updatedProfile.emailVerified
        };
        
        this.authService.saveUser(updatedUser);
        this.currentUser = updatedUser;
      },
      error: (error) => {
        this.isUpdatingProfile = false;
        console.error('Error actualizando perfil:', error);
        if (error.status === 400) {
          this.toastr.error('Datos inválidos. Verifica la información ingresada.');
        } else if (error.status === 401) {
          this.toastr.error('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          this.toastr.error('Error al actualizar el perfil. Inténtalo de nuevo.');
        }
      }
    });
  }

  /**
   * Cambia la contraseña del usuario
   */
  changePassword(): void {
    if (this.isUpdatingPassword) {
      return;
    }

    // Validaciones
    if (!this.passwordForm.currentPassword) {
      this.toastr.error('La contraseña actual es requerida');
      return;
    }

    if (!this.passwordForm.newPassword || this.passwordForm.newPassword.length < 8) {
      this.toastr.error('La nueva contraseña debe tener al menos 8 caracteres');
      return;
    }

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.toastr.error('Las contraseñas no coinciden');
      return;
    }

    if (this.passwordForm.currentPassword === this.passwordForm.newPassword) {
      this.toastr.error('La nueva contraseña debe ser diferente a la actual');
      return;
    }

    this.isUpdatingPassword = true;

    const passwordData: ChangePasswordRequest = {
      currentPassword: this.passwordForm.currentPassword,
      newPassword: this.passwordForm.newPassword,
      confirmPassword: this.passwordForm.confirmPassword
    };

    this.userService.changePassword(passwordData).subscribe({
      next: (response) => {
        this.isUpdatingPassword = false;
        this.toastr.success('Contraseña actualizada correctamente');
        
        // Limpiar el formulario
        this.passwordForm = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        };
      },
      error: (error) => {
        this.isUpdatingPassword = false;
        console.error('Error cambiando contraseña:', error);
        
        if (error.status === 400) {
          this.toastr.error(error.error || 'Contraseña actual incorrecta');
        } else if (error.status === 401) {
          this.toastr.error('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          this.toastr.error('Error al cambiar la contraseña. Inténtalo de nuevo.');
        }
      }
    });
  }

  /**
   * Elimina la cuenta del usuario
   */
  deleteAccount(): void {
    if (this.isDeletingAccount) {
      return;
    }

    const confirmMessage = '¿Estás completamente seguro de que quieres eliminar tu cuenta?\n\n' +
      'Esta acción es IRREVERSIBLE y eliminará:\n' +
      '• Toda tu información personal\n' +
      '• Todas tus máquinas virtuales\n' +
      '• Tu suscripción activa\n' +
      '• Todo el historial de pagos\n\n' +
      'Escribe "ELIMINAR" para confirmar:';

    const userConfirmation = prompt(confirmMessage);
    
    if (userConfirmation !== 'ELIMINAR') {
      if (userConfirmation !== null) {
        this.toastr.warning('Eliminación cancelada. Debes escribir exactamente "ELIMINAR" para confirmar.');
      }
      return;
    }

    this.isDeletingAccount = true;

    this.userService.deleteAccount().subscribe({
      next: (response) => {
        this.isDeletingAccount = false;
        this.toastr.success('Tu cuenta ha sido eliminada correctamente');
        
        // Cerrar sesión y redirigir
        this.authService.logout();
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.isDeletingAccount = false;
        console.error('Error eliminando cuenta:', error);
        
        if (error.status === 401) {
          this.toastr.error('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          this.toastr.error('Error al eliminar la cuenta. Inténtalo de nuevo o contacta soporte.');
        }
      }
    });
  }

  /**
   * Formatea el precio para mostrar
   */
  formatPrice(price: number): string {
    return this.hardwarePlanService.formatPrice(price);
  }

  /**
   * Obtiene el precio actual de la suscripción
   */
  getCurrentPrice(): string {
    if (!this.currentSubscription) return '$0';
    
    const price = this.currentSubscription.subscriptionType === 'yearly' 
      ? this.currentSubscription.plan.yearlyPrice 
      : this.currentSubscription.plan.monthlyPrice;
    
    return this.formatPrice(price);
  }

  /**
   * Obtiene el estado de la suscripción con estilo
   */
  getSubscriptionStatusClass(): string {
    if (!this.currentSubscription) return 'badge bg-secondary';
    
    switch (this.currentSubscription.status) {
      case 'active':
        return 'badge bg-success';
      case 'expired':
        return 'badge bg-danger';
      case 'cancelled':
        return 'badge bg-warning';
      case 'pending':
        return 'badge bg-info';
      default:
        return 'badge bg-secondary';
    }
  }

  /**
   * Obtiene el texto del estado de la suscripción
   */
  getSubscriptionStatusText(): string {
    if (!this.currentSubscription) return 'Sin estado';
    
    switch (this.currentSubscription.status) {
      case 'active':
        return 'Activa';
      case 'expired':
        return 'Expirada';
      case 'cancelled':
        return 'Cancelada';
      case 'pending':
        return 'Pendiente';
      default:
        return 'Desconocido';
    }
  }

  /**
   * Obtiene el texto del tipo de suscripción
   */
  getSubscriptionTypeText(): string {
    if (!this.currentSubscription) return 'N/A';
    
    return this.currentSubscription.subscriptionType === 'yearly' ? 'Anual' : 'Mensual';
  }
}
