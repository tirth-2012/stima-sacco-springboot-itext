package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.CollateralRequest;
import com.rutusoft.flowable.entity.Collateral;

import java.util.List;

public interface CollateralService {

    Collateral createCollateral(String cifNumber, CollateralRequest request);

    Collateral updateCollateral(String cifNumber, Long collateralId, CollateralRequest request);

    void deleteCollateral(Long id);

    Collateral getCollateralById(Long id);

    List<Collateral> getAllByCif(String cifNumber);

    List<Collateral> getByProcessInstanceId(String processInstanceId);


}