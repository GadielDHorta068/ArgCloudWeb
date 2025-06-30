/**
 * Configuración de entorno para producción.
 * Este archivo reemplaza environment.ts durante el build de producción.
 */
export const environment = {
  production: true,
  apiUrl: '', // Se configura en build time o runtime
  appName: 'ArgCloud',
  version: '1.0.0',
  
  // Configuración de Mercado Pago (se obtiene del backend)
  mercadoPagoPublicKey: '', // Se carga dinámicamente desde el backend
  
  // URLs de la aplicación (se configuran en build time)
  frontendUrl: '', // Se configura según el dominio de producción
  
  // Configuración de features
  features: {
    payments: true,
    proxmoxIntegration: true,
    mikrotikIntegration: true,
    emailVerification: true
  },
  
  // Configuración de UI
  ui: {
    toastDuration: 4000,
    loadingDelay: 300,
    autoRefreshInterval: 60000, // Menos frecuente en producción
    maxRetries: 5
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