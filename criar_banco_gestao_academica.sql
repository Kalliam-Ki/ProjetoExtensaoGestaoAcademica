
-- Criação do Banco de Dados

CREATE DATABASE gestaoacademica
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Portuguese_Brazil.1252'
    LC_CTYPE = 'Portuguese_Brazil.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Conectar ao banco criado
\c gestaoacademica;


-- Criação dos Enums

CREATE TYPE perfil_usuario AS ENUM ('ADMINISTRADOR', 'COORDENADOR', 'CONSULTA');
CREATE TYPE status_projeto AS ENUM ('RASCUNHO', 'SUBMETIDO', 'EM_ANALISE', 'APROVADO', 'EM_ANDAMENTO', 'CONCLUIDO');


-- Criação das tabelas

-- Tabela usuarios
CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          senha VARCHAR(255) NOT NULL,
                          perfil perfil_usuario NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT true,
                          data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          data_atualizacao TIMESTAMP
);

-- Tabela projetos
CREATE TABLE projetos (
                          id BIGSERIAL PRIMARY KEY,
                          titulo VARCHAR(100) NOT NULL,
                          descricao VARCHAR(500),
                          orientador VARCHAR(100) NOT NULL,
                          area VARCHAR(50) NOT NULL,
                          status status_projeto NOT NULL DEFAULT 'RASCUNHO',
                          data_inicio DATE,
                          data_prevista_termino DATE,
                          data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          data_atualizacao TIMESTAMP,
                          usuario_criador_id BIGINT NOT NULL,
                          CONSTRAINT fk_projeto_usuario_criador
                              FOREIGN KEY (usuario_criador_id) REFERENCES usuarios(id)
);

-- Tabela alunos
CREATE TABLE alunos (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL,
                        matricula VARCHAR(20) UNIQUE NOT NULL,
                        curso VARCHAR(50) NOT NULL,
                        email VARCHAR(100) NOT NULL,
                        periodo INTEGER NOT NULL CHECK (periodo >= 1 AND periodo <= 12),
                        data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        data_atualizacao TIMESTAMP,
                        usuario_criador_id BIGINT NOT NULL,
                        ativo BOOLEAN NOT NULL DEFAULT true,
                        CONSTRAINT fk_aluno_usuario_criador
                            FOREIGN KEY (usuario_criador_id) REFERENCES usuarios(id)
);

-- Tabela aluno_projeto (Relacionamento muitos-para-muitos)
CREATE TABLE aluno_projeto (
                               id BIGSERIAL PRIMARY KEY,
                               aluno_id BIGINT NOT NULL,
                               projeto_id BIGINT NOT NULL,
                               funcao VARCHAR(50) NOT NULL,
                               data_vinculacao DATE NOT NULL DEFAULT CURRENT_DATE,
                               data_desvinculacao DATE,
                               ativo BOOLEAN NOT NULL DEFAULT true,
                               data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               data_atualizacao TIMESTAMP,
                               usuario_criador_id BIGINT NOT NULL,
                               CONSTRAINT fk_aluno_projeto_aluno
                                   FOREIGN KEY (aluno_id) REFERENCES alunos(id),
                               CONSTRAINT fk_aluno_projeto_projeto
                                   FOREIGN KEY (projeto_id) REFERENCES projetos(id),
                               CONSTRAINT fk_aluno_projeto_usuario
                                   FOREIGN KEY (usuario_criador_id) REFERENCES usuarios(id),
                               CONSTRAINT uk_aluno_projeto_unico
                                   UNIQUE (aluno_id, projeto_id)
);

-- Tabela log_alteracoes (Auditoria)
CREATE TABLE log_alteracoes (
                                id BIGSERIAL PRIMARY KEY,
                                tabela_afetada VARCHAR(50) NOT NULL,
                                id_registro_afetado BIGINT NOT NULL,
                                acao VARCHAR(20) NOT NULL CHECK (acao IN ('CREATE', 'UPDATE', 'DELETE')),
                                valores_antigos TEXT,
                                valores_novos TEXT,
                                usuario_id BIGINT NOT NULL,
                                data_alteracao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                ip_requisicao VARCHAR(45),
                                CONSTRAINT fk_log_alteracoes_usuario
                                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);


