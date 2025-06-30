/**
 * Configuración de entorno para desarrollo.
 * Este archivo será reemplazado durante el build por environment.prod.ts en producción.
 */
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  appName: 'ArgCloud',
  version: '1.0.0',
  
  // Configuración de Mercado Pago (se obtiene del backend)
  mercadoPagoPublicKey: '', // Se carga dinámicamente desde el backend
  
  // URLs de la aplicación
  frontendUrl: 'http://localhost:4200',
  
  // Configuración de features
  features: {
    payments: true,
    proxmoxIntegration: true,
    mikrotikIntegration: true,
    emailVerification: true
  },
  
  // Configuración de UI
  ui: {
    toastDuration: 5000,
    loadingDelay: 500,
    autoRefreshInterval: 30000,
    maxRetries: 3
  },
  
  // Configuración de validaciones
  validation: {
    passwordMinLength: 8,
    emailVerificationTokenExpiry: 24, // horas
    jwtTokenExpiry: 86400 // segundos (24 horas)
  },
  
  // URLs de ayuda y documentación
  helpUrls: {
    documentation: 'https://docs.argcloud.com',
    support: 'mailto:soporte@argcloud.com',
    mercadoPagoHelp: 'https://www.mercadopago.com.ar/ayuda'
  }
}; 