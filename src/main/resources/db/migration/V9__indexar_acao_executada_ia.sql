ALTER TABLE ia_interacao
    ADD COLUMN IF NOT EXISTS acao_executada VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_ia_interacao_acao
    ON ia_interacao (acao_executada)
    WHERE acao_executada IS NOT NULL;
