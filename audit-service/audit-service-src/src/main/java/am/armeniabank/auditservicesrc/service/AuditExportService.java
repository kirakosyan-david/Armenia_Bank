package am.armeniabank.auditservicesrc.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface AuditExportService {

    @Scheduled(cron = "0 0 1 * * ?")
    void exportAndClearAuditEvents();
}
