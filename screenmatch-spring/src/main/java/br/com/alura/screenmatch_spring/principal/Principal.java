package br.com.alura.screenmatch_spring.principal;

import br.com.alura.screenmatch_spring.model.*;
import br.com.alura.screenmatch_spring.repository.SerieRepository;
import br.com.alura.screenmatch_spring.service.ConsumoApi;
import br.com.alura.screenmatch_spring.service.ConverteDados;
import static java.lang.System.out;
import static java.lang.System.out;


import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=6585022c";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository serieRepository;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;
    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries pelo nome do ator
                    6 - Top 5 Séries
                    7 - Buscar séries por categoria
                    8 - Buscar séries por temporada e avaliação 
                    9 - Buscar episódio por trecho
                    10 - Top 5 Episódios
                    0 - Sair                                 
                    """;

            out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtroSeries();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodioPorData();
                    break;
                case 0:
                    out.println("Saindo...");
                    break;
                default:
                    out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        serieRepository.save(serie);
        out.println("Série adicionada a lista com sucesso!");
    }

    private DadosSerie getDadosSerie() {
        out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + APIKEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        List<DadosTemporada> temporadas = new ArrayList<>();

        Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie.toLowerCase());
//        series.stream()
//                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
//                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + APIKEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);  // Corrigido com ponto e vírgula
        } else {
            out.println("Série não existe no banco de dados.");
        }
    }

    private void listarSeriesBuscadas() {
        series = serieRepository.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(out::println);
    }

    private void buscarSeriePorTitulo() {
        out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = serieRepository.findByTituloContainingIgnoreCase(nomeSerie.toLowerCase());
        if (serieBusca.isPresent()) {
            out.println("Dados da série: " + serieBusca.get());
        } else {
            out.println("Não encontrada.");
        }
    }

    private void buscarSeriesPorAtor() {
        out.println("Escreva o nome do ator: ");
        var nomeAtor = leitura.nextLine();
        List<Serie> seriesEncontradas = serieRepository.findByAtoresContainingIgnoreCase(nomeAtor);
        out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s -> out.println(s.getTitulo() + "\nAvaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s -> out.println(s.getTitulo() + "\nAvaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        out.println("Deseja buscar série de que categoria?");
        var nomeCategoria = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> seriesPorCategoria = serieRepository.findByGenero(categoria);
        out.println("Séries da categoria: " + nomeCategoria);
        seriesPorCategoria.forEach(System.out::println);
    }
    private void filtroSeries() {
        // Solicita o número máximo de temporadas e a avaliação mínima ao usuário
        out.println("Deseja buscar série de até quantas temporadas? ");
        int totalTemporadas = leitura.nextInt();  // Lê o número de temporadas
        out.println("E até qual avaliação? ");
        double avaliacao = leitura.nextDouble();  // Lê a avaliação mínima
        leitura.nextLine();  // Limpar o buffer do scanner

        // Consultando o repositório com base nos parâmetros fornecidos
        List<Serie> filtradas = serieRepository.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);

        // Exibe as séries filtradas
        out.println("Séries filtradas: ");
        filtradas.forEach(s -> out.println(s.getTitulo() + "\nAvaliação: " + s.getAvaliacao() + " \nTemporadas: " + s.getTotalTemporadas()));
    }
    private void buscarEpisodioPorTrecho(){
        out.println("Digite qual trecho você deseja buscar: ");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = serieRepository.episodiosPorTrecho(trechoEpisodio);
        if (episodiosEncontrados.isEmpty()) {
            out.println("Nenhum episódio encontrado com esse trecho.");
        } else {
            episodiosEncontrados.forEach(System.out::println);
        }
        episodiosEncontrados.forEach(System.out::println);
    }
    private void topEpisodiosPorSerie() {
       buscarSeriePorTitulo();
       if (serieBusca.isPresent()) {
           Serie serie = serieBusca.get();
           List<Episodio> topEpisodios = serieRepository.topEpisodiosPorSerie(serie);
           topEpisodios.forEach(e -> {
               System.out.println("Série: " + e.getSerie().getTitulo() +
                       " Temporada: " + e.getTemporada() +
                       " - Episódio: " + e.getNumeroEpisodio() +
                       " - " + e.getTitulo() +
                       " - Avaliação: " + e.getAvaliacao());
           });
       }
    }
    private void buscarEpisodioPorData(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            out.println("Digite o ano limite de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosAno = serieRepository.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }

    }
}