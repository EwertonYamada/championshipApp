package com.campeonato.campeonato.classificationTable.controllers;

import com.campeonato.campeonato.classificationTable.services.ClassificationTableService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classificacao")
public class ClassificationTableController {
    final ClassificationTableService classificationTableService;

    public ClassificationTableController(ClassificationTableService classificationTableService) {
        this.classificationTableService = classificationTableService;
    }
}
