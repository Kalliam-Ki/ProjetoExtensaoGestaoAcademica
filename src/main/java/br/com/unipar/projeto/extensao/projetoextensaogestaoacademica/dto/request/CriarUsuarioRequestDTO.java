package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;

public class CriarUsuarioRequestDTO {

    private String nome;
    private String email;
    private String senha;
    private PerfilUsuario perfil;

    // Construtor padrão
    public CriarUsuarioRequestDTO() {
    }

    // Construtor com parâmetros
    public CriarUsuarioRequestDTO(String nome, String email, String senha, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }

    // Getters e Setters
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
}
