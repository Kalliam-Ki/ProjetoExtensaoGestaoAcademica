package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import java.time.LocalDateTime;

public class UsuarioResponseDTO {

    // Sem senha por questão de segurança
    private Long id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private Boolean ativo;
    private LocalDateTime dataCriacao;

    // Construtor padrão
    public UsuarioResponseDTO() {}

    // Construtor com parâmetros
    public UsuarioResponseDTO(Long id, String nome, String email, PerfilUsuario perfil,
                              Boolean ativo, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
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
}