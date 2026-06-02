package com.rutusoft.flowable.service;

public interface OfferLetterService {
    byte[] generateOfferLetter(String processInstanceId) throws Exception;
}
