package am.armeniabank.auditservicesrc.service.impl;

import am.armeniabank.auditservicesrc.entity.AuditUser;
import am.armeniabank.auditservicesrc.repository.AuditEventRepository;
import am.armeniabank.auditservicesrc.service.AuditExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditExportServiceImpl implements AuditExportService {

    private final AuditEventRepository auditRepository;
    private final ObjectMapper objectMapper;

    @Value("${archive.audit}")
    private String archive;

    @Scheduled(cron = "0 1 0 * * ?")
    @Override
    public void exportAndClearAuditEvents() {
        List<AuditUser> allEvents = auditRepository.findAll();

        if (!allEvents.isEmpty()) {
            String filename = archive + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".json";
            Path filePath = Path.of(filename);

            try {

                Files.createDirectories(filePath.getParent());
                objectMapper.writeValue(new File(filename), allEvents);
                auditRepository.deleteAll();

                log.info("Exported {} events to file {} and cleared the database.", allEvents.size(), filename);

            } catch (IOException e) {
                log.error("Error exporting to file {}: {}", filename, e.getMessage(), e);
            }
        } else {
            log.info("There is no data to export or clean as of date {}.", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")));
        }
    }
}