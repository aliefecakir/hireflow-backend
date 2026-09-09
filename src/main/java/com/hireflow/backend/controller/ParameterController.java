package com.hireflow.backend.controller;

import com.hireflow.backend.dto.GeneralParameterResponse;
import com.hireflow.backend.service.GeneralParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public: GNL_PARM kaydı (portal seçimi vb.). */
@RestController
@RequestMapping("/api/parameters")
public class ParameterController {

    private final GeneralParameterService generalParameterService;

    public ParameterController(GeneralParameterService generalParameterService) {
        this.generalParameterService = generalParameterService;
    }

    @GetMapping("/{shrtCode}")
    public ResponseEntity<GeneralParameterResponse> getByShrtCode(
            @PathVariable("shrtCode") String shrtCode
    ) {
        return ResponseEntity.ok(generalParameterService.getByShrtCode(shrtCode));
    }
}
