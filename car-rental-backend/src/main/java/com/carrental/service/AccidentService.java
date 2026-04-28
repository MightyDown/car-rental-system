package com.carrental.service;

import com.carrental.common.PageResult;
import com.carrental.dto.*;

public interface AccidentService {

    PageResult<AccidentVO> listAccidents(Integer page, Integer size, String status);

    AccidentVO getAccidentById(Long id);

    AccidentVO createAccident(AccidentSaveDTO dto);

    void processAccident(Long id, AccidentProcessDTO dto);

    void completeAccident(Long id);
}
