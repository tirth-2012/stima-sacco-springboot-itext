package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.entity.ProcessInstanceSequence;
import com.rutusoft.flowable.repository.ProcessInstanceSequenceRepository;
import com.rutusoft.flowable.service.ProcessInstanceSequenceService;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceSequenceServiceImpl implements ProcessInstanceSequenceService {

    private final ProcessInstanceSequenceRepository sequenceRepository;

    public ProcessInstanceSequenceServiceImpl(ProcessInstanceSequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    @Override
    public Long getNextValue() {
        ProcessInstanceSequence entity = new ProcessInstanceSequence();
        entity = sequenceRepository.save(entity);
        return entity.getId();
    }
}
