package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response;

public class AuthResponseDTO {

    private String token;
    private String tipo = "Bearer";
    private String email;
    private String perfil;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String email, String perfil) {
        this.token = token;
        this.email = email;
        this.perfil = perfil;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}