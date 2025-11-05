package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.StatusProjeto;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProjetoResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private String orientador;
    private String area;
    private StatusProjeto status;
    private LocalDate dataInicio;
    private LocalDate dataPrevistaTermino;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long usuarioCriadorId;
    private String usuarioCriadorNome;

    // Construtor padrão
    public ProjetoResponseDTO() {}

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

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
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

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getUsuarioCriadorId() {
        return usuarioCriadorId;
    }

    public void setUsuarioCriadorId(Long usuarioCriadorId) {
        this.usuarioCriadorId = usuarioCriadorId;
    }

    public String getUsuarioCriadorNome() {
        return usuarioCriadorNome;
    }

    public void setUsuarioCriadorNome(String usuarioCriadorNome) {
        this.usuarioCriadorNome = usuarioCriadorNome;
    }
}