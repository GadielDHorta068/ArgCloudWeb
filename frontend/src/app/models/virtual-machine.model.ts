export interface VirtualMachine {
  id: number;
  name: string;
  status: 'running' | 'stopped' | 'restarting' | 'creating' | 'deleting' | 'error';
  os: string;
  cpu: number;
  memory: number; // in MB
  disk: number; // in GB
  ipAddress?: string;
  macAddress?: string;
  nodeName?: string;
  createdAt: string;
  updatedAt?: string;
  userName?: string;
}

export interface VirtualMachineRequest {
  name: string;
  os: string;
  cpu: number;
  memory: number; // in MB
  disk: number; // in GB
  nodeName?: string;
}

export interface VirtualMachineResponse {
  id: number;
  name: string;
  status: string;
  os: string;
  cpu: number;
  memory: number;
  disk: number;
  ipAddress?: string;
  macAddress?: string;
  nodeName?: string;
  createdAt: string;
  updatedAt?: string;
  userName?: string;
}

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  success: boolean;
} 