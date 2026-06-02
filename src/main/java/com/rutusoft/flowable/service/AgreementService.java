package com.rutusoft.flowable.service;

public interface AgreementService {

    byte[] generateFacilityAgreement(String processInstanceId) throws Exception;
    byte[] generateWakalaAgreement(String processInstanceId) throws Exception;
}