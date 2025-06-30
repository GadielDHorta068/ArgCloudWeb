import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, interval } from 'rxjs';
import { takeUntil, switchMap, startWith } from 'rxjs/operators';
import { VirtualMachine, VirtualMachineRequest } from '../../models/virtual-machine.model';
import { VirtualMachineService } from '../../services/virtual-machine.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-virtual-machines',
  templateUrl: './virtual-machines.component.html',
  styleUrls: ['./virtual-machines.component.css']
})
export class VirtualMachinesComponent implements OnInit, OnDestroy {
  virtualMachines: VirtualMachine[] = [];
  availableNodes: string[] = [];
  loading = false;
  showCreateForm = false;
  selectedVm: VirtualMachine | null = null;
  
  // Formulario de creación
  newVmForm: VirtualMachineRequest = {
    name: '',
    os: 'ubuntu-22.04',
    cpu: 2,
    memory: 2048,
    disk: 20,
    nodeName: ''
  };

  // Estados de carga para acciones individuales
  vmActions: { [key: number]: { [action: string]: boolean } } = {};

  private destroy$ = new Subject<void>();

  // Opciones de configuración
  osOptions = [
    { value: 'ubuntu-22.04', label: 'Ubuntu 22.04 LTS' },
    { value: 'ubuntu-20.04', label: 'Ubuntu 20.04 LTS' },
    { value: 'debian-11', label: 'Debian 11' },
    { value: 'debian-12', label: 'Debian 12' },
    { value: 'centos-8', label: 'CentOS 8' },
    { value: 'rhel-8', label: 'Red Hat 8' },
    { value: 'windows-server-2019', label: 'Windows Server 2019' },
    { value: 'windows-server-2022', label: 'Windows Server 2022' }
  ];

  cpuOptions = [1, 2, 4, 8, 16];
  memoryOptions = [1024, 2048, 4096, 8192, 16384, 32768];
  diskOptions = [10, 20, 40, 80, 160, 320];

  constructor(
    private vmService: VirtualMachineService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
    this.setupAutoRefresh();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Carga los datos iniciales
   */
  private loadData(): void {
    this.loading = true;
    Promise.all([
      this.loadVirtualMachines(),
      this.loadAvailableNodes()
    ]).finally(() => {
      this.loading = false;
    });
  }

  /**
   * Configura la actualización automática cada 30 segundos
   */
  private setupAutoRefresh(): void {
    interval(30000)
      .pipe(
        startWith(0),
        switchMap(() => this.vmService.getUserVirtualMachines()),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (vms) => {
          this.virtualMachines = vms;
        },
        error: (error) => {
          console.error('Error al actualizar VMs:', error);
        }
      });
  }

  /**
   * Carga las máquinas virtuales del usuario
   */
  private async loadVirtualMachines(): Promise<void> {
    try {
      this.virtualMachines = await this.vmService.getUserVirtualMachines().toPromise() || [];
    } catch (error) {
      console.error('Error al cargar máquinas virtuales:', error);
      this.toastr.error('Error al cargar las máquinas virtuales', 'Error');
    }
  }

  /**
   * Carga los nodos disponibles
   */
  private async loadAvailableNodes(): Promise<void> {
    try {
      this.availableNodes = await this.vmService.getAvailableNodes().toPromise() || [];
      if (this.availableNodes.length > 0) {
        this.newVmForm.nodeName = this.availableNodes[0];
      }
    } catch (error) {
      console.error('Error al cargar nodos disponibles:', error);
      this.toastr.warning('No se pudieron cargar los nodos disponibles', 'Advertencia');
    }
  }

