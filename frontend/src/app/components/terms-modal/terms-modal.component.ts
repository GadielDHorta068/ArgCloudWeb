import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TermsConditionsService } from '../../services/terms-conditions.service';

/**
 * Componente modal que muestra los términos y condiciones de uso.
 * Se utiliza para que los usuarios puedan revisar los términos antes de aceptarlos.
 */
@Component({
  selector: 'app-terms-modal',
  templateUrl: './terms-modal.component.html',
  styleUrls: ['./terms-modal.component.css']
})
export class TermsModalComponent {
  /** Controla la visibilidad del modal */
  @Input() isVisible: boolean = false;
  /** Evento que se emite cuando el modal se cierra */
  @Output() modalClose = new EventEmitter<void>();
  /** Evento que se emite cuando el usuario acepta los términos */
  @Output() termsAccepted = new EventEmitter<void>();

  /** Contenido HTML de los términos y condiciones */
  termsContent: string;

  constructor(private termsService: TermsConditionsService) {
    // Obtener el contenido de los términos y condiciones desde el servicio
    this.termsContent = this.termsService.getTermsContent();
  }

  /**
   * Cierra el modal sin realizar ninguna acción adicional.
   */
  onClose(): void {
    this.isVisible = false;
    this.modalClose.emit();
  }

  /**
   * Cierra el modal y notifica que el usuario ha aceptado los términos.
   * Este método se ejecuta cuando el usuario hace clic en "Aceptar".
   */
  onAccept(): void {
    this.isVisible = false;
    this.termsAccepted.emit();
  }

  /**
   * Maneja el clic en el backdrop del modal para cerrarlo.
   * @param event Evento del clic
   */
  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
} 