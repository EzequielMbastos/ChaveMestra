-- Adiciona coluna 'ativo' para soft delete de produtos.
-- Produtos com ativo=false não aparecem nas listagens, mas permanecem no banco
-- para preservar o histórico de atendimentos e estoque.

ALTER TABLE produto
  ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

-- Índice para acelerar a filtragem de produtos ativos
CREATE INDEX idx_produto_ativo ON produto(ativo) WHERE ativo = TRUE;
