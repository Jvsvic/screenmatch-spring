package br.com.alura.screenmatch_spring.controller;

import br.com.alura.screenmatch_spring.dto.SerieDTO;
import br.com.alura.screenmatch_spring.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService servico;


    @GetMapping
    public List<SerieDTO> obterSeries(){
    return servico.todasAsSeries();

    }

    @GetMapping("/top5")
    public List<SerieDTO> obterSeriesTop5(){
        return servico.obterSeriesTop5();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return servico.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id){
        return servico.obterPorId(id);
    }
}
