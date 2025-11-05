package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.StatusProjeto;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Utilizar para listagens pois trará infos menores e mais precisas

public class ProjetoResumoResponseDTO {

    private Long id;
    private String titulo;
    private String orientador;
    private String area;
    private StatusProjeto status;
    private LocalDate dataPrevistaTermino;
    private LocalDateTime dataCriacao;

    // Construtor padrão
    public ProjetoResumoResponseDTO() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getOrientador() {
        return orientador;
    }

    public void setOrientador(String orientador) {
        this.orientador = orientador;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public LocalDate getDataPrevistaTermino() {
        return dataPrevistaTermino;
    }

    public void setDataPrevistaTermino(LocalDate dataPrevistaTermino) {
        this.dataPrevistaTermino = dataPrevistaTermino;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}