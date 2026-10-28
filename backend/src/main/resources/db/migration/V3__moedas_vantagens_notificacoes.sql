CREATE TABLE departamento (
    id UUID PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    instituicao_id BIGINT NOT NULL REFERENCES instituicao(id),
    UNIQUE (nome, instituicao_id),
    UNIQUE (id, instituicao_id)
);

CREATE TABLE professor (
    id UUID PRIMARY KEY REFERENCES usuario(id),
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE CHECK (cpf ~ '^[0-9]{11}$'),
    departamento_id UUID NOT NULL,
    instituicao_id BIGINT NOT NULL REFERENCES instituicao(id),
    FOREIGN KEY (departamento_id, instituicao_id) REFERENCES departamento(id, instituicao_id)
);

CREATE TABLE vantagem (
    id UUID PRIMARY KEY,
    empresa_id UUID NOT NULL REFERENCES empresa_parceira(id),
    descricao VARCHAR(2000) NOT NULL CHECK (btrim(descricao) <> ''),
    custo BIGINT NOT NULL CHECK (custo > 0),
    foto BYTEA NOT NULL CHECK (octet_length(foto) BETWEEN 1 AND 2097152),
    tipo_foto VARCHAR(40) NOT NULL CHECK (tipo_foto IN ('image/png', 'image/jpeg'))
);
CREATE INDEX idx_vantagem_empresa ON vantagem(empresa_id);

CREATE TABLE movimentacao (
    id UUID PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('CREDITO_SEMESTRAL', 'RECONHECIMENTO', 'RESGATE')),
    quantidade BIGINT NOT NULL CHECK (quantidade > 0),
    data_hora TIMESTAMPTZ NOT NULL,
    origem_id UUID REFERENCES conta(id),
    destino_id UUID REFERENCES conta(id),
    solicitante_id UUID NOT NULL REFERENCES usuario(id),
    requisicao_id UUID NOT NULL,
    motivo VARCHAR(2000),
    vantagem_id UUID REFERENCES vantagem(id),
    descricao_vantagem VARCHAR(2000),
    cupom_codigo VARCHAR(40) UNIQUE,
    ano INTEGER,
    semestre INTEGER CHECK (semestre IN (1, 2)),
    UNIQUE (solicitante_id, requisicao_id),
    CHECK (
      (tipo = 'CREDITO_SEMESTRAL' AND origem_id IS NULL AND destino_id IS NOT NULL
        AND ano IS NOT NULL AND semestre IS NOT NULL AND motivo IS NULL
        AND vantagem_id IS NULL AND descricao_vantagem IS NULL AND cupom_codigo IS NULL)
      OR (tipo = 'RECONHECIMENTO' AND origem_id IS NOT NULL AND destino_id IS NOT NULL
        AND origem_id <> destino_id AND motivo IS NOT NULL AND btrim(motivo) <> ''
        AND vantagem_id IS NULL AND descricao_vantagem IS NULL AND cupom_codigo IS NULL
        AND ano IS NULL AND semestre IS NULL)
      OR (tipo = 'RESGATE' AND origem_id IS NOT NULL AND destino_id IS NULL
        AND vantagem_id IS NOT NULL AND descricao_vantagem IS NOT NULL AND cupom_codigo IS NOT NULL
        AND motivo IS NULL AND ano IS NULL AND semestre IS NULL)
    )
);
CREATE UNIQUE INDEX uq_credito_professor_semestre ON movimentacao(destino_id, ano, semestre)
    WHERE tipo = 'CREDITO_SEMESTRAL';
CREATE INDEX idx_movimentacao_origem ON movimentacao(origem_id, data_hora DESC);
CREATE INDEX idx_movimentacao_destino ON movimentacao(destino_id, data_hora DESC);

CREATE TABLE notificacao (
    id UUID PRIMARY KEY,
    movimentacao_id UUID NOT NULL REFERENCES movimentacao(id),
    destinatario VARCHAR(254) NOT NULL,
    assunto VARCHAR(200) NOT NULL,
    conteudo TEXT NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDENTE' CHECK (estado IN ('PENDENTE', 'ENVIADA', 'FALHA')),
    tentativas INTEGER NOT NULL DEFAULT 0 CHECK (tentativas >= 0),
    proxima_tentativa TIMESTAMPTZ NOT NULL,
    ultimo_erro VARCHAR(300)
);
CREATE INDEX idx_notificacao_pendente ON notificacao(proxima_tentativa) WHERE estado <> 'ENVIADA';
