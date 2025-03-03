package br.com.alura.screenmatch_spring.dto;

import br.com.alura.screenmatch_spring.model.Categoria;

public record SerieDTO(
Long id,
String titulo,
Integer totalTemporadas,
Double avaliacao,
Categoria genero,
String atores,
String poster,
String sinopse) {
}
