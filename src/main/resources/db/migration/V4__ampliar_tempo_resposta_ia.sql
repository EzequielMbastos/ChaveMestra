-- =====================================================================
-- Amplia a coluna tempo_resposta da tabela ia_interacao.
-- Originalmente criada como NUMERIC(5,2), que só comporta até 999,99.
-- O tempo de resposta da IA é medido em milissegundos e chega a 5-30 segundos
-- (5.000-30.000 ms). NUMERIC(10,2) comporta até 99.999.999,99 ms (~27 horas).
-- =====================================================================

ALTER TABLE ia_interacao
  ALTER COLUMN tempo_resposta TYPE NUMERIC(10, 2);
