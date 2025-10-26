package com.technorth.fluxivamed.core.dashboard;

import com.technorth.fluxivamed.core.dashboard.dto.AdminStatsResponseDTO;
import com.technorth.fluxivamed.core.dashboard.dto.MedicoStatsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Para controle de acesso
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/dashboard/stats")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN', 'ESCALISTA')") 
    public ResponseEntity<AdminStatsResponseDTO> getAdminDashboardStats() {
        AdminStatsResponseDTO stats = dashboardService.getAdminStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/medico")
    @PreAuthorize("hasRole('MEDICO')") // Protege o endpoint
    public ResponseEntity<MedicoStatsResponseDTO> getMedicoDashboardStats(Principal principal) {
        // Principal contém informações do usuário autenticado. Geralmente o nome de usuário (email)
        // Se precisar do ID do médico, você precisaria de um serviço para buscar o médico pelo email.
        MedicoStatsResponseDTO stats = dashboardService.getMedicoStats(principal);
        return ResponseEntity.ok(stats);
    }
}