package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.entity.Checklist;
import com.rutusoft.flowable.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    // ================================
    //  GET FILTERED CHECKLIST
    // ================================
    @GetMapping
    public ResponseEntity<?> getChecklist(
            @RequestParam String productName,
            @RequestParam(required = false) String productType,
            @RequestParam String stage
    ) {
        return ResponseEntity.ok(
                checklistService.getChecklist(productName, productType, stage)
        );
    }


    // ================================
    // ADD CHECKLIST ITEM
    // ================================
    @PostMapping
    public ResponseEntity<?> addChecklist(@RequestBody Checklist checklist) {
        return ResponseEntity.ok(
                checklistService.addChecklist(checklist)
        );
    }

    // ================================
    // GET ALL CHECKLIST
    // ================================
    @GetMapping("/all")
    public ResponseEntity<?> getAllChecklists() {
        return ResponseEntity.ok(
                checklistService.getAllChecklists()
        );
    }

    // ================================
    // UPDATE CHECKLIST
    // ================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChecklist(
            @PathVariable Long id,
            @RequestBody Checklist checklist
    ) {
        return ResponseEntity.ok(
                checklistService.updateChecklist(id, checklist)
        );
    }

    // ================================
    // DELETE CHECKLIST
    // ================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChecklist(@PathVariable Long id) {
        checklistService.deleteChecklist(id);
        return ResponseEntity.ok("Checklist deleted successfully");
    }
}