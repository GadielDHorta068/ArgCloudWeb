import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { 
  VirtualMachine, 
  VirtualMachineRequest, 
  VirtualMachineResponse,
  ApiResponse 
} from '../models/virtual-machine.model';

@Injectable({
  providedIn: 'root'
})
export class VirtualMachineService {
  private apiUrl = 'http://localhost:8080/api/vms';

  constructor(private http: HttpClient) {}

  /**
   * Obtiene los headers con el token de autenticación
   */
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  /**
   * Maneja errores de las peticiones HTTP
   */
  private handleError(error: any): Observable<never> {
    let errorMessage = 'Ha ocurrido un error inesperado';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Crea una nueva máquina virtual
   */
  createVirtualMachine(request: VirtualMachineRequest): Observable<VirtualMachine> {
    return this.http.post<VirtualMachineResponse>(this.apiUrl, request, {
      headers: this.getAuthHeaders()
    }).pipe(
      map(response => this.mapResponseToVirtualMachine(response)),
      catchError(this.handleError)
    );
  }

  /**
   * Obtiene todas las máquinas virtuales del usuario
   */
  getUserVirtualMachines(): Observable<VirtualMachine[]> {
    return this.http.get<VirtualMachineResponse[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    }).pipe(
      map(responses => responses.map(response => this.mapResponseToVirtualMachine(response))),
      catchError(this.handleError)
    );
  }

  /**
   * Obtiene una máquina virtual específica
   */
  getVirtualMachine(id: number): Observable<VirtualMachine> {
    return this.http.get<VirtualMachineResponse>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders()
    }).pipe(
      map(response => this.mapResponseToVirtualMachine(response)),
      catchError(this.handleError)
    );
  }

  /**
   * Inicia una máquina virtual
   */
  startVirtualMachine(id: number): Observable<VirtualMachine> {
    return this.http.post<VirtualMachineResponse>(`${this.apiUrl}/${id}/start`, {}, {
      headers: this.getAuthHeaders()
    }).pipe(
      map(response => this.mapResponseToVirtualMachine(response)),
      catchError(this.handleError)
    );
  }

  /**
   * Detiene una máquina virtual
   */
  stopVirtualMachine(id: number): Observable<VirtualMachine> {
    return this.http.post<VirtualMachineResponse>(`${this.apiUrl}/${id}/stop`, {}, {
      headers: this.getAuthHeaders()
    }).pipe(
      map(response => this.mapResponseToVirtualMachine(response)),
      catchError(this.handleError)
    );
  }

  /**
   * Reinicia una máquina virtual
   */
  restartVirtualMachine(id: number): Observable<VirtualMachine> {
    return this.http.post<VirtualMachineResponse>(`${this.apiUrl}/${id}/restart`, {}, {
      headers: this.getAuthHeaders()
    }).pipe(
      map(response => this.mapResponseToVirtualMachine(response)),
      catchError(this.handleError)
    );
  }

  /**
   * Elimina una máquina virtual
   */
  deleteVirtualMachine(id: number): Observable<{message: string}> {
    return this.http.delete<{message: string}>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Sincroniza el estado de una máquina virtual
   */
  syncVirtualMachine(id: number): Observable<VirtualMachine> {
    return this.http.post<VirtualMachineResponse>(`${this.apiUrl}/${id}/sync`, {}, {
      headers: this.getAuthHeaders()
    }).pipe(
      map(response => this.mapResponseToVirtualMachine(response)),
      catchError(this.handleError)
    );
  }

  /**
   * Obtiene los nodos disponibles
   */
  getAvailableNodes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/nodes`, {
      headers: this.getAuthHeaders()
    }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Mapea la respuesta del servidor al modelo de VirtualMachine
   */
  private mapResponseToVirtualMachine(response: VirtualMachineResponse): VirtualMachine {
    return {
      id: response.id,
      name: response.name,
      status: response.status as VirtualMachine['status'],
      os: response.os,
      cpu: response.cpu,
      memory: response.memory,
      disk: response.disk,
      ipAddress: response.ipAddress,
      macAddress: response.macAddress,
      nodeName: response.nodeName,
      createdAt: response.createdAt,
      updatedAt: response.updatedAt,
      userName: response.userName
    };
  }

  /**
   * Obtiene el icono CSS para el estado de la VM
   */
  getStatusIcon(status: string): string {
    switch (status) {
      case 'running':
        return 'fas fa-play-circle text-success';
      case 'stopped':
        return 'fas fa-stop-circle text-danger';
      case 'restarting':
        return 'fas fa-sync-alt fa-spin text-warning';
      case 'creating':
        return 'fas fa-cog fa-spin text-info';
      case 'deleting':
        return 'fas fa-trash-alt text-danger';
      case 'error':
        return 'fas fa-exclamation-triangle text-danger';
      default:
        return 'fas fa-question-circle text-muted';
    }
  }

  /**
   * Obtiene el texto legible para el estado de la VM
   */
  getStatusText(status: string): string {
    switch (status) {
      case 'running':
        return 'En ejecución';
      case 'stopped':
        return 'Detenida';
      case 'restarting':
        return 'Reiniciando';
      case 'creating':
        return 'Creando';
      case 'deleting':
        return 'Eliminando';
      case 'error':
        return 'Error';
      default:
        return 'Desconocido';
    }
  }

  /**
   * Obtiene la clase CSS para el estado de la VM
   */
  getStatusClass(status: string): string {
    switch (status) {
      case 'running':
        return 'badge badge-success';
      case 'stopped':
        return 'badge badge-secondary';
      case 'restarting':
        return 'badge badge-warning';
      case 'creating':
        return 'badge badge-info';
      case 'deleting':
        return 'badge badge-danger';
      case 'error':
        return 'badge badge-danger';
      default:
        return 'badge badge-light';
    }
  }
} 