  /**
   * Muestra/oculta el formulario de creación
   */
  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.resetForm();
    }
  }

  /**
   * Reinicia el formulario de creación
   */
  private resetForm(): void {
    this.newVmForm = {
      name: '',
      os: 'ubuntu-22.04',
      cpu: 2,
      memory: 2048,
      disk: 20,
      nodeName: this.availableNodes.length > 0 ? this.availableNodes[0] : ''
    };
  }

  /**
   * Crea una nueva máquina virtual
   */
  async createVirtualMachine(): Promise<void> {
    if (!this.isValidForm()) {
      this.toastr.error('Por favor complete todos los campos requeridos', 'Error');
      return;
    }

    this.loading = true;
    try {
      const newVm = await this.vmService.createVirtualMachine(this.newVmForm).toPromise();
      if (newVm) {
        this.virtualMachines.push(newVm);
        this.toastr.success('Máquina virtual creada exitosamente', 'Éxito');
        this.toggleCreateForm();
      }
    } catch (error: any) {
      console.error('Error al crear máquina virtual:', error);
      this.toastr.error(error.message || 'Error al crear la máquina virtual', 'Error');
    } finally {
      this.loading = false;
    }
  }

  /**
   * Valida el formulario de creación
   */
  private isValidForm(): boolean {
    return !!(this.newVmForm.name && 
              this.newVmForm.os && 
              this.newVmForm.nodeName &&
              this.newVmForm.cpu > 0 &&
              this.newVmForm.memory > 0 &&
              this.newVmForm.disk > 0);
  }

  /**
   * Inicia una máquina virtual
   */
  async startVm(vm: VirtualMachine): Promise<void> {
    if (vm.status === 'running') return;
    
    this.setVmActionLoading(vm.id, 'start', true);
    try {
      const updatedVm = await this.vmService.startVirtualMachine(vm.id).toPromise();
      if (updatedVm) {
        this.updateVmInList(updatedVm);
        this.toastr.success(`Máquina virtual "${vm.name}" iniciada`, 'Éxito');
      }
    } catch (error: any) {
      console.error('Error al iniciar VM:', error);
      this.toastr.error(error.message || 'Error al iniciar la máquina virtual', 'Error');
    } finally {
      this.setVmActionLoading(vm.id, 'start', false);
    }
  }

  /**
   * Detiene una máquina virtual
   */
  async stopVm(vm: VirtualMachine): Promise<void> {
    if (vm.status === 'stopped') return;
    
    this.setVmActionLoading(vm.id, 'stop', true);
    try {
      const updatedVm = await this.vmService.stopVirtualMachine(vm.id).toPromise();
      if (updatedVm) {
        this.updateVmInList(updatedVm);
        this.toastr.success(`Máquina virtual "${vm.name}" detenida`, 'Éxito');
      }
    } catch (error: any) {
      console.error('Error al detener VM:', error);
      this.toastr.error(error.message || 'Error al detener la máquina virtual', 'Error');
    } finally {
      this.setVmActionLoading(vm.id, 'stop', false);
    }
  }

  /**
   * Reinicia una máquina virtual
   */
  async restartVm(vm: VirtualMachine): Promise<void> {
    if (vm.status !== 'running') return;
    
    this.setVmActionLoading(vm.id, 'restart', true);
    try {
      const updatedVm = await this.vmService.restartVirtualMachine(vm.id).toPromise();
      if (updatedVm) {
        this.updateVmInList(updatedVm);
        this.toastr.success(`Máquina virtual "${vm.name}" reiniciada`, 'Éxito');
      }
    } catch (error: any) {
      console.error('Error al reiniciar VM:', error);
      this.toastr.error(error.message || 'Error al reiniciar la máquina virtual', 'Error');
    } finally {
      this.setVmActionLoading(vm.id, 'restart', false);
    }
  }

  /**
   * Elimina una máquina virtual
   */
  async deleteVm(vm: VirtualMachine): Promise<void> {
    if (!confirm(`¿Está seguro de que desea eliminar la máquina virtual "${vm.name}"? Esta acción no se puede deshacer.`)) {
      return;
    }

    this.setVmActionLoading(vm.id, 'delete', true);
    try {
      await this.vmService.deleteVirtualMachine(vm.id).toPromise();
      this.virtualMachines = this.virtualMachines.filter(v => v.id !== vm.id);
      this.toastr.success(`Máquina virtual "${vm.name}" eliminada`, 'Éxito');
    } catch (error: any) {
      console.error('Error al eliminar VM:', error);
      this.toastr.error(error.message || 'Error al eliminar la máquina virtual', 'Error');
    } finally {
      this.setVmActionLoading(vm.id, 'delete', false);
    }
  }

  /**
   * Sincroniza el estado de una máquina virtual
   */
  async syncVm(vm: VirtualMachine): Promise<void> {
    this.setVmActionLoading(vm.id, 'sync', true);
    try {
      const updatedVm = await this.vmService.syncVirtualMachine(vm.id).toPromise();
      if (updatedVm) {
        this.updateVmInList(updatedVm);
        this.toastr.success(`Estado de "${vm.name}" sincronizado`, 'Éxito');
      }
    } catch (error: any) {
      console.error('Error al sincronizar VM:', error);
      this.toastr.error(error.message || 'Error al sincronizar la máquina virtual', 'Error');
    } finally {
      this.setVmActionLoading(vm.id, 'sync', false);
    }
  }

  /**
   * Actualiza una VM en la lista
   */
  private updateVmInList(updatedVm: VirtualMachine): void {
    const index = this.virtualMachines.findIndex(vm => vm.id === updatedVm.id);
    if (index !== -1) {
      this.virtualMachines[index] = updatedVm;
    }
  }

  /**
   * Establece el estado de carga para una acción específica de una VM
   */
  private setVmActionLoading(vmId: number, action: string, loading: boolean): void {
    if (!this.vmActions[vmId]) {
      this.vmActions[vmId] = {};
    }
    this.vmActions[vmId][action] = loading;
  }

  /**
   * Verifica si una acción específica está cargando
   */
  isActionLoading(vmId: number, action: string): boolean {
    return this.vmActions[vmId]?.[action] || false;
  }

  /**
   * Obtiene el icono para el estado de la VM
   */
  getStatusIcon(status: string): string {
    return this.vmService.getStatusIcon(status);
  }

  /**
   * Obtiene el texto para el estado de la VM
   */
  getStatusText(status: string): string {
    return this.vmService.getStatusText(status);
  }

  /**
   * Obtiene la clase CSS para el estado de la VM
   */
  getStatusClass(status: string): string {
    return this.vmService.getStatusClass(status);
  }

  /**
   * Verifica si una VM puede ser iniciada
   */
  canStart(vm: VirtualMachine): boolean {
    return vm.status === 'stopped' && !this.isActionLoading(vm.id, 'start');
  }

  /**
   * Verifica si una VM puede ser detenida
   */
  canStop(vm: VirtualMachine): boolean {
    return vm.status === 'running' && !this.isActionLoading(vm.id, 'stop');
  }

  /**
   * Verifica si una VM puede ser reiniciada
   */
  canRestart(vm: VirtualMachine): boolean {
    return vm.status === 'running' && !this.isActionLoading(vm.id, 'restart');
  }

  /**
   * Verifica si una VM puede ser eliminada
   */
  canDelete(vm: VirtualMachine): boolean {
    return vm.status === 'stopped' && !this.isActionLoading(vm.id, 'delete');
  }

  /**
   * Actualiza manualmente la lista de VMs
   */
  refreshVms(): void {
    this.loadVirtualMachines();
  }
} 