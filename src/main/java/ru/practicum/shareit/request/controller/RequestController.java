package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    @PostMapping
    public void createRequest(){

    }

    @GetMapping
    public void getRequests(){

    }

    @GetMapping
    public void getAllRequests(){

    }

    @GetMapping
    public void getRequestById(){

    }


}