-- Criação de Índices

-- Para usuarios
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_perfil ON usuarios(perfil);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

-- Para projetos
CREATE INDEX idx_projetos_status ON projetos(status);
CREATE INDEX idx_projetos_area ON projetos(area);
CREATE INDEX idx_projetos_usuario_criador ON projetos(usuario_criador_id);
CREATE INDEX idx_projetos_data_prevista_termino ON projetos(data_prevista_termino);

-- Para alunos
CREATE INDEX idx_alunos_matricula ON alunos(matricula);
CREATE INDEX idx_alunos_email ON alunos(email);
CREATE INDEX idx_alunos_curso ON alunos(curso);
CREATE INDEX idx_alunos_periodo ON alunos(periodo);
CREATE INDEX idx_alunos_ativo ON alunos(ativo);
CREATE INDEX idx_alunos_usuario_criador ON alunos(usuario_criador_id);

-- Para aluno_projeto
CREATE INDEX idx_aluno_projeto_aluno_id ON aluno_projeto(aluno_id);
CREATE INDEX idx_aluno_projeto_projeto_id ON aluno_projeto(projeto_id);
CREATE INDEX idx_aluno_projeto_ativo ON aluno_projeto(ativo);
CREATE INDEX idx_aluno_projeto_data_vinculacao ON aluno_projeto(data_vinculacao);

-- Para log_alteracoes
CREATE INDEX idx_log_alteracoes_tabela ON log_alteracoes(tabela_afetada);
CREATE INDEX idx_log_alteracoes_registro ON log_alteracoes(id_registro_afetado);
CREATE INDEX idx_log_alteracoes_usuario ON log_alteracoes(usuario_id);
CREATE INDEX idx_log_alteracoes_data ON log_alteracoes(data_alteracao);
CREATE INDEX idx_log_alteracoes_acao ON log_alteracoes(acao);


-- Criação de Validadores

CREATE OR REPLACE FUNCTION validar_vinculo_unico_ativo()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.ativo = true THEN
        IF EXISTS (
            SELECT 1 FROM aluno_projeto
            WHERE aluno_id = NEW.aluno_id
            AND projeto_id = NEW.projeto_id
            AND ativo = true
            AND id != NEW.id
        ) THEN
            RAISE EXCEPTION 'O aluno já possui um vínculo ativo com este projeto';
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_vinculo_unico_ativo
    BEFORE INSERT OR UPDATE ON aluno_projeto
                         FOR EACH ROW
                         EXECUTE FUNCTION validar_vinculo_unico_ativo();


-- Inserção de usuários ADMINs

-- Inserir APENAS usuários essenciais (sem projetos/alunos pré-criados)
INSERT INTO usuarios (nome, email, senha, perfil, ativo) VALUES
                                                             ('Administrador do Sistema', 'admin@universidade.edu', '123456', 'ADMINISTRADOR', true),
                                                             ('Coordenador de TI', 'coordenador.ti@universidade.edu', '123456', 'COORDENADOR', true),
                                                             ('Usuário Consulta', 'consulta@universidade.edu', '123456', 'CONSULTA', true);


-- Criação de Visualizações

-- View: Projetos com contagem de alunos
CREATE VIEW vw_projetos_com_alunos AS
SELECT
    p.id,
    p.titulo,
    p.orientador,
    p.area,
    p.status,
    p.data_prevista_termino,
    COUNT(ap.id) AS quantidade_alunos_vinculados
FROM projetos p
         LEFT JOIN aluno_projeto ap ON p.id = ap.projeto_id AND ap.ativo = true
GROUP BY p.id, p.titulo, p.orientador, p.area, p.status, p.data_prevista_termino;

-- View: Alunos com projetos ativos
CREATE VIEW vw_alunos_com_projetos AS
SELECT
    a.id,
    a.nome,
    a.matricula,
    a.curso,
    a.periodo,
    COUNT(ap.id) AS quantidade_projetos_ativos
FROM alunos a
         LEFT JOIN aluno_projeto ap ON a.id = ap.aluno_id AND ap.ativo = true
WHERE a.ativo = true
GROUP BY a.id, a.nome, a.matricula, a.curso, a.periodo;
