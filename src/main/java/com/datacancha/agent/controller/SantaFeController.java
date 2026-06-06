package com.datacancha.agent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datacancha.agent.dto.Santafe_DTOS.ClubProfileDto;
import com.datacancha.agent.service.SantaFeProfileService;

@RestController
@RequestMapping("/api/v1/santafe")
public class SantaFeController {

    private final SantaFeProfileService profileService;

    public SantaFeController(SantaFeProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ClubProfileDto> getClubProfile() {
        ClubProfileDto profile = profileService.getProfile();
        return ResponseEntity.ok(profile);
    }
}