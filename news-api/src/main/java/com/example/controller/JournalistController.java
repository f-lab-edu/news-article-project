package com.example.controller;

import com.example.dto.JournalistReputationDTO;
import com.example.service.JournalistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/journalist")
@RestController
@RequiredArgsConstructor
public class JournalistController {

    private final JournalistService journalistService;

    @GetMapping("/{journalistId}/reputation")
    public JournalistReputationDTO getJournalistReputationScore(@PathVariable long journalistId) {
        return journalistService.getJournalistReputation(journalistId);
    }

    @PutMapping("/{journalistId}/update-reputation")
    public ResponseEntity<String> updateReputation(@PathVariable long journalistId) {
        journalistService.updateJournalistReputation(journalistId);
        return ResponseEntity.ok("Reputation updated");
    }
}
