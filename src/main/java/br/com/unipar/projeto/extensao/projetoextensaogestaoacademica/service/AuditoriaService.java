package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.LogAlteracaoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception.NotFoundException;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.LogAlteracao;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.LogAlteracaoRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditoriaService {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaService.class);
    private final LogAlteracaoRepository logAlteracaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public AuditoriaService(LogAlteracaoRepository logAlteracaoRepository,
                            UsuarioRepository usuarioRepository,
                            ObjectMapper objectMapper) {
        this.logAlteracaoRepository = logAlteracaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
    }

    public void registrarAlteracao(String tabelaAfetada, Long idRegistroAfetado, String acao,
                                   Object valoresAntigos, Object valoresNovos, Long usuarioId,
                                   HttpServletRequest request) {
        try {
            logger.debug("Registrando alteracao: {} na tabela {} ID: {} pelo usuario: {}",
                    acao, tabelaAfetada, idRegistroAfetado, usuarioId);

            String jsonValoresAntigos = converterParaJson(valoresAntigos);
            String jsonValoresNovos = converterParaJson(valoresNovos);
            String ipRequisicao = obterIpRequisicao(request);

            LogAlteracao log = new LogAlteracao(
                    tabelaAfetada,
                    idRegistroAfetado,
                    acao,
                    jsonValoresAntigos,
                    jsonValoresNovos,
                    usuarioId,
                    ipRequisicao
            );

            logAlteracaoRepository.save(log);
            logger.info("Alteracao registrada com sucesso. ID do log: {}", log.getId());

        } catch (Exception e) {
            logger.error("Erro ao registrar alteracao na auditoria: {}", e.getMessage());
        }
    }

    public List<LogAlteracaoResponseDTO> listarTodosLogs() {
        logger.info("Listando todos os logs de auditoria");
        List<LogAlteracao> logs = logAlteracaoRepository.findAll();
        logger.debug("Encontrados {} logs de auditoria", logs.size());
        return logs.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<LogAlteracaoResponseDTO> listarLogsPorTabela(String tabelaAfetada) {
        logger.debug("Listando logs da tabela: {}", tabelaAfetada);
        List<LogAlteracao> logs = logAlteracaoRepository.findByTabelaAfetada(tabelaAfetada);
        logger.debug("Encontrados {} logs para a tabela {}", logs.size(), tabelaAfetada);
        return logs.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<LogAlteracaoResponseDTO> listarLogsPorRegistro(String tabelaAfetada, Long idRegistroAfetado) {
        logger.debug("Listando logs do registro {} da tabela {}", idRegistroAfetado, tabelaAfetada);
        List<LogAlteracao> logs = logAlteracaoRepository.findByTabelaAfetadaAndIdRegistroAfetado(tabelaAfetada, idRegistroAfetado);
        logger.debug("Encontrados {} logs para o registro {}", logs.size(), idRegistroAfetado);
        return logs.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<LogAlteracaoResponseDTO> listarLogsPorUsuario(Long usuarioId) {
        logger.debug("Listando logs do usuario: {}", usuarioId);
        List<LogAlteracao> logs = logAlteracaoRepository.findByUsuarioId(usuarioId);
        logger.debug("Encontrados {} logs para o usuario {}", logs.size(), usuarioId);
        return logs.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<LogAlteracaoResponseDTO> listarLogsPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        logger.debug("Listando logs do periodo: {} ate {}", dataInicio, dataFim);
        List<LogAlteracao> logs = logAlteracaoRepository.findByDataAlteracaoBetween(dataInicio, dataFim);
        logger.debug("Encontrados {} logs no periodo especificado", logs.size());
        return logs.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<LogAlteracaoResponseDTO> listarLogsPorAcao(String acao) {
        logger.debug("Listando logs da acao: {}", acao);
        List<LogAlteracao> logs = logAlteracaoRepository.findByAcao(acao);
        logger.debug("Encontrados {} logs para a acao {}", logs.size(), acao);
        return logs.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<LogAlteracaoResponseDTO> listarLogsPorTabelaEPeriodo(String tabela, LocalDateTime dataInicio, LocalDateTime dataFim) {
        logger.debug("Listando logs da tabela {} no periodo: {} ate {}", tabela, dataInicio, dataFim);
        List<LogAlteracao> logs = logAlteracaoRepository.findByTabelaAndPeriodo(tabela, dataInicio, dataFim);
        logger.debug("Encontrados {} logs para a tabela {} no periodo especificado", logs.size(), tabela);
        return logs.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private String converterParaJson(Object objeto) {
        if (objeto == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            logger.warn("Erro ao converter objeto para JSON: {}", e.getMessage());
            return "{\"erro\": \"Falha na serializacao\"}";
        }
    }

    private String obterIpRequisicao(HttpServletRequest request) {
        if (request == null) {
            return "IP_NAO_DISPONIVEL";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    private LogAlteracaoResponseDTO converterParaDTO(LogAlteracao log) {
        if (log == null) {
            throw new NotFoundException("LogAlteracao nao pode ser nulo");
        }

        LogAlteracaoResponseDTO dto = new LogAlteracaoResponseDTO();
        dto.setId(log.getId());
        dto.setTabelaAfetada(log.getTabelaAfetada());
        dto.setIdRegistroAfetado(log.getIdRegistroAfetado());
        dto.setAcao(log.getAcao());
        dto.setValoresAntigos(log.getValoresAntigos());
        dto.setValoresNovos(log.getValoresNovos());
        dto.setUsuarioId(log.getUsuarioId());
        dto.setDataAlteracao(log.getDataAlteracao());
        dto.setIpRequisicao(log.getIpRequisicao());

        String nomeUsuario = usuarioRepository.findById(log.getUsuarioId())
                .map(Usuario::getNome)
                .orElse("Usuario nao encontrado");
        dto.setUsuarioNome(nomeUsuario);

        return dto;
    }
}