-- =====================================================================
-- Amplia a constraint de tipo_pergunta da tabela ia_interacao.
--
-- A constraint original aceitava apenas 'texto' e 'voz' (indicando a FORMA
-- da pergunta). O IaService agora classifica a pergunta semanticamente,
-- enviando 'consulta', 'relatorio' ou 'outro' (indicando o CONTEÚDO).
--
-- Esta migration mantém compatibilidade com o design original E aceita a
-- nova classificação semântica.
-- =====================================================================

-- Remove a constraint antiga
ALTER TABLE ia_interacao
  DROP CONSTRAINT IF EXISTS ia_interacao_tipo_pergunta_check;

-- Cria a nova constraint com os 5 valores aceitos
ALTER TABLE ia_interacao
  ADD CONSTRAINT ia_interacao_tipo_pergunta_check
  CHECK (tipo_pergunta IN ('texto', 'voz', 'consulta', 'relatorio', 'outro'));
