-- Adiciona avaliação do usuário para cada interação de IA.
-- Permite identificar respostas ruins para refinamento do prompt.

ALTER TABLE ia_interacao
    ADD COLUMN IF NOT EXISTS avaliacao INTEGER,
    ADD COLUMN IF NOT EXISTS comentario_avaliacao VARCHAR(500),
    ADD COLUMN IF NOT EXISTS avaliado_em TIMESTAMP;

ALTER TABLE ia_interacao
    DROP CONSTRAINT IF EXISTS ia_interacao_avaliacao_check;

ALTER TABLE ia_interacao
    ADD CONSTRAINT ia_interacao_avaliacao_check
    CHECK (avaliacao IS NULL OR (avaliacao BETWEEN 1 AND 5));

CREATE INDEX IF NOT EXISTS idx_ia_interacao_avaliacao
    ON ia_interacao (avaliacao)
    WHERE avaliacao IS NOT NULL;
