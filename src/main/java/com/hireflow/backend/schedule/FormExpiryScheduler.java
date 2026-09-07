package com.hireflow.backend.schedule;

import com.hireflow.backend.service.FormService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FormExpiryScheduler {

    private final FormService formService;

    public FormExpiryScheduler(FormService formService) {
        this.formService = formService;
    }

    @Scheduled(fixedRate = 60_000)
    public void deactivateExpiredForms() {
        formService.deactivateExpiredForms();
    }
}
