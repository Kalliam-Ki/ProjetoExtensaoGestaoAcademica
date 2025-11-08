package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlunoProjetoResponseDTO {

    private Long id;
    private Long alunoId;
    private String alunoNome;
    private String alunoMatricula;
    private Long projetoId;
    private String projetoTitulo;
    private String projetoOrientador;
    private String funcao;
    private LocalDate dataVinculacao;
    private LocalDate dataDesvinculacao;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private Long usuarioCriadorId;
    private String usuarioCriadorNome;


    public AlunoProjetoResponseDTO() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
    }

    public String getAlunoMatricula() {
        return alunoMatricula;
    }

    public void setAlunoMatricula(String alunoMatricula) {
        this.alunoMatricula = alunoMatricula;
    }

    public Long getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(Long projetoId) {
        this.projetoId = projetoId;
    }

    public String getProjetoTitulo() {
        return projetoTitulo;
    }

    public void setProjetoTitulo(String projetoTitulo) {
        this.projetoTitulo = projetoTitulo;
    }

    public String getProjetoOrientador() {
        return projetoOrientador;
    }

    public void setProjetoOrientador(String projetoOrientador) {
        this.projetoOrientador = projetoOrientador;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public LocalDate getDataVinculacao() {
        return dataVinculacao;
    }

    public void setDataVinculacao(LocalDate dataVinculacao) {
        this.dataVinculacao = dataVinculacao;
    }

    public LocalDate getDataDesvinculacao() {
        return dataDesvinculacao;
    }

    public void setDataDesvinculacao(LocalDate dataDesvinculacao) {
        this.dataDesvinculacao = dataDesvinculacao;
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