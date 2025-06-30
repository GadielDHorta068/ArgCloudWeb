package com.argcloud.vm.repository;

import com.argcloud.vm.entity.VirtualMachine;
import com.argcloud.vm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {
    
    /**
     * Encuentra todas las máquinas virtuales de un usuario específico.
     */
    List<VirtualMachine> findByUser(User user);
    
    /**
     * Encuentra todas las máquinas virtuales de un usuario por su ID.
     */
    List<VirtualMachine> findByUserId(Long userId);
    
    /**
     * Encuentra una máquina virtual por su ID externo.
     */
    Optional<VirtualMachine> findByExternalId(String externalId);
    
    /**
     * Encuentra máquinas virtuales por estado.
     */
    List<VirtualMachine> findByStatus(VirtualMachine.VMStatus status);
    
    /**
     * Encuentra máquinas virtuales por nodo.
     */
    List<VirtualMachine> findByNodeName(String nodeName);
    
    /**
     * Encuentra una máquina virtual por nombre y usuario.
     */
    Optional<VirtualMachine> findByNameAndUser(String name, User user);
    
    /**
     * Cuenta las máquinas virtuales activas de un usuario.
     */
    @Query("SELECT COUNT(vm) FROM VirtualMachine vm WHERE vm.user.id = :userId AND vm.status IN ('RUNNING', 'STOPPED')")
    long countActiveVMsByUserId(@Param("userId") Long userId);
    
    /**
     * Encuentra máquinas virtuales por usuario y estado.
     */
    List<VirtualMachine> findByUserAndStatus(User user, VirtualMachine.VMStatus status);
    
    /**
     * Verifica si existe una máquina virtual con el nombre especificado para un usuario.
     */
    boolean existsByNameAndUser(String name, User user);
} 