package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VincularAlunoRequestDTO {

    @NotNull(message = "ID do aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "ID do projeto é obrigatório")
    private Long projetoId;

    @NotBlank(message = "Função é obrigatória")
    @Size(max = 50, message = "Função deve ter no máximo 50 caracteres")
    private String funcao;


    public VincularAlunoRequestDTO() {}


    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public Long getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(Long projetoId) {
        this.projetoId = projetoId;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
}