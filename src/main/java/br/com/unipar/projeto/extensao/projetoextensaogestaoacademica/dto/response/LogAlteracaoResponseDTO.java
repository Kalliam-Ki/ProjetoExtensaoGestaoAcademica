package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response;

import java.time.LocalDateTime;

public class LogAlteracaoResponseDTO {

    private Long id;
    private String tabelaAfetada;
    private Long idRegistroAfetado;
    private String acao;
    private String valoresAntigos;
    private String valoresNovos;
    private Long usuarioId;
    private String usuarioNome;
    private LocalDateTime dataAlteracao;
    private String ipRequisicao;


    public LogAlteracaoResponseDTO() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTabelaAfetada() {
        return tabelaAfetada;
    }

    public void setTabelaAfetada(String tabelaAfetada) {
        this.tabelaAfetada = tabelaAfetada;
    }

    public Long getIdRegistroAfetado() {
        return idRegistroAfetado;
    }

    public void setIdRegistroAfetado(Long idRegistroAfetado) {
        this.idRegistroAfetado = idRegistroAfetado;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getValoresAntigos() {
        return valoresAntigos;
    }

    public void setValoresAntigos(String valoresAntigos) {
        this.valoresAntigos = valoresAntigos;
    }

    public String getValoresNovos() {
        return valoresNovos;
    }

    public void setValoresNovos(String valoresNovos) {
        this.valoresNovos = valoresNovos;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getIpRequisicao() {
        return ipRequisicao;
    }

    public void setIpRequisicao(String ipRequisicao) {
        this.ipRequisicao = ipRequisicao;
    }
}