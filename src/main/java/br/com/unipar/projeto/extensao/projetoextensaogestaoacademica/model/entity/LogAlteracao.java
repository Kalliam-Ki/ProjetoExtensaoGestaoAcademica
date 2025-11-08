package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_alteracoes")
public class LogAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tabela_afetada", nullable = false, length = 50)
    private String tabelaAfetada;

    @Column(name = "id_registro_afetado", nullable = false)
    private Long idRegistroAfetado;

    @Column(nullable = false, length = 20)
    private String acao; // CREATE, UPDATE, DELETE

    @Column(name = "valores_antigos", columnDefinition = "TEXT")
    private String valoresAntigos; // JSON com valores antigos

    @Column(name = "valores_novos", columnDefinition = "TEXT")
    private String valoresNovos; // JSON com valores novos

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "data_alteracao", nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(name = "ip_requisicao", length = 45)
    private String ipRequisicao;


    public LogAlteracao() {}


    public LogAlteracao(String tabelaAfetada, Long idRegistroAfetado, String acao,
                        String valoresAntigos, String valoresNovos, Long usuarioId, String ipRequisicao) {
        this.tabelaAfetada = tabelaAfetada;
        this.idRegistroAfetado = idRegistroAfetado;
        this.acao = acao;
        this.valoresAntigos = valoresAntigos;
        this.valoresNovos = valoresNovos;
        this.usuarioId = usuarioId;
        this.ipRequisicao = ipRequisicao;
        this.dataAlteracao = LocalDateTime.now();
    }


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

    @Override
    public String toString() {
        return "LogAlteracao{" +
                "id=" + id +
                ", tabelaAfetada='" + tabelaAfetada + '\'' +
                ", idRegistroAfetado=" + idRegistroAfetado +
                ", acao='" + acao + '\'' +
                ", usuarioId=" + usuarioId +
                ", dataAlteracao=" + dataAlteracao +
                '}';
    }
}