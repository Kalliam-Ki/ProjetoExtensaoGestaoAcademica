package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aluno_projeto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"aluno_id", "projeto_id"})
})
public class AlunoProjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @Column(nullable = false, length = 50)
    private String funcao;

    @Column(name = "data_vinculacao", nullable = false)
    private LocalDate dataVinculacao;

    @Column(name = "data_desvinculacao")
    private LocalDate dataDesvinculacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "usuario_criador_id", nullable = false)
    private Long usuarioCriadorId;

    public AlunoProjeto() {}

    public AlunoProjeto(Aluno aluno, Projeto projeto, String funcao, Long usuarioCriadorId) {
        this.aluno = aluno;
        this.projeto = projeto;
        this.funcao = funcao;
        this.usuarioCriadorId = usuarioCriadorId;
        this.ativo = true;
    }

    // Getters e Setters (TODOS necessários)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }

    public LocalDate getDataVinculacao() { return dataVinculacao; }
    public void setDataVinculacao(LocalDate dataVinculacao) { this.dataVinculacao = dataVinculacao; }

    public LocalDate getDataDesvinculacao() { return dataDesvinculacao; }
    public void setDataDesvinculacao(LocalDate dataDesvinculacao) { this.dataDesvinculacao = dataDesvinculacao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Long getUsuarioCriadorId() { return usuarioCriadorId; }
    public void setUsuarioCriadorId(Long usuarioCriadorId) { this.usuarioCriadorId = usuarioCriadorId; }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (dataVinculacao == null) {
            dataVinculacao = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "AlunoProjeto{" +
                "id=" + id +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", projeto=" + (projeto != null ? projeto.getTitulo() : "null") +
                ", funcao='" + funcao + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}