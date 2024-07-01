package org.samsamohoh.searchengineproject.controller;

import org.samsamohoh.searchengineproject.aggregate.AWSBlog;
import org.samsamohoh.searchengineproject.service.SemiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/aws")
public class SemiController {

    private final SemiService service;

    @Autowired
    public SemiController(SemiService service) {
        this.service = service;
    }

    @GetMapping("/{words}")
    public List<AWSBlog> findTest(@PathVariable("words") String words) {

        return service.findTest(words);
    }
}
