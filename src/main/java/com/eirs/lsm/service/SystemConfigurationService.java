package com.eirs.lsm.service;

import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface SystemConfigurationService {

    String operatorAll = "ALL";

    List<String> getOperators();

    Integer getNoOfEirs(String operator);

    public String findByKey(String key) throws RuntimeException;

    public Integer findByKey(String key, int defaultValue);

    public String findByKey(String key, String defaultValue);

    public Float findByKey(String key, float defaultValue);


}
