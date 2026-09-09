package com.hireflow.backend.service;

import com.hireflow.backend.dto.GeneralParameterResponse;

/** GNL_PARM okuma. */
public interface GeneralParameterService {

    GeneralParameterResponse getByShrtCode(String shrtCode);
}
