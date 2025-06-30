import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';

import { HardwarePlanService } from '../../services/hardware-plan.service';
import { ResourceSummary, UserSubscription } from '../../models/hardware-plan.model';

/**
 * Componente para mostrar el resumen de recursos del usuario.
 * Muestra el uso actual vs disponible de CPU, RAM, disco y VMs.
 */
@Component({
  selector: 'app-resource-summary',
  templateUrl: './resource-summary.component.html',
  styleUrls: ['./resource-summary.component.css']
})
export class ResourceSummaryComponent implements OnInit, OnDestroy {

  @Input() showHeader: boolean = true;
  @Input() showUpgradeButton: boolean = true;

  private destroy$ = new Subject<void>();

  // Estados del componente
  resourceSummary: ResourceSummary | null = null;
  userSubscription: UserSubscription | null = null;
  isLoading = false;
  lastUpdated: Date = new Date();

  constructor(
    private hardwarePlanService: HardwarePlanService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadResourceSummary();
    this.loadUserSubscription();
    
    // Auto-refresh cada minuto
    setInterval(() => {
      this.loadResourceSummary();
    }, 60000);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Carga el resumen de recursos del usuario.
   */
  private loadResourceSummary(): void {
    this.isLoading = true;
    
    this.hardwarePlanService.getResourceSummary()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (summary) => {
          this.resourceSummary = summary;
          this.lastUpdated = new Date();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error cargando resumen de recursos:', error);
          this.isLoading = false;
          // No mostrar toast para este error, es silencioso
        }
      });
  }

  /**
   * Carga la suscripción actual del usuario.
   */
  private loadUserSubscription(): void {
    this.hardwarePlanService.getCurrentSubscription()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (subscription) => {
          this.userSubscription = subscription;
        },
        error: (error) => {
          console.error('Error cargando suscripción:', error);
        }
      });
  }

  /**
   * Calcula el porcentaje de uso de un recurso.
   */
  getUsagePercentage(used: number, total: number): number {
    if (total === 0) return 0;
    return Math.round((used / total) * 100);
  }

  /**
   * Obtiene la clase CSS para la barra de progreso según el porcentaje.
   */
  getProgressBarClass(percentage: number): string {
    return this.hardwarePlanService.getUsageProgressClass(percentage);
  }

  /**
   * Obtiene el color del indicador según el porcentaje de uso.
   */
  getIndicatorColor(percentage: number): string {
    if (percentage >= 90) return '#dc3545'; // Rojo
    if (percentage >= 75) return '#ffc107'; // Amarillo
    if (percentage >= 50) return '#17a2b8'; // Azul
    return '#28a745'; // Verde
  }

  /**
   * Verifica si el recurso está en estado crítico (>90% uso).
   */
  isCritical(used: number, total: number): boolean {
    return this.getUsagePercentage(used, total) >= 90;
  }

  /**
   * Verifica si el recurso está en estado de advertencia (>75% uso).
   */
  isWarning(used: number, total: number): boolean {
    const percentage = this.getUsagePercentage(used, total);
    return percentage >= 75 && percentage < 90;
  }

  /**
   * Formatea la memoria para mostrar.
   */
  formatMemory(memoryMB: number): string {
    return this.hardwarePlanService.formatMemory(memoryMB);
  }

  /**
   * Formatea bytes para mostrar.
   */
  formatBytes(bytes: number): string {
    return this.hardwarePlanService.formatBytes(bytes);
  }

  /**
   * Obtiene el mensaje de estado para un recurso.
   */
  getResourceStatus(used: number, total: number): string {
    const percentage = this.getUsagePercentage(used, total);
    const remaining = total - used;
    
    if (percentage >= 95) {
      return `¡Crítico! Solo ${remaining} disponible`;
    }
    if (percentage >= 85) {
      return `Advertencia: ${remaining} restante`;
    }
    if (percentage >= 50) {
      return `${remaining} disponible`;
    }
    return `${remaining} de ${total} disponible`;
  }

  /**
   * Verifica si se puede crear una VM con recursos específicos.
   */
  canCreateVM(cpu: number, memory: number, disk: number): void {
    this.hardwarePlanService.canCreateVM(cpu, memory, disk)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.canCreate) {
            this.toastr.success('✅ Puedes crear esta VM con tus recursos actuales');
          } else {
            this.toastr.warning(`❌ No puedes crear esta VM: ${result.reason}`);
          }
        },
        error: (error) => {
          console.error('Error verificando capacidad de VM:', error);
          this.toastr.error('Error al verificar recursos disponibles');
        }
      });
  }

  /**
   * Refresca manualmente los datos.
   */
  refreshData(): void {
    this.loadResourceSummary();
    this.toastr.info('Actualizando recursos...');
  }

  /**
   * Navega a la página de planes para actualizar.
   */
  upgradePlan(): void {
    // Implementar navegación a planes
    console.log('Navegar a planes para actualizar');
  }

  /**
   * Obtiene recomendaciones basadas en el uso actual.
   */
  getRecommendations(): string[] {
    if (!this.resourceSummary) return [];

    const recommendations: string[] = [];
    const { total, used } = this.resourceSummary;

    // Verificar CPU
    const cpuPercentage = this.getUsagePercentage(used.cpu, total.cpu);
    if (cpuPercentage > 80) {
      recommendations.push('Considera actualizar tu plan para obtener más CPU');
    }

    // Verificar memoria
    const memoryPercentage = this.getUsagePercentage(used.memory, total.memory);
    if (memoryPercentage > 80) {
      recommendations.push('Tu uso de memoria está alto, considera más RAM');
    }

    // Verificar disco
    const diskPercentage = this.getUsagePercentage(used.disk, total.disk);
    if (diskPercentage > 80) {
      recommendations.push('Necesitas más espacio de almacenamiento');
    }

    // Verificar VMs
    const vmPercentage = this.getUsagePercentage(used.currentVMs, total.maxVMs);
    if (vmPercentage > 80) {
      recommendations.push('Estás cerca del límite de VMs, considera un plan superior');
    }

    if (recommendations.length === 0) {
      recommendations.push('Tu uso de recursos está bien balanceado 👍');
    }

    return recommendations;
  }

  /**
   * Obtiene estadísticas adicionales.
   */
  getStats(): any {
    if (!this.resourceSummary) return null;

    const { total, used } = this.resourceSummary;
    
    return {
      totalUsagePercentage: Math.round(
        (this.getUsagePercentage(used.cpu, total.cpu) +
         this.getUsagePercentage(used.memory, total.memory) +
         this.getUsagePercentage(used.disk, total.disk) +
         this.getUsagePercentage(used.currentVMs, total.maxVMs)) / 4
      ),
      mostUsedResource: this.getMostUsedResource(),
      leastUsedResource: this.getLeastUsedResource()
    };
  }

  /**
   * Obtiene el recurso más utilizado.
   */
  private getMostUsedResource(): string {
    if (!this.resourceSummary) return '';

    const { total, used } = this.resourceSummary;
    const percentages = [
      { name: 'CPU', percentage: this.getUsagePercentage(used.cpu, total.cpu) },
      { name: 'Memoria', percentage: this.getUsagePercentage(used.memory, total.memory) },
      { name: 'Disco', percentage: this.getUsagePercentage(used.disk, total.disk) },
      { name: 'VMs', percentage: this.getUsagePercentage(used.currentVMs, total.maxVMs) }
    ];

    return percentages.reduce((max, current) => 
      current.percentage > max.percentage ? current : max
    ).name;
  }

  /**
   * Obtiene el recurso menos utilizado.
   */
  private getLeastUsedResource(): string {
    if (!this.resourceSummary) return '';

    const { total, used } = this.resourceSummary;
    const percentages = [
      { name: 'CPU', percentage: this.getUsagePercentage(used.cpu, total.cpu) },
      { name: 'Memoria', percentage: this.getUsagePercentage(used.memory, total.memory) },
      { name: 'Disco', percentage: this.getUsagePercentage(used.disk, total.disk) },
      { name: 'VMs', percentage: this.getUsagePercentage(used.currentVMs, total.maxVMs) }
    ];

    return percentages.reduce((min, current) => 
      current.percentage < min.percentage ? current : min
    ).name;
  }
} 