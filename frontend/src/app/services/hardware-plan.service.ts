import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { HardwarePlan, UserSubscription, PaymentRequest, PaymentResponse, ResourceSummary } from '../models/hardware-plan.model';

/**
 * Servicio para manejar planes de hardware, suscripciones y pagos.
 * Incluye integración con Mercado Pago y gestión de recursos.
 */
@Injectable({
  providedIn: 'root'
})
export class HardwarePlanService {

  private readonly apiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) { }

  /**
   * Obtiene todos los planes de hardware activos.
   * @returns Observable con la lista de planes disponibles.
   */
  getActivePlans(): Observable<HardwarePlan[]> {
    return this.http.get<HardwarePlan[]>(`${this.apiUrl}/plans`);
  }

  /**
   * Obtiene un plan específico por ID.
   * @param planId ID del plan a obtener.
   * @returns Observable con el plan solicitado.
   */
  getPlan(planId: number): Observable<HardwarePlan> {
    return this.http.get<HardwarePlan>(`${this.apiUrl}/plans/${planId}`);
  }

  /**
   * Obtiene el plan popular (destacado).
   * @returns Observable con el plan popular si existe.
   */
  getPopularPlan(): Observable<HardwarePlan> {
    return this.http.get<HardwarePlan>(`${this.apiUrl}/plans/popular`);
  }

  /**
   * Busca planes por rango de precio.
   * @param minPrice precio mínimo.
   * @param maxPrice precio máximo.
   * @returns Observable con planes en el rango especificado.
   */
  getPlansByPriceRange(minPrice: number, maxPrice: number): Observable<HardwarePlan[]> {
    return this.http.get<HardwarePlan[]>(`${this.apiUrl}/plans/price-range?min=${minPrice}&max=${maxPrice}`);
  }

  /**
   * Obtiene planes que cumplan con recursos mínimos.
   * @param minCpu CPU mínimo requerido.
   * @param minMemory memoria mínima requerida.
   * @param minDisk disco mínimo requerido.
   * @returns Observable con planes que cumplen los requisitos.
   */
  getPlansByMinimumResources(minCpu: number, minMemory: number, minDisk: number): Observable<HardwarePlan[]> {
    return this.http.get<HardwarePlan[]>(`${this.apiUrl}/plans/minimum-resources?cpu=${minCpu}&memory=${minMemory}&disk=${minDisk}`);
  }

  // ============================================
  // GESTIÓN DE SUSCRIPCIONES
  // ============================================

  /**
   * Obtiene la suscripción activa del usuario actual.
   * @returns Observable con la suscripción activa o null si no tiene.
   */
  getCurrentSubscription(): Observable<UserSubscription | null> {
    return this.http.get<UserSubscription | null>(`${this.apiUrl}/subscriptions/current`);
  }

  /**
   * Obtiene el historial de suscripciones del usuario.
   * @returns Observable con todas las suscripciones del usuario.
   */
  getSubscriptionHistory(): Observable<UserSubscription[]> {
    return this.http.get<UserSubscription[]>(`${this.apiUrl}/subscriptions/history`);
  }

  /**
   * Obtiene el resumen de recursos del usuario.
   * @returns Observable con el resumen detallado de recursos utilizados vs disponibles.
   */
  getResourceSummary(): Observable<ResourceSummary> {
    return this.http.get<ResourceSummary>(`${this.apiUrl}/subscriptions/resources`);
  }

  /**
   * Verifica si el usuario puede crear una VM con los recursos especificados.
   * @param cpu CPU requerido.
   * @param memory memoria requerida.
   * @param disk disco requerido.
   * @returns Observable con true si puede crear la VM, false en caso contrario.
   */
  canCreateVM(cpu: number, memory: number, disk: number): Observable<{ canCreate: boolean; reason?: string }> {
    return this.http.post<{ canCreate: boolean; reason?: string }>(`${this.apiUrl}/subscriptions/can-create-vm`, {
      cpu,
      memory,
      disk
    });
  }

  // ============================================
  // GESTIÓN DE PAGOS CON MERCADO PAGO
  // ============================================

  /**
   * Obtiene la clave pública de MercadoPago para inicializar CardForm.
   * @returns Observable con la clave pública
   */
  getMercadoPagoPublicKey(): Observable<{public_key: string}> {
    return this.http.get<{public_key: string}>(`${this.apiUrl}/payments/public-key`);
  }

  /**
   * Crea un pago usando CardForm con token de tarjeta.
   * @param paymentData datos del pago incluyendo token de CardForm
   * @returns Observable con la respuesta del pago
   */
  createPaymentWithCardForm(paymentData: any): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/payments/create`, paymentData);
  }

  /**
   * Crea un pago con los datos del formulario de tarjeta.
   * @param createPaymentData - Datos completos del pago incluyendo token y datos de la tarjeta.
   * @returns Observable con la respuesta del pago.
   */
  createPayment(createPaymentData: any): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.apiUrl}/payments/create`, createPaymentData);
  }

  /**
   * Verifica el estado de un pago específico.
   * @param paymentId ID del pago en Mercado Pago.
   * @returns Observable con el estado actualizado del pago.
   */
  checkPaymentStatus(paymentId: string): Observable<{ status: string; subscription?: UserSubscription }> {
    return this.http.get<{ status: string; subscription?: UserSubscription }>(`${this.apiUrl}/payments/${paymentId}/status`);
  }

  /**
   * Obtiene el historial de pagos del usuario.
   * @returns Observable con todos los pagos realizados por el usuario.
   */
  getPaymentHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/payments/history`);
  }

  /**
   * Cancela una suscripción activa.
   * @param subscriptionId ID de la suscripción a cancelar.
   * @returns Observable con la confirmación de cancelación.
   */
  cancelSubscription(subscriptionId: number): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/subscriptions/${subscriptionId}/cancel`, {});
  }

  // ============================================
  // MÉTODOS AUXILIARES
  // ============================================

  /**
   * Calcula el descuento del plan anual vs mensual.
   * @param plan el plan de hardware.
   * @returns porcentaje de descuento (0-100).
   */
  calculateYearlyDiscount(plan: HardwarePlan): number {
    if (!plan.monthlyPrice || !plan.yearlyPrice) {
      return 0;
    }
    const monthlyTotal = plan.monthlyPrice * 12;
    const savings = monthlyTotal - plan.yearlyPrice;
    return Math.round((savings / monthlyTotal) * 100);
  }

  /**
   * Formatea el precio para mostrar en la UI.
   * @param price precio a formatear.
   * @returns precio formateado en pesos argentinos.
   */
  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(price);
  }

  /**
   * Formatea bytes a una unidad legible.
   * @param bytes cantidad en bytes.
   * @returns string formateado (ej: "2 GB", "512 MB").
   */
  formatBytes(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * Formatea memoria de MB a una unidad legible.
   * @param memoryMB memoria en MB.
   * @returns string formateado.
   */
  formatMemory(memoryMB: number): string {
    if (memoryMB >= 1024) {
      return `${(memoryMB / 1024).toFixed(memoryMB % 1024 === 0 ? 0 : 1)} GB`;
    }
    return `${memoryMB} MB`;
  }

  /**
   * Obtiene el color del badge según el nivel de soporte.
   * @param supportLevel nivel de soporte.
   * @returns clase CSS para el color del badge.
   */
  getSupportLevelBadgeClass(supportLevel: string): string {
    switch (supportLevel) {
      case 'basic': return 'badge-secondary';
      case 'standard': return 'badge-primary';
      case 'premium': return 'badge-warning';
      default: return 'badge-secondary';
    }
  }

  /**
   * Obtiene el color de la barra de progreso según el porcentaje de uso.
   * @param usagePercentage porcentaje de uso (0-100).
   * @returns clase CSS para el color de la barra.
   */
  getUsageProgressClass(usagePercentage: number): string {
    if (usagePercentage >= 90) return 'progress-bar-danger';
    if (usagePercentage >= 75) return 'progress-bar-warning';
    if (usagePercentage >= 50) return 'progress-bar-info';
    return 'progress-bar-success';
  }

  /**
   * Verifica si un plan es recomendado según los recursos actuales del usuario.
   * @param plan plan a evaluar.
   * @param currentUsage uso actual de recursos.
   * @returns true si el plan es recomendado.
   */
  isPlanRecommended(plan: HardwarePlan, currentUsage?: { cpu: number; memory: number; disk: number; vms: number }): boolean {
    if (!currentUsage) return false;

    // Un plan es recomendado si puede manejar el doble del uso actual
    const recommendedCpu = currentUsage.cpu * 2;
    const recommendedMemory = currentUsage.memory * 2;
    const recommendedDisk = currentUsage.disk * 2;
    const recommendedVMs = currentUsage.vms * 2;

    return plan.totalCpu >= recommendedCpu &&
           plan.totalMemory >= recommendedMemory &&
           plan.totalDisk >= recommendedDisk &&
           plan.maxVMs >= recommendedVMs;
  }

  // ============================================
  // GESTIÓN DE ERRORES Y ESTADOS
  // ============================================

  /**
   * Maneja errores comunes de la API.
   * @param error error recibido.
   * @returns mensaje de error user-friendly.
   */
  handleApiError(error: any): string {
    if (error.status === 401) {
      return 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.';
    }
    if (error.status === 403) {
      return 'No tienes permisos para realizar esta acción.';
    }
    if (error.status === 404) {
      return 'El recurso solicitado no fue encontrado.';
    }
    if (error.status === 400) {
      return error.error?.message || 'Los datos enviados no son válidos.';
    }
    if (error.status === 500) {
      return 'Error interno del servidor. Intenta nuevamente más tarde.';
    }
    return 'Ocurrió un error inesperado. Por favor, intenta nuevamente.';
  }
} 