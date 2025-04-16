package com.cook.cookapp.user.controller;

import com.cook.cookapp.user.service.ComplimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compliment")
@RequiredArgsConstructor
public class ComplimentController {
    private final ComplimentService complimentService;
}
