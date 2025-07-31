package am.armeniabank.auditservice.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface AuditExportService {
    @Scheduled(cron = "0 0 1 * * ?")
        // Ежедневно в 01:00 утра
    void exportAndClearAuditEvents();
}
