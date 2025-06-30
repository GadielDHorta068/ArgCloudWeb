import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';

import { HardwarePlanService } from '../../services/hardware-plan.service';
import { AuthService } from '../../services/auth.service';
import { HardwarePlan, UserSubscription, PaymentRequest } from '../../models/hardware-plan.model';

/**
 * Componente para mostrar los planes de hardware disponibles.
 * Permite a los usuarios ver los planes, compararlos y suscribirse.
 */
@Component({
  selector: 'app-pricing',
  templateUrl: './pricing.component.html',
  styleUrls: ['./pricing.component.css']
})
export class PricingComponent implements OnInit, OnDestroy {

  // Observable para manejar la destrucción del componente
  private destroy$ = new Subject<void>();

  // Estados del componente
  plans: HardwarePlan[] = [];
  currentSubscription: UserSubscription | null = null;
  isLoading = false;
  isLoggedIn = false;
  selectedBillingCycle: 'monthly' | 'yearly' = 'monthly';
  


  // Estados de procesamiento
  processingPayments: Set<number> = new Set();

  constructor(
    public hardwarePlanService: HardwarePlanService,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.checkAuthStatus();
    this.loadPlans();
    this.loadCurrentSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Verifica si el usuario está autenticado.
   */
  private checkAuthStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  /**
   * Carga todos los planes de hardware activos.
   */
  public loadPlans(): void {
    this.isLoading = true;
    
    this.hardwarePlanService.getActivePlans()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (plans) => {
          this.plans = plans.sort((a, b) => a.monthlyPrice - b.monthlyPrice);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error cargando planes:', error);
          this.toastr.error('Error al cargar los planes de hardware');
          this.isLoading = false;
        }
      });
  }

  /**
   * Carga la suscripción actual del usuario.
   */
  private loadCurrentSubscription(): void {
    if (!this.isLoggedIn) return;

    this.hardwarePlanService.getCurrentSubscription()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (subscription) => {
          this.currentSubscription = subscription;
        },
        error: (error) => {
          console.error('Error cargando suscripción actual:', error);
        }
      });
  }

  /**
   * Cambia el ciclo de facturación (mensual/anual).
   */
  changeBillingCycle(cycle: 'monthly' | 'yearly'): void {
    this.selectedBillingCycle = cycle;
  }

  /**
   * Obtiene el precio según el ciclo de facturación seleccionado.
   */
  getPrice(plan: HardwarePlan): number {
    return this.selectedBillingCycle === 'monthly' ? plan.monthlyPrice : plan.yearlyPrice;
  }

  /**
   * Obtiene el texto del precio formateado.
   */
  getPriceText(plan: HardwarePlan): string {
    const price = this.getPrice(plan);
    const formattedPrice = this.hardwarePlanService.formatPrice(price);
    const period = this.selectedBillingCycle === 'monthly' ? '/mes' : '/año';
    return `${formattedPrice}${period}`;
  }

  /**
   * Calcula y formatea el descuento anual.
   */
  getYearlyDiscountText(plan: HardwarePlan): string {
    const discount = this.hardwarePlanService.calculateYearlyDiscount(plan);
    return discount > 0 ? `${discount}% OFF` : '';
  }

  /**
   * Verifica si un plan es el actual del usuario.
   */
  isCurrentPlan(plan: HardwarePlan): boolean {
    return this.currentSubscription?.planId === plan.id;
  }

  /**
   * Verifica si un plan es una actualización del plan actual.
   */
  isUpgrade(plan: HardwarePlan): boolean {
    if (!this.currentSubscription) return false;
    return plan.monthlyPrice > this.currentSubscription.plan.monthlyPrice;
  }

  /**
   * Verifica si un plan es una degradación del plan actual.
   */
  isDowngrade(plan: HardwarePlan): boolean {
    if (!this.currentSubscription) return false;
    return plan.monthlyPrice < this.currentSubscription.plan.monthlyPrice;
  }

  /**
   * Obtiene el texto del botón según el estado del plan.
   */
  getButtonText(plan: HardwarePlan): string {
    if (!this.isLoggedIn) {
      return 'Iniciar Sesión';
    }

    if (this.processingPayments.has(plan.id)) {
      return 'Procesando...';
    }

    if (this.isCurrentPlan(plan)) {
      return 'Plan Actual';
    }

    if (this.isUpgrade(plan)) {
      return 'Actualizar Plan';
    }

    if (this.isDowngrade(plan)) {
      return 'Cambiar Plan';
    }

    return 'Seleccionar Plan';
  }

  /**
   * Obtiene la clase CSS del botón según el estado del plan.
   */
  getButtonClass(plan: HardwarePlan): string {
    if (this.processingPayments.has(plan.id)) {
      return 'btn btn-secondary disabled';
    }

    if (this.isCurrentPlan(plan)) {
      return 'btn btn-success disabled';
    }

    if (plan.isPopular) {
      return 'btn btn-primary';
    }

    return 'btn btn-outline-primary';
  }

  /**
   * Verifica si el botón debe estar deshabilitado.
   */
  isButtonDisabled(plan: HardwarePlan): boolean {
    return this.processingPayments.has(plan.id) || 
           this.isCurrentPlan(plan) || 
           !plan.isActive;
  }

  /**
   * Maneja la selección de un plan.
   */
  selectPlan(plan: HardwarePlan): void {
    if (!this.isLoggedIn) {
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: '/pricing', selectedPlan: plan.id } 
      });
      return;
    }

    if (this.isButtonDisabled(plan)) {
      return;
    }

    this.initiatePurchase(plan);
  }

  /**
   * Inicia el proceso de compra de un plan.
   * Redirige al usuario al checkout integrado con Mercado Pago CardForm.
   */
  private initiatePurchase(plan: HardwarePlan): void {
    // Redirigir al checkout con los datos del plan seleccionado
    this.router.navigate(['/checkout'], {
      queryParams: {
        planId: plan.id,
        subscriptionType: this.selectedBillingCycle
      }
    });
  }

  /**
   * Formatea la memoria para mostrar en la UI.
   */
  formatMemory(memoryMB: number): string {
    return this.hardwarePlanService.formatMemory(memoryMB);
  }

  /**
   * Obtiene la clase CSS del badge de soporte.
   */
  getSupportBadgeClass(supportLevel: string): string {
    return this.hardwarePlanService.getSupportLevelBadgeClass(supportLevel);
  }

  /**
   * Formatea el nivel de soporte para mostrar.
   */
  formatSupportLevel(supportLevel: string): string {
    switch (supportLevel) {
      case 'basic': return 'Básico';
      case 'standard': return 'Estándar';
      case 'premium': return 'Premium';
      default: return supportLevel;
    }
  }

  /**
   * Obtiene el icono de FontAwesome para una característica.
   */
  getFeatureIcon(feature: string): string {
    // Mapeo básico de características a iconos
    if (feature.toLowerCase().includes('cpu') || feature.toLowerCase().includes('core')) {
      return 'fas fa-microchip';
    }
    if (feature.toLowerCase().includes('ram') || feature.toLowerCase().includes('memoria')) {
      return 'fas fa-memory';
    }
    if (feature.toLowerCase().includes('ssd') || feature.toLowerCase().includes('disco')) {
      return 'fas fa-hdd';
    }
    if (feature.toLowerCase().includes('soporte')) {
      return 'fas fa-headset';
    }
    if (feature.toLowerCase().includes('backup')) {
      return 'fas fa-shield-alt';
    }
    if (feature.toLowerCase().includes('monitoreo')) {
      return 'fas fa-chart-line';
    }
    return 'fas fa-check';
  }


} 