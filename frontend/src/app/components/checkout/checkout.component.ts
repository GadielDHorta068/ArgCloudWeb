import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HardwarePlanService } from '../../services/hardware-plan.service';
import { AuthService } from '../../services/auth.service';
import { HardwarePlan, PaymentRequest } from '../../models/hardware-plan.model';

// Declaraciones TypeScript para MercadoPago
declare var MercadoPago: any;

/**
 * Componente de checkout que implementa CardForm de MercadoPago.
 * Sigue exactamente la documentación oficial de MercadoPago.
 * https://www.mercadopago.com.ar/developers/es/docs/checkout-api/integration-configuration/card/web-integration
 */
@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit, OnDestroy {

  selectedPlan: HardwarePlan | null = null;
  subscriptionType: 'monthly' | 'yearly' = 'monthly';

  // Variables de MercadoPago
  private mp: any;
  private cardForm: any;
  
  // Estados del componente
  isLoading = true;
  isProcessingPayment = false;
  
  // Datos del formulario
  formData = {
    email: '',
    identificationType: 'DNI',
    identificationNumber: '',
    cardholderName: ''
  };

  // Opciones para tipos de identificación (Argentina)
  identificationTypes = [
    { id: 'DNI', name: 'DNI' },
    { id: 'LC', name: 'LC' },
    { id: 'LE', name: 'LE' },
    { id: 'CI', name: 'CI' },
    { id: 'PASSPORT', name: 'Pasaporte' }
  ];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private toastr: ToastrService,
    private hardwarePlanService: HardwarePlanService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadPlanFromParams();
  }

  ngOnDestroy(): void {
    if (this.cardForm) {
      this.cardForm.unmount();
    }
  }

  /**
   * Carga el plan seleccionado desde los parámetros de la URL.
   */
  private loadPlanFromParams(): void {
    this.route.queryParams.subscribe(params => {
      const planId = params['planId'];
      const subscriptionType = params['subscriptionType'];

      if (planId) {
        // Cargar el plan desde el backend
        this.hardwarePlanService.getPlan(planId).subscribe({
          next: (plan) => {
            this.selectedPlan = plan;
            this.subscriptionType = subscriptionType || 'monthly';
            this.initializeCheckout();
          },
          error: (error) => {
            console.error('Error cargando plan:', error);
            this.toastr.error('Error al cargar el plan seleccionado');
            this.router.navigate(['/pricing']);
          }
        });
      } else {
        // Si no hay planId, redirigir a pricing
        this.toastr.warning('No se ha seleccionado un plan');
        this.router.navigate(['/pricing']);
      }
    });
  }

  /**
   * Inicializa el checkout de MercadoPago.
   * Obtiene la clave pública y configura CardForm.
   */
  private async initializeCheckout(): Promise<void> {
    try {
      if (typeof MercadoPago === 'undefined') {
        throw new Error('MercadoPago SDK no está disponible. Verifique la conexión a internet.');
      }

      const publicKeyResponse = await this.hardwarePlanService.getMercadoPagoPublicKey().toPromise();
      const publicKey = publicKeyResponse?.public_key;

      if (!publicKey) {
        throw new Error('No se pudo obtener la clave pública de MercadoPago');
      }

      this.mp = new MercadoPago(publicKey);
      
      const currentUser = this.authService.getCurrentUser();
      if (currentUser) {
        this.formData.email = currentUser.email;
      }

      this.isLoading = false;
      
      setTimeout(async () => {
        try {
          await this.setupCardForm();
        } catch (error) {
          console.error('Error configurando CardForm:', error);
          this.toastr.error('Error configurando el formulario de pago');
        }
      }, 100);

    } catch (error) {
      console.error('Error inicializando checkout:', error);
      this.toastr.error('Error cargando el sistema de pagos. Verifique su conexión e intente nuevamente.');
      this.isLoading = false;
      
      setTimeout(() => {
        this.router.navigate(['/pricing']);
      }, 3000);
    }
  }

  /**
   * Configura el CardForm de MercadoPago según la documentación oficial.
   */
  private async setupCardForm(): Promise<void> {
    try {
      let container = document.querySelector('#form-checkout');
      let attempts = 0;
      const maxAttempts = 10;

      while (!container && attempts < maxAttempts) {
        await new Promise(resolve => setTimeout(resolve, 50));
        container = document.querySelector('#form-checkout');
        attempts++;
      }

      if (!container) {
        throw new Error('Contenedor #form-checkout no encontrado después de múltiples intentos');
      }

      const cardFormOptions = {
        // Configuración según la documentación oficial actualizada de MercadoPago
        amount: this.subscriptionType === 'yearly' 
          ? this.selectedPlan!.yearlyPrice.toString() 
          : this.selectedPlan!.monthlyPrice.toString(),
        form: {
          id: 'form-checkout',
          cardholderName: {
            id: 'form-checkout__cardholderName',
            placeholder: 'Titular de la tarjeta'
          },
          cardholderEmail: {
            id: 'form-checkout__cardholderEmail',
            placeholder: 'E-mail'
          },
          cardNumber: {
            id: 'form-checkout__cardNumber',
            placeholder: '1234 1234 1234 1234'
          },
          expirationDate: {
            id: 'form-checkout__expirationDate',
            placeholder: 'MM/YY'
          },
          securityCode: {
            id: 'form-checkout__securityCode',
            placeholder: '123'
          },
          installments: {
            id: 'form-checkout__installments',
            placeholder: 'Cuotas'
          },
          identificationType: {
            id: 'form-checkout__identificationType'
          },
          identificationNumber: {
            id: 'form-checkout__identificationNumber',
            placeholder: 'Número de documento'
          },
          issuer: {
            id: 'form-checkout__issuer'
          }
        },
        
        // Callbacks para manejar eventos
        callbacks: {
          onFormMounted: (error: any) => {
            if (error) {
              console.error('Error montando CardForm:', error);
              console.error('Detalles del error:', JSON.stringify(error, null, 2));
              this.toastr.error('Error cargando formulario de pago');
            }
          },
          onFormUnmounted: (error: any) => {
            if (error) {
              console.error('Error desmontando CardForm:', error);
            }
          },
          onIdentificationTypesReceived: (error: any, identificationTypes: any) => {
            if (error) {
              console.error('Error recibiendo tipos de identificación:', error);
            }
          },
          onInstallmentsReceived: (error: any, installments: any) => {
            if (error) {
              console.error('Error recibiendo cuotas:', error);
            }
          },
          onCardTokenReceived: (error: any, token: any) => {
            if (error) {
              console.error('Error al crear token de tarjeta:', error);
              this.toastr.error('Error de validación. Revise los datos de su tarjeta.');
              this.isProcessingPayment = false;
            } else {
              this.processPaymentWithToken(token);
            }
          },
          onSubmit: (event: { preventDefault: () => void; }) => {
            // Este callback se activa al hacer submit, pero usamos nuestro propio botón
          },
          onError: (error: any) => {
            console.error('Error general en CardForm:', error);
            this.toastr.error('Ocurrió un error inesperado. Intente nuevamente.');
            this.isProcessingPayment = false;
          }
        }
      };

      this.cardForm = this.mp.cardForm(cardFormOptions);
      if (!this.cardForm) {
        throw new Error('No se pudo inicializar cardForm. Verifique la instancia de MercadoPago.');
      }

    } catch (error) {
      console.error('Error crítico en setupCardForm:', error);
      this.toastr.error('Error fatal al configurar el formulario. Recargue la página.');
    }
  }

  /**
   * Maneja el envío del formulario de pago.
   * @param event evento del formulario
   */
  onSubmitPayment(event: Event): void {
    event.preventDefault();

    if (!this.selectedPlan) {
      this.toastr.error('No hay plan seleccionado');
      return;
    }

    if (this.isProcessingPayment) {
      return;
    }

    // Verificar que CardForm esté inicializado
    if (!this.cardForm) {
      this.toastr.error('El formulario de pago no está listo. Espere un momento.');
      return;
    }

    // Validar datos del formulario
    if (!this.validateForm()) {
      return;
    }

    this.isProcessingPayment = true;
    this.toastr.info('Procesando pago...');

    try {
      // Crear el token de la tarjeta
      this.cardForm.createCardToken();
    } catch (error) {
      console.error('Error al llamar a createCardToken:', error);
      this.toastr.error('No se pudo iniciar el proceso de pago. Intente nuevamente.');
      this.isProcessingPayment = false;
    }
  }

  /**
   * Valida los datos del formulario antes de enviar.
   */
  private validateForm(): boolean {
    // Validar datos básicos
    if (!this.formData.email || !this.formData.email.includes('@')) {
      this.toastr.error('Email inválido');
      return false;
    }

    if (!this.formData.identificationNumber || this.formData.identificationNumber.length < 7) {
      this.toastr.error('Número de documento inválido (mínimo 7 dígitos)');
      return false;
    }

    if (!this.formData.cardholderName || this.formData.cardholderName.length < 3) {
      this.toastr.error('Nombre del titular inválido (mínimo 3 caracteres)');
      return false;
    }

    // Validar campos del formulario de MercadoPago
    const cardNumber = (document.querySelector('#form-checkout__cardNumber') as HTMLInputElement)?.value;
    const expirationDate = (document.querySelector('#form-checkout__expirationDate') as HTMLInputElement)?.value;
    const securityCode = (document.querySelector('#form-checkout__securityCode') as HTMLInputElement)?.value;
    
    if (!cardNumber || cardNumber.replace(/\s/g, '').length < 13) {
      this.toastr.error('Número de tarjeta inválido');
      return false;
    }

    if (!expirationDate || !expirationDate.match(/^\d{2}\/\d{2}$/)) {
      this.toastr.error('Fecha de expiración inválida (MM/YY)');
      return false;
    }

    if (!securityCode || securityCode.length < 3) {
      this.toastr.error('Código de seguridad inválido');
      return false;
    }

    // Validar que la fecha no esté vencida
    if (expirationDate) {
      const [month, year] = expirationDate.split('/');
      const expDate = new Date(2000 + parseInt(year), parseInt(month) - 1);
      const now = new Date();
      
      if (expDate < now) {
        this.toastr.error('La tarjeta está vencida');
        return false;
      }
    }

    console.log('✅ Validación del formulario completada exitosamente');
    return true;
  }

  /**
   * Procesa el pago una vez que se ha obtenido el token de la tarjeta.
   * @param cardFormData - Datos del formulario de tarjeta, incluido el token.
   */
  private processPaymentWithToken(cardFormData: any): void {
    if (!this.selectedPlan) {
      this.toastr.error('No se ha seleccionado un plan');
      this.isProcessingPayment = false;
      return;
    }

    const paymentRequest: PaymentRequest = {
      planId: this.selectedPlan.id,
      subscriptionType: this.subscriptionType,
      email: this.formData.email,
    };

    const fullPaymentData = {
      ...paymentRequest,
      token: cardFormData.token,
      paymentMethodId: cardFormData.payment_method_id,
      issuerId: cardFormData.issuer_id,
      installments: cardFormData.installments,
      identificationType: this.formData.identificationType,
      identificationNumber: this.formData.identificationNumber,
    };

    this.hardwarePlanService.createPayment(fullPaymentData).subscribe({
      next: (response) => {
        this.isProcessingPayment = false;
        this.toastr.success('¡Pago realizado con éxito! Redirigiendo...', 'Éxito');
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isProcessingPayment = false;
        console.error('Error procesando pago:', error);
        this.toastr.error('Ocurrió un error al procesar el pago. Por favor, intente nuevamente.');
      }
    });
  }

  /**
   * Obtiene el precio a mostrar según el tipo de suscripción.
   */
  getDisplayPrice(): string {
    if (!this.selectedPlan) return '$0';
    
    const price = this.subscriptionType === 'yearly' 
      ? this.selectedPlan.yearlyPrice 
      : this.selectedPlan.monthlyPrice;
    
    return this.hardwarePlanService.formatPrice(price);
  }

  /**
   * Cancela el proceso de checkout.
   */
  cancelCheckout(): void {
    this.router.navigate(['/pricing']);
  }
} 