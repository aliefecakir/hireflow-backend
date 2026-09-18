package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.GeneralParameterResponse;
import com.hireflow.backend.entity.GeneralParameter;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.GeneralParameterRepository;
import com.hireflow.backend.service.GeneralParameterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class GeneralParameterServiceImpl implements GeneralParameterService {

    private final GeneralParameterRepository generalParameterRepository;

    public GeneralParameterServiceImpl(GeneralParameterRepository generalParameterRepository) {
        this.generalParameterRepository = generalParameterRepository;
    }

    @Override
    public GeneralParameterResponse getByShrtCode(String shrtCode) {
        if (shrtCode == null || shrtCode.isBlank()) {
            throw new BadRequestException("Parametre kodu zorunludur.");
        }

        GeneralParameter parameter = generalParameterRepository.findByShrtCode(shrtCode.trim())
                .orElseThrow(() -> new NoSuchElementException("Parametre bulunamadı."));

        return new GeneralParameterResponse(
                parameter.getGnlParmId(),
                parameter.getName(),
                parameter.getShrtCode(),
                parameter.getVal(),
                parameter.getIsActv()
        );
    }
}
