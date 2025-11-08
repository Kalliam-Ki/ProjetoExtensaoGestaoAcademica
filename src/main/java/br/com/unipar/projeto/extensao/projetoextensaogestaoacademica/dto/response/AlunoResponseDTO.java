package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response;

import java.time.LocalDateTime;

public class AlunoResponseDTO {

    private Long id;
    private String nome;
    private String matricula;
    private String curso;
    private String email;
    private Integer periodo;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long usuarioCriadorId;
    private String usuarioCriadorNome;


    public AlunoResponseDTO() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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