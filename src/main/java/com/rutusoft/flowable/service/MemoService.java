package com.rutusoft.flowable.service;

public interface MemoService {

    byte[] generateLegalMemo(String processInstanceId) throws Exception;
    byte[] generateRcaMemo(String processInstanceId) throws Exception;
}