package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CriarProjetoRequestDTO {

    // Colocado contra medidas para garantir que código não
    // irá quebrar

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @NotBlank(message = "Orientador é obrigatório")
    @Size(max = 100, message = "Orientador deve ter no máximo 100 caracteres")
    private String orientador;

    @NotBlank(message = "Área é obrigatória")
    @Size(max = 50, message = "Área deve ter no máximo 50 caracteres")
    private String area;

    @NotNull(message = "Data prevista de término é obrigatória")
    private LocalDate dataPrevistaTermino;

    // Construtor padrão
    public CriarProjetoRequestDTO() {}

    // Getters e Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public LocalDate getDataPrevistaTermino() {
        return dataPrevistaTermino;
    }

    public void setDataPrevistaTermino(LocalDate dataPrevistaTermino) {
        this.dataPrevistaTermino = dataPrevistaTermino;
    }
}