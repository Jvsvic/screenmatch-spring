package br.com.alura.screenmatch_spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SerieController {

    @GetMapping("/series")
    public String obterSeries(){
        return "Aqui vão ser listada as séries";

    }
}
