package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.CriarAlunoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.AtualizarAlunoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.AlunoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception.NegocioException;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception.NotFoundException;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Aluno;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.AlunoRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    private static final Logger logger = LoggerFactory.getLogger(AlunoService.class);
    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final HttpServletRequest request;

    public AlunoService(AlunoRepository alunoRepository,
                        UsuarioRepository usuarioRepository,
                        AuditoriaService auditoriaService,
                        HttpServletRequest request) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.request = request;
    }

    public AlunoResponseDTO criarAluno(CriarAlunoRequestDTO request, Long usuarioCriadorId) {
        logger.info("Criando aluno: {} para usuario: {}", request.getNome(), usuarioCriadorId);

        // Valida se o usuário criador existe e tem permissão
        Usuario usuarioCriador = usuarioRepository.findById(usuarioCriadorId)
                .orElseThrow(() -> new NotFoundException("Usuario criador nao encontrado"));

        // Valida permissão para criar aluno
        validarPermissaoCriarAluno(usuarioCriador);

        // Valida se matrícula já existe
        if (alunoRepository.existsByMatricula(request.getMatricula())) {
            throw new NegocioException("Ja existe um aluno com esta matricula: " + request.getMatricula());
        }

        // Valida se email já existe
        if (alunoRepository.existsByEmail(request.getEmail())) {
            throw new NegocioException("Ja existe um aluno com este email: " + request.getEmail());
        }

        // Valida período
        validarPeriodo(request.getPeriodo());

        // Cria o aluno
        Aluno aluno = new Aluno(
                request.getNome(),
                request.getMatricula(),
                request.getCurso(),
                request.getEmail(),
                request.getPeriodo(),
                usuarioCriadorId
        );

        Aluno alunoSalvo = alunoRepository.save(aluno);
        logger.info("Aluno criado com sucesso com ID: {}", alunoSalvo.getId());

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "ALUNO",
                alunoSalvo.getId(),
                "CREATE",
                null,
                alunoSalvo,
                usuarioCriadorId,
                this.request
        );

        return converterParaDTO(alunoSalvo, usuarioCriador.getNome());
    }

    public AlunoResponseDTO buscarAlunoPorId(Long id) {
        logger.debug("Buscando aluno por ID: {}", id);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aluno nao encontrado com ID: " + id));

        String nomeUsuarioCriador = obterNomeUsuarioCriador(aluno.getUsuarioCriadorId());
        return converterParaDTO(aluno, nomeUsuarioCriador);
    }

    public AlunoResponseDTO buscarAlunoPorMatricula(String matricula) {
        logger.debug("Buscando aluno por matricula: {}", matricula);

        Aluno aluno = alunoRepository.findByMatricula(matricula)
                .orElseThrow(() -> new NotFoundException("Aluno nao encontrado com matricula: " + matricula));

        String nomeUsuarioCriador = obterNomeUsuarioCriador(aluno.getUsuarioCriadorId());
        return converterParaDTO(aluno, nomeUsuarioCriador);
    }

    public List<AlunoResponseDTO> listarTodosAlunos() {
        logger.info("Listando todos os alunos");

        List<Aluno> alunos = alunoRepository.findAll();
        logger.debug("Encontrados {} alunos", alunos.size());

        return alunos.stream()
                .map(aluno -> converterParaDTO(aluno, obterNomeUsuarioCriador(aluno.getUsuarioCriadorId())))
                .collect(Collectors.toList());
    }

    public List<AlunoResponseDTO> listarAlunosPorCurso(String curso) {
        logger.debug("Listando alunos por curso: {}", curso);

        List<Aluno> alunos = alunoRepository.findByCurso(curso);
        logger.debug("Encontrados {} alunos no curso {}", alunos.size(), curso);

        return alunos.stream()
                .map(aluno -> converterParaDTO(aluno, obterNomeUsuarioCriador(aluno.getUsuarioCriadorId())))
                .collect(Collectors.toList());
    }

    public List<AlunoResponseDTO> listarAlunosPorPeriodo(Integer periodo) {
        logger.debug("Listando alunos por periodo: {}", periodo);

        validarPeriodo(periodo);

        List<Aluno> alunos = alunoRepository.findByPeriodo(periodo);
        logger.debug("Encontrados {} alunos no periodo {}", alunos.size(), periodo);

        return alunos.stream()
                .map(aluno -> converterParaDTO(aluno, obterNomeUsuarioCriador(aluno.getUsuarioCriadorId())))
                .collect(Collectors.toList());
    }

    public List<AlunoResponseDTO> listarAlunosAtivos() {
        logger.debug("Listando alunos ativos");

        List<Aluno> alunos = alunoRepository.findByAtivoTrue();
        logger.debug("Encontrados {} alunos ativos", alunos.size());

        return alunos.stream()
                .map(aluno -> converterParaDTO(aluno, obterNomeUsuarioCriador(aluno.getUsuarioCriadorId())))
                .collect(Collectors.toList());
    }

    public AlunoResponseDTO atualizarAluno(Long id, AtualizarAlunoRequestDTO request, Long usuarioId) {
        logger.info("Atualizando aluno ID: {} pelo usuario: {}", id, usuarioId);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aluno nao encontrado com ID: " + id));

        // Valida se usuário tem permissão para editar
        validarPermissaoEdicaoAluno(aluno, usuarioId);

        // Valida se email já existe (para outro aluno)
        if (alunoRepository.findByEmail(request.getEmail())
                .filter(a -> !a.getId().equals(id))
                .isPresent()) {
            throw new NegocioException("Ja existe outro aluno com este email: " + request.getEmail());
        }

        // Valida período
        validarPeriodo(request.getPeriodo());

        // Aplica as atualizações
        aluno.setNome(request.getNome());
        aluno.setCurso(request.getCurso());
        aluno.setEmail(request.getEmail());
        aluno.setPeriodo(request.getPeriodo());

        Aluno alunoAtualizado = alunoRepository.save(aluno);
        logger.info("Aluno atualizado com sucesso: {}", alunoAtualizado.getNome());

        String nomeUsuarioCriador = obterNomeUsuarioCriador(aluno.getUsuarioCriadorId());
        return converterParaDTO(alunoAtualizado, nomeUsuarioCriador);
    }

    public void inativarAluno(Long id, Long usuarioId) {
        logger.info("Inativando aluno ID: {} pelo usuario: {}", id, usuarioId);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aluno nao encontrado com ID: " + id));

        validarPermissaoEdicaoAluno(aluno, usuarioId);

        Aluno alunoAntigo = new Aluno();
        alunoAntigo.setAtivo(aluno.getAtivo());

        aluno.setAtivo(false);
        alunoRepository.save(aluno);

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "ALUNO",
                aluno.getId(),
                "UPDATE",
                alunoAntigo,
                aluno,
                usuarioId,
                this.request
        );

        logger.info("Aluno inativado: {}", aluno.getNome());
    }

    public void ativarAluno(Long id, Long usuarioId) {
        logger.info("Ativando aluno ID: {} pelo usuario: {}", id, usuarioId);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aluno nao encontrado com ID: " + id));

        validarPermissaoEdicaoAluno(aluno, usuarioId);

        aluno.setAtivo(true);
        alunoRepository.save(aluno);

        logger.info("Aluno ativado: {}", aluno.getNome());
    }

    // Métodos de validação
    private void validarPermissaoCriarAluno(Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.CONSULTA) {
            throw new NegocioException("Usuarios com perfil CONSULTA nao podem criar alunos");
        }
        logger.debug("Permissao validada para criar aluno: {}", usuario.getPerfil());
    }

    private void validarPermissaoEdicaoAluno(Aluno aluno, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        // ADMIN pode editar qualquer aluno
        if (usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            return;
        }

        // COORDENADOR só pode editar alunos que criou
        if (usuario.getPerfil() == PerfilUsuario.COORDENADOR &&
                !aluno.getUsuarioCriadorId().equals(usuarioId)) {
            throw new NegocioException("Coordenador so pode editar alunos proprios");
        }

        // CONSULTA não pode editar
        if (usuario.getPerfil() == PerfilUsuario.CONSULTA) {
            throw new NegocioException("Usuario CONSULTA nao tem permissao para editar alunos");
        }
    }

    private void validarPeriodo(Integer periodo) {
        if (periodo == null || periodo < 1 || periodo > 12) {
            throw new NegocioException("Periodo deve ser um valor entre 1 e 12");
        }
    }

    // Métodos auxiliares
    private AlunoResponseDTO converterParaDTO(Aluno aluno, String nomeUsuarioCriador) {
        if (aluno == null) {
            logger.error("Tentativa de converter aluno nulo para DTO");
            throw new NegocioException("Aluno nao pode ser nulo");
        }

        AlunoResponseDTO dto = new AlunoResponseDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setMatricula(aluno.getMatricula());
        dto.setCurso(aluno.getCurso());
        dto.setEmail(aluno.getEmail());
        dto.setPeriodo(aluno.getPeriodo());
        dto.setAtivo(aluno.getAtivo());
        dto.setDataCriacao(aluno.getDataCriacao());
        dto.setDataAtualizacao(aluno.getDataAtualizacao());
        dto.setUsuarioCriadorId(aluno.getUsuarioCriadorId());
        dto.setUsuarioCriadorNome(nomeUsuarioCriador);

        return dto;
    }

    private String obterNomeUsuarioCriador(Long usuarioCriadorId) {
        return usuarioRepository.findById(usuarioCriadorId)
                .map(Usuario::getNome)
                .orElse("Usuario nao encontrado");
    }
}