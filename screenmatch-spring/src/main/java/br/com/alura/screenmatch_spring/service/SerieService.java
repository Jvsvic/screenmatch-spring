package br.com.alura.screenmatch_spring.service;

import br.com.alura.screenmatch_spring.dto.EpisodioDTO;
import br.com.alura.screenmatch_spring.dto.SerieDTO;
import br.com.alura.screenmatch_spring.model.Categoria;
import br.com.alura.screenmatch_spring.model.Serie;
import br.com.alura.screenmatch_spring.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> todasAsSeries(){
        return converteSeries(serieRepository.findAll());
    }

    public List<SerieDTO> obterSeriesTop5() {
        return converteSeries(serieRepository.findTop5ByOrderByAvaliacaoDesc());


    }
    private List<SerieDTO> converteSeries(List<Serie> series){
        return series
                .stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());

    }

    public List<SerieDTO> obterLancamentos() {
        return converteSeries(serieRepository.encontrarEpisodiosMaisRecentes());
    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
        return serieRepository.obterEpisodiosPorTemporada(id, numero)
                .stream().map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterCategorias(String genero) {
        Categoria categoria = Categoria.fromPortugues(genero);
        return converteSeries(serieRepository.findByGenero(categoria));

    }
}
