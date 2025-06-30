import { Injectable } from '@angular/core';

/**
 * Servicio que gestiona los términos y condiciones de la aplicación.
 * Proporciona métodos para obtener el contenido de los términos.
 */
@Injectable({
  providedIn: 'root'
})
export class TermsConditionsService {

  constructor() { }

  /**
   * Obtiene el contenido completo de los términos y condiciones.
   * @returns El contenido HTML de los términos y condiciones
   */
  getTermsContent(): string {
    return `
      <h2 class="mb-4">Términos y Condiciones de Uso - ArgCloud</h2>
      
      <div class="terms-content">
        <h4>1. Aceptación de los Términos</h4>
        <p>Al acceder y utilizar la plataforma ArgCloud, usted acepta estar legalmente vinculado por estos términos y condiciones.</p>
        
        <h4>2. Descripción del Servicio</h4>
        <p>ArgCloud es una plataforma de gestión de máquinas virtuales en la nube que permite a los usuarios crear, configurar, administrar y monitorear infraestructura virtualizada.</p>
        
        <h4>3. Registro de Cuenta</h4>
        <h5>3.1 Elegibilidad</h5>
        <ul>
          <li>Debe ser mayor de 18 años para crear una cuenta</li>
          <li>Debe proporcionar información precisa y completa durante el registro</li>
          <li>Es responsable de mantener la seguridad de su cuenta y contraseña</li>
        </ul>
        
        <h5>3.2 Responsabilidades del Usuario</h5>
        <ul>
          <li>Mantener la confidencialidad de sus credenciales</li>
          <li>Notificar inmediatamente cualquier uso no autorizado de su cuenta</li>
          <li>Actualizar su información personal cuando sea necesario</li>
        </ul>
        
        <h4>4. Uso Aceptable</h4>
        <h5>4.1 Usos Permitidos</h5>
        <ul>
          <li>Gestión legítima de máquinas virtuales</li>
          <li>Desarrollo y pruebas de aplicaciones</li>
          <li>Hospedaje de servicios web legales</li>
        </ul>
        
        <h5>4.2 Usos Prohibidos</h5>
        <ul>
          <li>Actividades ilegales o que violen derechos de terceros</li>
          <li>Distribución de malware o contenido malicioso</li>
          <li>Intentos de comprometer la seguridad del sistema</li>
          <li>Uso excesivo que afecte el rendimiento del servicio</li>
        </ul>
        
        <h4>5. Privacidad y Protección de Datos</h4>
        <p>Recopilamos información necesaria para proporcionar el servicio y procesamos los datos personales conforme a nuestra Política de Privacidad.</p>
        
        <h4>6. Facturación y Pagos</h4>
        <p>Las tarifas se basan en el uso de recursos. Los precios pueden cambiar con previo aviso. Aceptamos las principales tarjetas de crédito/débito.</p>
        
        <h4>7. Disponibilidad del Servicio</h4>
        <p>Nos esforzamos por mantener una disponibilidad del 99.9%. Pueden ocurrir interrupciones por mantenimiento programado.</p>
        
        <h4>8. Limitación de Responsabilidad</h4>
        <p>El servicio se proporciona "tal como está". No seremos responsables por daños indirectos o consecuentes.</p>
        
        <h4>9. Terminación</h4>
        <p>Puede cancelar su cuenta en cualquier momento. Podemos terminar cuentas que violen estos términos.</p>
        
        <h4>10. Modificaciones</h4>
        <p>Podemos modificar estos términos en cualquier momento. Los cambios se notificarán con al menos 30 días de anticipación.</p>
        
        <h4>11. Contacto</h4>
        <p>Si tiene preguntas sobre estos términos, contáctenos en: <strong>legal@argcloud.com</strong></p>
        
        <hr class="my-4">
        <p><small class="text-muted">Al utilizar ArgCloud, usted reconoce que ha leído, entendido y acepta estar vinculado por estos términos y condiciones.</small></p>
      </div>
    `;
  }

  /**
   * Obtiene un resumen corto de los términos principales.
   * @returns Resumen de los términos más importantes
   */
  getTermsSummary(): string[] {
    return [
      'Debe ser mayor de 18 años para usar el servicio',
      'Debe proporcionar información precisa en el registro',
      'Es responsable de mantener segura su cuenta',
      'Solo puede usar el servicio para actividades legales',
      'Respetamos su privacidad según nuestra política',
      'Las tarifas se basan en el uso de recursos'
    ];
  }
} 