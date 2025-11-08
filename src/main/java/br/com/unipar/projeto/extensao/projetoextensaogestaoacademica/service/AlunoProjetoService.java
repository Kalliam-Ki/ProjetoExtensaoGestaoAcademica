package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.VincularAlunoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.AlunoProjetoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Aluno;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.AlunoProjeto;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Projeto;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.AlunoProjetoRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.AlunoRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.ProjetoRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoProjetoService {

    private static final Logger logger = LoggerFactory.getLogger(AlunoProjetoService.class);
    private final AlunoProjetoRepository alunoProjetoRepository;
    private final AlunoRepository alunoRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final HttpServletRequest request;

    public AlunoProjetoService(AlunoProjetoRepository alunoProjetoRepository,
                               AlunoRepository alunoRepository,
                               ProjetoRepository projetoRepository,
                               UsuarioRepository usuarioRepository,
                               AuditoriaService auditoriaService,
                               HttpServletRequest request) {
        this.alunoProjetoRepository = alunoProjetoRepository;
        this.alunoRepository = alunoRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.request = request;
    }

    public AlunoProjetoResponseDTO vincularAlunoProjeto(VincularAlunoRequestDTO request, Long usuarioCriadorId) {
        logger.info("Vinculando aluno {} ao projeto {} pelo usuario: {}",
                request.getAlunoId(), request.getProjetoId(), usuarioCriadorId);

        // Valida se o usuário criador existe e tem permissão
        Usuario usuarioCriador = usuarioRepository.findById(usuarioCriadorId)
                .orElseThrow(() -> {
                    logger.warn("Usuario criador nao encontrado com ID: {}", usuarioCriadorId);
                    return new RuntimeException("Usuario criador nao encontrado");
                });

        // Valida permissão para vincular aluno
        validarPermissaoVincularAluno(usuarioCriador);

        // Busca aluno e projeto
        Aluno aluno = alunoRepository.findById(request.getAlunoId())
                .orElseThrow(() -> {
                    logger.warn("Aluno nao encontrado com ID: {}", request.getAlunoId());
                    return new RuntimeException("Aluno nao encontrado com ID: " + request.getAlunoId());
                });

        Projeto projeto = projetoRepository.findById(request.getProjetoId())
                .orElseThrow(() -> {
                    logger.warn("Projeto nao encontrado com ID: {}", request.getProjetoId());
                    return new RuntimeException("Projeto nao encontrado com ID: " + request.getProjetoId());
                });

        // Valida se aluno está ativo
        if (!aluno.getAtivo()) {
            throw new RuntimeException("Nao e possivel vincular aluno inativo");
        }

        // Valida se projeto está em status adequado para vincular alunos
        validarStatusProjetoParaVinculo(projeto);

        // Valida se já existe vínculo ativo
        if (alunoProjetoRepository.existsByAlunoIdAndProjetoIdAndAtivoTrue(aluno.getId(), projeto.getId())) {
            throw new RuntimeException("Aluno ja esta vinculado a este projeto");
        }

        // Cria o vínculo
        AlunoProjeto alunoProjeto = new AlunoProjeto(aluno, projeto, request.getFuncao(), usuarioCriadorId);
        AlunoProjeto alunoProjetoSalvo = alunoProjetoRepository.save(alunoProjeto);
        logger.info("Aluno vinculado com sucesso ao projeto. ID do vinculo: {}", alunoProjetoSalvo.getId());

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "ALUNO_PROJETO",
                alunoProjetoSalvo.getId(),
                "CREATE",
                null,
                alunoProjetoSalvo,
                usuarioCriadorId,
                this.request
        );

        return converterParaDTO(alunoProjetoSalvo);
    }

    public List<AlunoProjetoResponseDTO> listarVinculosPorAluno(Long alunoId) {
        logger.debug("Listando vinculos do aluno: {}", alunoId);

        // Verifica se aluno existe
        if (!alunoRepository.existsById(alunoId)) {
            throw new RuntimeException("Aluno nao encontrado com ID: " + alunoId);
        }

        List<AlunoProjeto> vinculos = alunoProjetoRepository.findByAlunoId(alunoId);
        logger.debug("Encontrados {} vinculos para o aluno {}", vinculos.size(), alunoId);

        return vinculos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<AlunoProjetoResponseDTO> listarVinculosPorProjeto(Long projetoId) {
        logger.debug("Listando vinculos do projeto: {}", projetoId);

        // Verifica se projeto existe
        if (!projetoRepository.existsById(projetoId)) {
            throw new RuntimeException("Projeto nao encontrado com ID: " + projetoId);
        }

        List<AlunoProjeto> vinculos = alunoProjetoRepository.findByProjetoId(projetoId);
        logger.debug("Encontrados {} vinculos para o projeto {}", vinculos.size(), projetoId);

        return vinculos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<AlunoProjetoResponseDTO> listarVinculosAtivosPorAluno(Long alunoId) {
        logger.debug("Listando vinculos ativos do aluno: {}", alunoId);

        // Verifica se aluno existe
        if (!alunoRepository.existsById(alunoId)) {
            throw new RuntimeException("Aluno nao encontrado com ID: " + alunoId);
        }

        List<AlunoProjeto> vinculos = alunoProjetoRepository.findByAlunoIdAndAtivoTrue(alunoId);
        logger.debug("Encontrados {} vinculos ativos para o aluno {}", vinculos.size(), alunoId);

        return vinculos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<AlunoProjetoResponseDTO> listarVinculosAtivosPorProjeto(Long projetoId) {
        logger.debug("Listando vinculos ativos do projeto: {}", projetoId);

        // Verifica se projeto existe
        if (!projetoRepository.existsById(projetoId)) {
            throw new RuntimeException("Projeto nao encontrado com ID: " + projetoId);
        }

        List<AlunoProjeto> vinculos = alunoProjetoRepository.findByProjetoIdAndAtivoTrue(projetoId);
        logger.debug("Encontrados {} vinculos ativos para o projeto {}", vinculos.size(), projetoId);

        return vinculos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public void desvincularAlunoProjeto(Long alunoProjetoId, Long usuarioId) {
        logger.info("Desvinculando aluno do projeto. ID do vinculo: {} pelo usuario: {}", alunoProjetoId, usuarioId);

        AlunoProjeto alunoProjeto = alunoProjetoRepository.findById(alunoProjetoId)
                .orElseThrow(() -> {
                    logger.warn("Vinculo nao encontrado com ID: {}", alunoProjetoId);
                    return new RuntimeException("Vinculo nao encontrado com ID: " + alunoProjetoId);
                });

        // Valida se usuário tem permissão para desvincular
        validarPermissaoDesvincularAluno(alunoProjeto, usuarioId);

        // Valida se já está desvinculado
        if (!alunoProjeto.getAtivo()) {
            throw new RuntimeException("Aluno ja esta desvinculado deste projeto");
        }

        // Salva estado antigo para auditoria
        AlunoProjeto alunoProjetoAntigo = new AlunoProjeto();
        alunoProjetoAntigo.setAtivo(alunoProjeto.getAtivo());
        alunoProjetoAntigo.setDataDesvinculacao(alunoProjeto.getDataDesvinculacao());

        alunoProjeto.setAtivo(false);
        alunoProjeto.setDataDesvinculacao(java.time.LocalDate.now());
        alunoProjetoRepository.save(alunoProjeto);

        // Registra auditoria
        auditoriaService.registrarAlteracao(
                "ALUNO_PROJETO",
                alunoProjeto.getId(),
                "UPDATE",
                alunoProjetoAntigo,
                alunoProjeto,
                usuarioId,
                this.request
        );

        logger.info("Aluno desvinculado do projeto com sucesso");
    }

    public void reativarVinculo(Long alunoProjetoId, Long usuarioId) {
        logger.info("Reativando vinculo. ID: {} pelo usuario: {}", alunoProjetoId, usuarioId);

        AlunoProjeto alunoProjeto = alunoProjetoRepository.findById(alunoProjetoId)
                .orElseThrow(() -> {
                    logger.warn("Vinculo nao encontrado com ID: {}", alunoProjetoId);
                    return new RuntimeException("Vinculo nao encontrado com ID: " + alunoProjetoId);
                });

        // Valida se usuário tem permissão para reativar
        validarPermissaoDesvincularAluno(alunoProjeto, usuarioId);

        // Valida se já está ativo
        if (alunoProjeto.getAtivo()) {
            throw new RuntimeException("Vinculo ja esta ativo");
        }

        // Valida se aluno ainda está ativo
        if (!alunoProjeto.getAluno().getAtivo()) {
            throw new RuntimeException("Nao e possivel reativar vinculo com aluno inativo");
        }

        // Valida se projeto está em status adequado
        validarStatusProjetoParaVinculo(alunoProjeto.getProjeto());

        alunoProjeto.setAtivo(true);
        alunoProjeto.setDataDesvinculacao(null);
        alunoProjetoRepository.save(alunoProjeto);

        logger.info("Vinculo reativado com sucesso");
    }

    // Métodos de validação
    private void validarPermissaoVincularAluno(Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.CONSULTA) {
            throw new RuntimeException("Usuarios com perfil CONSULTA nao podem vincular alunos a projetos");
        }
        logger.debug("Permissao validada para vincular aluno: {}", usuario.getPerfil());
    }

    private void validarPermissaoDesvincularAluno(AlunoProjeto alunoProjeto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        // ADMIN pode desvincular qualquer aluno
        if (usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            return;
        }

        // COORDENADOR só pode desvincular alunos de projetos que criou
        if (usuario.getPerfil() == PerfilUsuario.COORDENADOR) {
            if (!alunoProjeto.getProjeto().getUsuarioCriadorId().equals(usuarioId)) {
                throw new RuntimeException("Coordenador so pode desvincular alunos de projetos proprios");
            }
            return;
        }

        // CONSULTA não pode desvincular
        if (usuario.getPerfil() == PerfilUsuario.CONSULTA) {
            throw new RuntimeException("Usuario CONSULTA nao tem permissao para desvincular alunos");
        }
    }

    private void validarStatusProjetoParaVinculo(Projeto projeto) {
        // Só permite vincular alunos em projetos que estão em andamento ou aprovados
        if (projeto.getStatus().ordinal() < br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.StatusProjeto.APROVADO.ordinal()) {
            throw new RuntimeException("Nao e possivel vincular alunos a projetos que nao estao aprovados ou em andamento");
        }
    }

    // Método auxiliar para converter para DTO
    private AlunoProjetoResponseDTO converterParaDTO(AlunoProjeto alunoProjeto) {
        if (alunoProjeto == null) {
            logger.error("Tentativa de converter AlunoProjeto nulo para DTO");
            throw new RuntimeException("AlunoProjeto nao pode ser nulo");
        }

        AlunoProjetoResponseDTO dto = new AlunoProjetoResponseDTO();
        dto.setId(alunoProjeto.getId());
        dto.setAlunoId(alunoProjeto.getAluno().getId());
        dto.setAlunoNome(alunoProjeto.getAluno().getNome());
        dto.setAlunoMatricula(alunoProjeto.getAluno().getMatricula());
        dto.setProjetoId(alunoProjeto.getProjeto().getId());
        dto.setProjetoTitulo(alunoProjeto.getProjeto().getTitulo());
        dto.setProjetoOrientador(alunoProjeto.getProjeto().getOrientador());
        dto.setFuncao(alunoProjeto.getFuncao());
        dto.setDataVinculacao(alunoProjeto.getDataVinculacao());
        dto.setDataDesvinculacao(alunoProjeto.getDataDesvinculacao());
        dto.setAtivo(alunoProjeto.getAtivo());
        dto.setDataCriacao(alunoProjeto.getDataCriacao());
        dto.setUsuarioCriadorId(alunoProjeto.getUsuarioCriadorId());

        // Busca nome do usuário criador
        String nomeUsuarioCriador = usuarioRepository.findById(alunoProjeto.getUsuarioCriadorId())
                .map(Usuario::getNome)
                .orElse("Usuario nao encontrado");
        dto.setUsuarioCriadorNome(nomeUsuarioCriador);

        return dto;
    }
}