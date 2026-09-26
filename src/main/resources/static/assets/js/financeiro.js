document.addEventListener('DOMContentLoaded', async () => {
  const tbody = document.getElementById('financeiro-tbody');
  const form = document.getElementById('formFinanceiro');
  const modal = document.getElementById('modalFinanceiro');
  const campoTipo = document.getElementById('financeiro-tipo');
  const campoPessoa = document.getElementById('financeiro-pessoa');
  const campoCategoria = document.getElementById('financeiro-categoria');
  let categoriasFinanceiras = [];

  async function carregarCategorias() {
    categoriasFinanceiras = await apiGet('/categorias-financeiras');
    atualizarCategoriasDisponiveis();
  }

  function atualizarCategoriasDisponiveis() {
    const tipoSelecionado = campoTipo.value.toLowerCase();
    const categoriasFiltradas = categoriasFinanceiras.filter(
      categoria => categoria.tipo.toLowerCase() === tipoSelecionado
    );

    campoCategoria.innerHTML = '<option value="">Selecione uma categoria</option>' +
      categoriasFiltradas
        .map(categoria => `<option value="${categoria.id}">${categoria.nome}</option>`)
        .join('');
  }

  function atualizarPessoaObrigatoria() {
    const tipo = campoTipo.value;
    const obrigatorio = tipo === 'SAIDA';
    campoPessoa.disabled = !obrigatorio;
    campoPessoa.required = obrigatorio;
    campoPessoa.value = obrigatorio ? (campoPessoa.value || 'CNPJ') : '';
    campoPessoa.closest('.mb-3').style.display = obrigatorio ? 'block' : 'none';
  }

  campoTipo.addEventListener('change', () => {
    atualizarPessoaObrigatoria();
    atualizarCategoriasDisponiveis();
  });

  async function carregarFinanceiro() {
    const itens = await apiGet('/movimentos-financeiros');
    const entradas = itens.filter(item => item.tipo?.toUpperCase() === 'ENTRADA').reduce((soma, item) => soma + Number(item.valor || 0), 0);
    const saidas = itens.filter(item => item.tipo?.toUpperCase() === 'SAIDA').reduce((soma, item) => soma + Number(item.valor || 0), 0);
    const saldo = entradas - saidas;
    const pendentes = itens.filter(item => item.status?.toUpperCase() === 'PENDENTE').length;

    document.getElementById('total-entradas').textContent = formatarMoeda(entradas);
    document.getElementById('total-saidas').textContent = formatarMoeda(saidas);
    document.getElementById('saldo-financeiro').textContent = formatarMoeda(saldo);
    document.getElementById('pendencias-financeiro').textContent = pendentes;

    tbody.innerHTML = itens.map(item => `
      <tr>
        <td>
          <span class="badge ${item.tipo?.toUpperCase() === 'ENTRADA' ? 'bg-success' : 'bg-danger'}">${item.tipo?.toUpperCase() === 'ENTRADA' ? 'Entrada' : 'Saída'}</span>
        </td>
        <td>${item.categoriaFinanceiraNome || '-'}</td>
        <td>${item.nome || '-'}</td>
        <td>${item.descricao}</td>
        <td>${item.vencimento ? new Date(`${item.vencimento}T00:00:00`).toLocaleDateString('pt-BR') : '-'}</td>
        <td class="fw-bold ${item.tipo?.toUpperCase() === 'ENTRADA' ? 'text-success' : 'text-danger'}">${formatarMoeda(item.valor)}</td>
        <td><span class="badge ${item.status?.toUpperCase() === 'PAGO' ? 'bg-success' : 'bg-warning text-dark'}">${item.status}</span></td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary me-2" data-editar="${item.id}"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger" data-excluir="${item.id}"><i class="bi bi-trash"></i></button>
        </td>
      </tr>
    `).join('');

    document.querySelectorAll('[data-editar]').forEach(botao => {
      botao.addEventListener('click', async () => {
        const item = itens.find(i => Number(i.id) === Number(botao.dataset.editar));
        if (!item) return;

        document.getElementById('financeiro-id').value = item.id;
        document.getElementById('financeiro-tipo').value = item.tipo;
        atualizarCategoriasDisponiveis();
        document.getElementById('financeiro-categoria').value = item.categoriaFinanceiraId;
        document.getElementById('financeiro-pessoa').value = item.nome || '';
        document.getElementById('financeiro-descricao').value = item.descricao;
        document.getElementById('financeiro-valor').value = item.valor;
        document.getElementById('financeiro-data').value = item.vencimento;
        document.getElementById('financeiro-status').value = item.status;

        atualizarPessoaObrigatoria();
        bootstrap.Modal.getOrCreateInstance(modal).show();
      });
    });

    document.querySelectorAll('[data-excluir]').forEach(botao => {
      botao.addEventListener('click', async () => {
        const id = Number(botao.dataset.excluir);
        if (!confirm('Deseja excluir esta movimentação?')) return;

        await apiDelete(`/movimentos-financeiros/${id}`);
        mostrarToast('Movimentação excluída.', 'success');
        carregarFinanceiro();
      });
    });
  }

  form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const id = document.getElementById('financeiro-id').value;
    const tipo = document.getElementById('financeiro-tipo').value;
    const nome = tipo === 'SAIDA' ? document.getElementById('financeiro-pessoa').value : '';
    const payload = {
      categoriaFinanceiraId: Number(document.getElementById('financeiro-categoria').value),
      nome,
      descricao: document.getElementById('financeiro-descricao').value.trim(),
      valor: Number(document.getElementById('financeiro-valor').value),
      vencimento: document.getElementById('financeiro-data').value,
      status: document.getElementById('financeiro-status').value
    };

    if (tipo === 'SAIDA' && !payload.nome) {
      mostrarToast('Para saídas, informe se foi CNPJ ou CPF.', 'warning');
      return;
    }

    if (!payload.categoriaFinanceiraId || !payload.descricao || payload.valor <= 0 || !payload.vencimento) {
      mostrarToast('Preencha todos os campos corretamente.', 'warning');
      return;
    }

    if (id) {
      await apiPut(`/movimentos-financeiros/${id}`, payload);
      mostrarToast('Movimentação atualizada.', 'success');
    } else {
      await apiPost('/movimentos-financeiros', payload);
      mostrarToast('Movimentação cadastrada.', 'success');
    }

    bootstrap.Modal.getInstance(modal).hide();
    form.reset();
    atualizarPessoaObrigatoria();
    atualizarCategoriasDisponiveis();
    await carregarCategorias();
    carregarFinanceiro();
  });

  modal.addEventListener('hidden.bs.modal', () => {
    form.reset();
    document.getElementById('financeiro-id').value = '';
    atualizarPessoaObrigatoria();
    atualizarCategoriasDisponiveis();
  });

  atualizarPessoaObrigatoria();
  await carregarCategorias();
  carregarFinanceiro();
});
