package br.com.alura.screenmatch_spring.repository;

import br.com.alura.screenmatch_spring.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long> {

}
