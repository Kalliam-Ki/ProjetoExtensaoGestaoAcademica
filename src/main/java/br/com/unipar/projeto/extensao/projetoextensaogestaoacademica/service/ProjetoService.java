package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.AtualizarProjetoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.CriarProjetoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.ProjetoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.ProjetoResumoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception.NegocioException;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception.NotFoundException;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Projeto;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.StatusProjeto;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.ProjetoRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    // Logger para logs melhores e facilitar no debug
    private static final Logger logger = LoggerFactory.getLogger(ProjetoService.class);
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final HttpServletRequest request;

    public ProjetoService(ProjetoRepository projetoRepository,
                          UsuarioRepository usuarioRepository,
                          AuditoriaService auditoriaService,
                          HttpServletRequest request) {
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.request = request;
    }

    public ProjetoResponseDTO criarProjeto(CriarProjetoRequestDTO request, Long usuarioCriadorId) {
        logger.info("Criando projeto: {} para usuario: {}", request.getTitulo(), usuarioCriadorId);

        // Valida se já existe projeto com mesmo título (case insensitive)
        if (projetoRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new NegocioException("Já existe um projeto com este título");
        }

        // Valida se usuário tem permissão para criar projetos
        Usuario usuarioCriador = usuarioRepository.findById(usuarioCriadorId)
                .orElseThrow(() -> {
                    logger.warn("Usuario criador nao encontrado com ID: {}", usuarioCriadorId);
                    return new NotFoundException("Usuario criador nao encontrado");
                });

        // Apenas ADMINISTRADOR e COORDENADOR podem criar projetos
        if (usuarioCriador.getPerfil() == PerfilUsuario.CONSULTA) {
            throw new NegocioException("Usuários com perfil CONSULTA não podem criar projetos");
        }

        // Cria o projeto
        Projeto projeto = new Projeto(
                request.getTitulo(),
                request.getDescricao(),
                request.getOrientador(),
                request.getArea(),
                usuarioCriadorId
        );

        projeto.setDataPrevistaTermino(request.getDataPrevistaTermino());
        // Valida data prevista de término
        validarDatasProjeto(request.getDataPrevistaTermino());

        Projeto projetoSalvo = projetoRepository.save(projeto);
        logger.info("Projeto criado com sucesso com ID: {}", projetoSalvo.getId());

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "PROJETO",
                projetoSalvo.getId(),
                "CREATE",
                null,
                projetoSalvo,
                usuarioCriadorId,
                this.request
        );

        return converterParaDTO(projetoSalvo, usuarioCriador.getNome());
    }

    public ProjetoResponseDTO buscarProjetoPorId(Long id) {
        logger.debug("Buscando projeto por ID: {}", id);

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Projeto nao encontrado com ID: {}", id);
                    return new NotFoundException("Projeto nao encontrado com ID: " + id);
                });

        String nomeUsuarioCriador = obterNomeUsuarioCriador(projeto.getUsuarioCriadorId());
        return converterParaDTO(projeto, nomeUsuarioCriador);
    }

    public List<ProjetoResumoResponseDTO> listarTodosProjetos() {
        logger.info("Listando todos os projetos");

        List<Projeto> projetos = projetoRepository.findAll();
        logger.debug("Encontrados {} projetos", projetos.size());

        return projetos.stream()
                .map(this::converterParaResumoDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetoResumoResponseDTO> listarProjetosPorStatus(StatusProjeto status) {
        logger.debug("Listando projetos por status: {}", status);

        List<Projeto> projetos = projetoRepository.findByStatus(status);
        logger.debug("Encontrados {} projetos com status {}", projetos.size(), status);

        return projetos.stream()
                .map(this::converterParaResumoDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetoResumoResponseDTO> listarProjetosPorArea(String area) {
        logger.debug("Listando projetos por area: {}", area);

        List<Projeto> projetos = projetoRepository.findByArea(area);
        logger.debug("Encontrados {} projetos na area {}", projetos.size(), area);

        // CORREÇÃO: estava "projets" - agora é "projetos"
        return projetos.stream()
                .map(this::converterParaResumoDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetoResumoResponseDTO> listarProjetosPorUsuarioCriador(Long usuarioCriadorId) {
        logger.debug("Listando projetos por usuario criador: {}", usuarioCriadorId);

        List<Projeto> projetos = projetoRepository.findByUsuarioCriadorId(usuarioCriadorId);
        logger.debug("Encontrados {} projetos do usuario {}", projetos.size(), usuarioCriadorId);

        return projetos.stream()
                .map(this::converterParaResumoDTO)
                .collect(Collectors.toList());
    }

    public ProjetoResponseDTO atualizarProjeto(Long id, AtualizarProjetoRequestDTO request, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado"));

        // Valida se usuário tem permissão para editar
        validarPermissaoEdicao(projeto, usuarioId);

        // Valida se projeto pode ser editado (apenas RASCUNHO e SUBMETIDO)
        if (projeto.getStatus() != StatusProjeto.RASCUNHO &&
                projeto.getStatus() != StatusProjeto.SUBMETIDO) {
            throw new NegocioException("Projeto não pode ser editado no status atual: " + projeto.getStatus());
        }

        // Clona estado antigo para auditoria
        Projeto projetoAntigo = new Projeto();
        projetoAntigo.setTitulo(projeto.getTitulo());
        projetoAntigo.setDescricao(projeto.getDescricao());
        projetoAntigo.setOrientador(projeto.getOrientador());
        projetoAntigo.setArea(projeto.getArea());
        projetoAntigo.setDataPrevistaTermino(projeto.getDataPrevistaTermino());

        // Aplica as atualizações
        projeto.setTitulo(request.getTitulo());
        projeto.setDescricao(request.getDescricao());
        projeto.setOrientador(request.getOrientador());
        projeto.setArea(request.getArea());
        projeto.setDataPrevistaTermino(request.getDataPrevistaTermino());

        // Valida a nova data
        validarDatasProjeto(request.getDataPrevistaTermino());

        Projeto projetoAtualizado = projetoRepository.save(projeto);

        // Registra auditoria
        auditoriaService.registrarAlteracao(
                "PROJETO",
                projeto.getId(),
                "UPDATE",
                projetoAntigo,
                projetoAtualizado,
                usuarioId,
                this.request
        );

        String nomeUsuarioCriador = obterNomeUsuarioCriador(projeto.getUsuarioCriadorId());
        return converterParaDTO(projetoAtualizado, nomeUsuarioCriador);
    }

    private void validarPermissaoEdicao(Projeto projeto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        // ADMIN pode editar qualquer projeto
        if (usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            return;
        }

        // COORDENADOR só pode editar projetos que criou
        if (usuario.getPerfil() == PerfilUsuario.COORDENADOR &&
                !projeto.getUsuarioCriadorId().equals(usuarioId)) {
            throw new NegocioException("Coordenador só pode editar projetos próprios");
        }

        // CONSULTA não pode editar
        if (usuario.getPerfil() == PerfilUsuario.CONSULTA) {
            throw new NegocioException("Usuário CONSULTA não tem permissão para editar projetos");
        }
    }

    public ProjetoResponseDTO atualizarStatusProjeto(Long id, StatusProjeto novoStatus, Long usuarioId) {
        logger.info("Atualizando status do projeto ID: {} para {} pelo usuario: {}", id, novoStatus, usuarioId);

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de atualizar status. Projeto nao encontrado com ID: {}", id);
                    return new NotFoundException("Projeto nao encontrado com ID: " + id);
                });

        Projeto projetoAntigo = new Projeto();
        projetoAntigo.setStatus(projeto.getStatus());
        projetoAntigo.setDataInicio(projeto.getDataInicio());

        validarTransicaoStatus(projeto.getStatus(), novoStatus);

        projeto.setStatus(novoStatus);

        // Se for aprovado, define data de início
        if (novoStatus == StatusProjeto.APROVADO && projeto.getDataInicio() == null) {
            projeto.setDataInicio(LocalDate.now());
        }

        // Se for concluído, valida se pode ser concluído
        if (novoStatus == StatusProjeto.CONCLUIDO) {
            validarConclusaoProjeto(projeto);
        }

        Projeto projetoAtualizado = projetoRepository.save(projeto);

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "PROJETO",
                projetoAtualizado.getId(),
                "UPDATE",
                projetoAntigo,
                projetoAtualizado,
                usuarioId,
                this.request
        );

        logger.info("Status do projeto atualizado: {}", projetoAtualizado.getStatus());

        String nomeUsuarioCriador = obterNomeUsuarioCriador(projeto.getUsuarioCriadorId());
        return converterParaDTO(projetoAtualizado, nomeUsuarioCriador);
    }

    private void validarTransicaoStatus(StatusProjeto statusAtual, StatusProjeto novoStatus) {

        // Transições permitidas baseadas no diagrama de estados

        switch (statusAtual) {
            case RASCUNHO:
                if (novoStatus != StatusProjeto.SUBMETIDO) {
                    throw new NegocioException("Projeto em RASCUNHO so pode ser SUBMETIDO");
                }
                break;

            case SUBMETIDO:
                if (novoStatus != StatusProjeto.EM_ANALISE && novoStatus != StatusProjeto.RASCUNHO) {
                    throw new NegocioException("Projeto SUBMETIDO so pode voltar para RASCUNHO ou ir para EM_ANALISE");
                }
                break;

            case EM_ANALISE:
                if (novoStatus != StatusProjeto.APROVADO && novoStatus != StatusProjeto.RASCUNHO) {
                    throw new NegocioException("Projeto EM_ANALISE so pode ser APROVADO ou voltar para RASCUNHO");
                }
                break;

            case APROVADO:
                if (novoStatus != StatusProjeto.EM_ANDAMENTO) {
                    throw new NegocioException("Projeto APROVADO so pode ir para EM_ANDAMENTO");
                }
                break;

            case EM_ANDAMENTO:
                if (novoStatus != StatusProjeto.CONCLUIDO && novoStatus != StatusProjeto.APROVADO) {
                    throw new NegocioException("Projeto EM_ANDAMENTO so pode ser CONCLUIDO ou voltar para APROVADO");
                }
                break;

            case CONCLUIDO:
                throw new NegocioException("Projeto CONCLUIDO nao pode ter seu status alterado");

            default:
                throw new NegocioException("Status desconhecido: " + statusAtual);
        }

        logger.debug("Transicao de status validada: {} -> {}", statusAtual, novoStatus);
    }

    public ProjetoResponseDTO submeterProjeto(Long id, Long usuarioId) {
        logger.info("Submetendo projeto ID: {} pelo usuario: {}", id, usuarioId);

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Projeto nao encontrado com ID: {}", id);
                    return new NotFoundException("Projeto nao encontrado com ID: " + id);
                });

        // Verifica permissão: apenas criador ou ADMIN podem submeter
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));
        if (usuario.getPerfil() != PerfilUsuario.ADMINISTRADOR &&
            !projeto.getUsuarioCriadorId().equals(usuarioId)) {
            throw new NegocioException("Voce nao tem permissao para submeter este projeto");
        }

        // Valida se pode ser submetido
        if (projeto.getStatus() != StatusProjeto.RASCUNHO) {
            throw new NegocioException("Apenas projetos em RASCUNHO podem ser submetidos");
        }

        // Valida campos obrigatórios e data
        validarCamposSubmissao(projeto);

        projeto.setStatus(StatusProjeto.SUBMETIDO);
        Projeto projetoAtualizado = projetoRepository.save(projeto);

        logger.info("Projeto submetido com sucesso: {}", projeto.getTitulo());

        String nomeUsuarioCriador = obterNomeUsuarioCriador(projeto.getUsuarioCriadorId());
        return converterParaDTO(projetoAtualizado, nomeUsuarioCriador);
    }

    private void validarCamposSubmissao(Projeto projeto) {
        if (projeto.getTitulo() == null || projeto.getTitulo().trim().isEmpty()) {
            throw new NegocioException("Titulo é obrigatorio para submeter o projeto");
        }
        if (projeto.getOrientador() == null || projeto.getOrientador().trim().isEmpty()) {
            throw new NegocioException("Orientador é obrigatorio para submeter o projeto");
        }
        if (projeto.getArea() == null || projeto.getArea().trim().isEmpty()) {
            throw new NegocioException("Area é obrigatoria para submeter o projeto");
        }
        if (projeto.getDataPrevistaTermino() == null) {
            throw new NegocioException("Data prevista de termino é obrigatoria para submeter o projeto");
        }

        // Valida a data prevista de término (não pode ser anterior à data atual)
        validarDatasProjeto(projeto.getDataPrevistaTermino());
    }

    private void validarDatasProjeto(LocalDate dataPrevistaTermino) {
        if (dataPrevistaTermino.isBefore(LocalDate.now())) {
            throw new NegocioException("Data prevista de término não pode ser anterior à data atual");
        }
    }

    private void validarConclusaoProjeto(Projeto projeto) {

        // Verificar se todas as atividades estão concluídas

        if (projeto.getDataPrevistaTermino() != null &&
                projeto.getDataPrevistaTermino().isAfter(LocalDate.now())) {
            logger.warn("Projeto sendo concluido antes da data prevista: {}", projeto.getId());
            // Gera apenas um aviso
        }
    }

    public void excluirProjeto(Long id, Long usuarioId) {
        logger.info("Excluindo projeto ID: {} pelo usuario: {}", id, usuarioId);

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de excluir projeto nao encontrado com ID: {}", id);
                    return new NotFoundException("Projeto nao encontrado com ID: " + id);
                });

        // VALIDAÇÃO: Não permitir excluir projetos que não estão em RASCUNHO
        if (projeto.getStatus() != StatusProjeto.RASCUNHO) {
            throw new NegocioException("Apenas projetos em RASCUNHO podem ser excluídos");
        }

        // Registra na auditoria antes de excluir
        auditoriaService.registrarAlteracao(
                "PROJETO",
                projeto.getId(),
                "DELETE",
                projeto,
                null,
                usuarioId,
                this.request
        );

        projetoRepository.delete(projeto);
        logger.info("Projeto excluido: {}", projeto.getTitulo());
    }

    private ProjetoResponseDTO converterParaDTO(Projeto projeto, String nomeUsuarioCriador) {
        if (projeto == null) {
            logger.error("Tentativa de converter projeto nulo para DTO");
            throw new NegocioException("Projeto não pode ser nulo");
        }

        ProjetoResponseDTO dto = new ProjetoResponseDTO();
        dto.setId(projeto.getId());
        dto.setTitulo(projeto.getTitulo());
        dto.setDescricao(projeto.getDescricao());
        dto.setOrientador(projeto.getOrientador());
        dto.setArea(projeto.getArea());
        dto.setStatus(projeto.getStatus());
        dto.setDataInicio(projeto.getDataInicio());
        dto.setDataPrevistaTermino(projeto.getDataPrevistaTermino());
        dto.setDataCriacao(projeto.getDataCriacao());
        dto.setDataAtualizacao(projeto.getDataAtualizacao());
        dto.setUsuarioCriadorId(projeto.getUsuarioCriadorId());
        dto.setUsuarioCriadorNome(nomeUsuarioCriador);

        return dto;
    }

    private ProjetoResumoResponseDTO converterParaResumoDTO(Projeto projeto) {
        if (projeto == null) {
            logger.error("Tentativa de converter projeto nulo para DTO resumido");
            throw new NegocioException("Projeto não pode ser nulo");
        }

        ProjetoResumoResponseDTO dto = new ProjetoResumoResponseDTO();
        dto.setId(projeto.getId());
        dto.setTitulo(projeto.getTitulo());
        dto.setOrientador(projeto.getOrientador());
        dto.setArea(projeto.getArea());
        dto.setStatus(projeto.getStatus());
        dto.setDataPrevistaTermino(projeto.getDataPrevistaTermino());
        dto.setDataCriacao(projeto.getDataCriacao());

        return dto;
    }

    private String obterNomeUsuarioCriador(Long usuarioCriadorId) {
        return usuarioRepository.findById(usuarioCriadorId)
                .map(Usuario::getNome)
                .orElse("Usuário não encontrado");
    }
}