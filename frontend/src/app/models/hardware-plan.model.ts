/**
 * Modelo que representa un plan de hardware disponible para los usuarios
 */
export interface HardwarePlan {
  /** ID único del plan */
  id: number;
  /** Nombre del plan */
  name: string;
  /** Descripción del plan */
  description: string;
  /** Precio mensual en pesos argentinos */
  monthlyPrice: number;
  /** Precio anual en pesos argentinos (con descuento) */
  yearlyPrice: number;
  /** CPU total disponible (en cores) */
  totalCpu: number;
  /** RAM total disponible (en MB) */
  totalMemory: number;
  /** Almacenamiento total disponible (en GB) */
  totalDisk: number;
  /** Número máximo de VMs que se pueden crear */
  maxVMs: number;
  /** Ancho de banda mensual (en GB) */
  monthlyBandwidth: number;
  /** Soporte técnico incluido */
  supportLevel: 'basic' | 'standard' | 'premium';
  /** Características adicionales */
  features: string[];
  /** Color del tema para la tarjeta */
  color: 'primary' | 'success' | 'info' | 'warning' | 'danger' | 'secondary';
  /** Icono Font Awesome para representar el plan */
  icon: string;
  /** Si el plan está activo */
  isActive: boolean;
  /** Si es el plan más popular */
  isPopular: boolean;
  /** Fecha de creación */
  createdAt: string;
  /** Fecha de última actualización */
  updatedAt?: string;
}

/**
 * Modelo que representa la suscripción de un usuario a un plan
 */
export interface UserSubscription {
  /** ID único de la suscripción */
  id: number;
  /** ID del usuario */
  userId: number;
  /** ID del plan */
  planId: number;
  /** Detalles del plan */
  plan: HardwarePlan;
  /** Tipo de suscripción */
  subscriptionType: 'monthly' | 'yearly';
  /** Estado de la suscripción */
  status: 'active' | 'expired' | 'cancelled' | 'pending';
  /** Fecha de inicio */
  startDate: string;
  /** Fecha de finalización */
  endDate: string;
  /** CPU utilizada actualmente */
  usedCpu: number;
  /** RAM utilizada actualmente */
  usedMemory: number;
  /** Almacenamiento utilizado actualmente */
  usedDisk: number;
  /** Número de VMs creadas */
  currentVMs: number;
  /** Última facturación */
  lastBillingDate?: string;
  /** Próxima facturación */
  nextBillingDate?: string;
}

/**
 * Modelo para la solicitud de pago
 */
export interface PaymentRequest {
  /** ID del plan seleccionado */
  planId: number;
  /** Tipo de suscripción */
  subscriptionType: 'monthly' | 'yearly';
  /** Email del usuario */
  email: string;
  /** Información adicional */
  additionalInfo?: string;
}

/**
 * Modelo para la respuesta de Mercado Pago
 */
export interface PaymentResponse {
  /** URL de redirección a Mercado Pago */
  checkoutUrl: string;
  /** ID del pago en Mercado Pago */
  paymentId: string;
  /** Token de la transacción */
  transactionToken: string;
  /** Estado del pago */
  status: 'pending' | 'approved' | 'rejected';
  /** ID de la suscripción creada */
  subscriptionId: number;
}

/**
 * Modelo para el resumen de recursos del usuario
 */
export interface ResourceSummary {
  /** Recursos totales del plan */
  total: {
    cpu: number;
    memory: number;
    disk: number;
    maxVMs: number;
  };
  /** Recursos utilizados */
  used: {
    cpu: number;
    memory: number;
    disk: number;
    currentVMs: number;
  };
  /** Recursos disponibles */
  available: {
    cpu: number;
    memory: number;
    disk: number;
    availableVMs: number;
  };
  /** Porcentaje de uso */
  usage: {
    cpu: number;
    memory: number;
    disk: number;
    vms: number;
  };
} 