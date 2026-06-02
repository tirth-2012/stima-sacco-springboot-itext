package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ApplicationDraftDto;
import com.rutusoft.flowable.service.ApplicationDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draft")
public class ApplicationDraftController {

    @Autowired
    private ApplicationDraftService service;

    @PostMapping("/save")
    public ResponseEntity<?> saveDraft(@RequestBody ApplicationDraftDto dto) {
        return ResponseEntity.ok(service.saveDraft(dto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDraft(@PathVariable String userId) {
        return ResponseEntity.ok(service.getDraftByUser(userId));
    }

    @DeleteMapping("/{draftId}")
    public ResponseEntity<?> deleteDraft(@PathVariable String draftId) {
        service.deleteDraft(draftId);
        return ResponseEntity.ok("Draft deleted");
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllDrafts(@PathVariable String userId) {
        return ResponseEntity.ok(service.getAllDrafts(userId));
    }
}