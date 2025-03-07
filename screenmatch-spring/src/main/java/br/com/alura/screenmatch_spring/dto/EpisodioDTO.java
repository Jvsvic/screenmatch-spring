package br.com.alura.screenmatch_spring.dto;

public record EpisodioDTO(
        Integer temporada,
        Integer numeroEpisodio,
        String titulo
) {
}
