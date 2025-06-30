import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { VirtualMachine } from '../../models/virtual-machine.model';

/**
 * Componente del panel de control del usuario.
 * Muestra información de la cuenta, estadísticas (actualmente placeholders)
 * y una sección para la gestión de máquinas virtuales.
 */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  /** El usuario actualmente autenticado. */
  currentUser: User | null = null;
  virtualMachines: VirtualMachine[] = [];
  selectedMachine: VirtualMachine | null = null;

  constructor(
    private authService: AuthService,
    private http: HttpClient
  ) { }

  /**
   * Método del ciclo de vida de Angular. Se ejecuta al inicializar el componente.
   * Obtiene la información del usuario actual y solicita los datos del dashboard al backend.
   */
  ngOnInit(): void {
    this.currentUser = this.authService.getUser();

    // Obtener mensaje de bienvenida del backend
    this.getDashboardData();
    this.loadVirtualMachines();
  }

  /**
   * Carga una lista de máquinas virtuales de prueba.
   * En una implementación real, esto vendría de una API.
   */
  loadVirtualMachines(): void {
    this.virtualMachines = [
      { 
        id: 1, 
        name: 'Servidor Web (Ubuntu)', 
        status: 'running', 
        os: 'Ubuntu 22.04', 
        cpu: 2, 
        memory: 2048, 
        disk: 50,
        ipAddress: '192.168.1.100',
        macAddress: '00:16:3e:12:34:56',
        nodeName: 'proxmox-node-1',
        createdAt: '2024-01-15T10:30:00Z',
        updatedAt: '2024-01-20T14:45:00Z',
        userName: 'admin'
      },
      { 
        id: 2, 
        name: 'Base de Datos (Debian)', 
        status: 'stopped', 
        os: 'Debian 11', 
        cpu: 4, 
        memory: 4096, 
        disk: 100,
        ipAddress: '192.168.1.101',
        macAddress: '00:16:3e:12:34:57',
        nodeName: 'proxmox-node-2',
        createdAt: '2024-01-10T08:15:00Z',
        updatedAt: '2024-01-18T16:20:00Z',
        userName: 'admin'
      },
      { 
        id: 3, 
        name: 'Entorno de tuvieja (Fedora)', 
        status: 'running', 
        os: 'Fedora 38', 
        cpu: 8, 
        memory: 16384, 
        disk: 250,
        ipAddress: '192.168.1.102',
        macAddress: '00:16:3e:12:34:58',
        nodeName: 'proxmox-node-1',
        createdAt: '2024-01-12T12:00:00Z',
        updatedAt: '2024-01-22T09:30:00Z',
        userName: 'admin'
      },
    ];
  }

  /**
   * Selecciona una máquina virtual para ver sus detalles.
   * @param machine La máquina virtual seleccionada.
   * @param event El evento del click (opcional).
   */
  selectMachine(machine: VirtualMachine, event?: Event): void {
    if (event) {
      event.preventDefault();
    }
    this.selectedMachine = machine;
  }

  /** 
   * Vuelve a la lista de máquinas virtuales. 
   * @param event El evento del click (opcional).
   */
  deselectMachine(event?: Event): void {
    if (event) {
      event.preventDefault();
    }
    this.selectedMachine = null;
  }

  /**
   * Inicia una máquina virtual (simulado).
   * @param vm La máquina virtual a iniciar.
   * @param event El evento del click (opcional).
   */
  startVM(vm: VirtualMachine, event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    console.log(`Iniciando ${vm.name}...`);
    vm.status = 'running';
  }

  /**
   * Reinicia una máquina virtual (simulado).
   * @param vm La máquina virtual a reiniciar.
   * @param event El evento del click (opcional).
   */
  restartVM(vm: VirtualMachine, event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    console.log(`Reiniciando ${vm.name}...`);
    vm.status = 'restarting';
    setTimeout(() => {
      vm.status = 'running';
      console.log(`${vm.name} reiniciada.`);
    }, 3000); // Simular tiempo de reinicio
  }

  /**
   * Detiene una máquina virtual (simulado).
   * @param vm La máquina virtual a detener.
   * @param event El evento del click (opcional).
   */
  stopVM(vm: VirtualMachine, event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    console.log(`Deteniendo ${vm.name}...`);
    vm.status = 'stopped';
  }

  /**
   * Abre la consola de la máquina virtual (simulado).
   * @param vm La máquina virtual para la que se abrirá la consola.
   * @param event El evento del click (opcional).
   */
  openConsole(vm: VirtualMachine, event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    console.log(`Abriendo consola para ${vm.name}...`);
    // En una implementación real, esto podría navegar a una nueva ruta
    // o abrir un modal con un componente de terminal (ej. ngx-terminal).
    alert(`Consola para ${vm.name} (simulado)`);
  }

  /**
   * Abre la conexión FTP para la máquina virtual (simulado).
   * @param vm La máquina virtual para la que se abrirá la conexión FTP.
   * @param event El evento del click (opcional).
   */
  openFTP(vm: VirtualMachine, event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    console.log(`Abriendo FTP para ${vm.name}...`);
    // Esto podría abrir un enlace ftp:// o mostrar detalles de conexión.
    alert(`FTP para ${vm.name} (simulado)`);
  }

  /**
   * Obtiene datos del backend para el dashboard.
   * Actualmente solo solicita un mensaje de bienvenida.
   */
  private getDashboardData(): void {
    // El interceptor agregará automáticamente el token Bearer
    this.http.get('http://localhost:8080/api/dashboard/welcome')
      .subscribe({
        next: (response: any) => {
          console.log('Dashboard data:', response);
        },
        error: (error) => {
          console.error('Error fetching dashboard data:', error);
          // El interceptor manejará automáticamente los errores 401
        }
      });
  }
} 