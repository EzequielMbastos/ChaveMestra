document.addEventListener('DOMContentLoaded', () => {
  const formChat = document.getElementById('form-chat');
  const promptInput = document.getElementById('prompt-usuario');
  const chatMensagens = document.getElementById('chat-mensagens');
  const historicoLista = document.getElementById('historico-conversas');
  const historicoVazio = document.getElementById('historico-vazio');
  const chatVazio = document.getElementById('chat-vazio');
  const novaConversaButton = document.getElementById('nova-conversa');
  const limparHistoricoButton = document.getElementById('limpar-historico');
  const promptButtons = document.querySelectorAll('.prompt-chip');
  const submitButton = formChat.querySelector('button[type="submit"]');
  const STORAGE_KEYS = {
    draft: 'chavemestra.ia.rascunho',
    lastQuestion: 'chavemestra.ia.ultimaPergunta',
    conversationId: 'chavemestra.ia.conversaAtual'
  };
  let resumoAtual = null;
  let produtosBaixoEstoque = [];
  let historico = [];
  let conversaAtualId = Number(localStorage.getItem(STORAGE_KEYS.conversationId)) || null;

  promptInput.value = localStorage.getItem(STORAGE_KEYS.draft)
    || localStorage.getItem(STORAGE_KEYS.lastQuestion)
    || '';

  function adicionarMensagem(texto, tipo = 'bot') {
    chatVazio.classList.add('d-none');
    const elemento = document.createElement('div');
    elemento.className = `message ${tipo}`;
    elemento.textContent = texto;
    chatMensagens.appendChild(elemento);
    rolarChatParaBaixo();
  }

  function rolarChatParaBaixo() {
    chatMensagens.scrollTop = chatMensagens.scrollHeight;
  }

  function limparMensagens() {
    chatMensagens.querySelectorAll('.message').forEach((mensagem) => mensagem.remove());
    chatVazio.classList.remove('d-none');
  }

  function formatarData(data) {
    if (!data) return 'Data indisponível';
    const dataFormatada = new Date(data);
    return Number.isNaN(dataFormatada.getTime())
      ? 'Data indisponível'
      : dataFormatada.toLocaleString('pt-BR');
  }

  function truncarPergunta(pergunta, limite = 48) {
    const texto = String(pergunta || 'Pergunta sem texto');
    return texto.length > limite ? `${texto.slice(0, limite - 1)}…` : texto;
  }

  function renderizarHistorico() {
    historicoLista.replaceChildren();
    historicoVazio.classList.toggle('d-none', historico.length > 0);

    historico.forEach((interacao) => {
      const botao = document.createElement('button');
      botao.type = 'button';
      botao.className = 'btn btn-light border history-item';
      botao.classList.toggle('active', interacao.id === conversaAtualId);
      botao.dataset.interacaoId = interacao.id;

      const pergunta = document.createElement('span');
      pergunta.className = 'd-block fw-semibold small';
      pergunta.textContent = truncarPergunta(interacao.usuarioPergunta);

      const data = document.createElement('span');
      data.className = 'd-block text-muted';
      data.style.fontSize = '0.75rem';
      data.textContent = formatarData(interacao.dataInteracao);

      botao.append(pergunta, data);
      historicoLista.appendChild(botao);
    });
  }

  function exibirInteracao(interacao) {
    conversaAtualId = Number(interacao.id);
    localStorage.setItem(STORAGE_KEYS.conversationId, String(conversaAtualId));
    limparMensagens();
    adicionarMensagem(interacao.usuarioPergunta || '', 'user');
    adicionarMensagem(interacao.iaResposta || 'Esta interação não possui resposta registrada.', 'bot');
    renderizarHistorico();
  }

  async function carregarHistorico() {
    try {
      const response = await fetch('/ia/historico?limite=20');
      if (!response.ok) throw new Error(`Erro HTTP ${response.status}`);
      historico = await response.json();
      renderizarHistorico();

      const conversaSalva = historico.find(
        (interacao) => Number(interacao.id) === conversaAtualId
      );
      if (conversaSalva) exibirInteracao(conversaSalva);
      else if (conversaAtualId !== null) {
        conversaAtualId = null;
        localStorage.removeItem(STORAGE_KEYS.conversationId);
      }
    } catch (error) {
      console.error('Erro ao carregar histórico do assistente:', error);
      mostrarToast('Não foi possível carregar o histórico de conversas.', 'danger');
    }
  }

  function adicionarAoHistorico(interacao) {
    historico = [interacao, ...historico.filter((item) => item.id !== interacao.id)].slice(0, 20);
    renderizarHistorico();
  }

  function iniciarNovaConversa() {
    conversaAtualId = null;
    localStorage.removeItem(STORAGE_KEYS.conversationId);
    localStorage.removeItem(STORAGE_KEYS.draft);
    promptInput.value = '';
    limparMensagens();
    renderizarHistorico();
    promptInput.focus();
  }

  function deveAbrirDashboard(prompt) {
    return /(gr[aá]fico|dashboard|relat[oó]rio|visual)/i.test(prompt);
  }

  async function carregarResumoLoja() {
    try {
      const [resumo, baixoEstoque] = await Promise.all([
        apiGet('/relatorios/financeiro-resumo'),
        apiGet('/relatorios/produtos-baixo-estoque?limite=5')
      ]);
      resumoAtual = resumo;
      produtosBaixoEstoque = baixoEstoque.produtos || [];

      document.getElementById('ai-entradas').textContent = formatarMoeda(resumo.entradas || 0);
      document.getElementById('ai-saidas').textContent = formatarMoeda(resumo.saidas || 0);
      document.getElementById('ai-saldo').textContent = formatarMoeda(resumo.saldo || 0);
      document.getElementById('ai-estoque-baixo').textContent = baixoEstoque.total || 0;

      const canvas = document.getElementById('ai-chart');
      if (canvas && window.aiChartInstance) {
        window.aiChartInstance.destroy();
      }
      if (canvas) {
        window.aiChartInstance = new Chart(canvas, {
          type: 'bar',
          data: {
            labels: ['Entradas', 'Saídas', 'Saldo', 'Estoque baixo'],
            datasets: [{
              label: 'Resumo da loja',
              data: [resumo.entradas || 0, resumo.saidas || 0, resumo.saldo || 0, baixoEstoque.total || 0],
              backgroundColor: ['#1E3A5F', '#dc3545', '#DAA520', '#f4b740'],
              borderRadius: 8
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
          }
        });
      }
    } catch (error) {
      console.error('Erro ao carregar resumo da loja:', error);
      mostrarToast('Não foi possível carregar o resumo da loja.', 'danger');
    }
  }

  function abrirDashboardModal() {
    if (!resumoAtual) {
      mostrarToast('O resumo da loja ainda não está disponível.', 'warning');
      return;
    }
    const dados = [
      resumoAtual.entradas || 0,
      resumoAtual.saidas || 0,
      resumoAtual.saldo || 0,
      produtosBaixoEstoque.length
    ];
    const resumo = document.getElementById('dashboard-ia-resumo');
    resumo.innerHTML = `
      <div class="row g-2 text-center">
        <div class="col-md-3"><span class="badge bg-success-subtle text-success-emphasis px-3 py-2">Entradas: ${formatarMoeda(dados[0])}</span></div>
        <div class="col-md-3"><span class="badge bg-danger-subtle text-danger-emphasis px-3 py-2">Saídas: ${formatarMoeda(dados[1])}</span></div>
        <div class="col-md-3"><span class="badge bg-primary-subtle text-primary-emphasis px-3 py-2">Saldo: ${formatarMoeda(dados[2])}</span></div>
        <div class="col-md-3"><span class="badge bg-warning-subtle text-warning-emphasis px-3 py-2">Estoque baixo: ${dados[3]}</span></div>
      </div>
    `;

    const canvas = document.getElementById('dashboard-ia-chart');
    if (window.dashboardChartInstance) {
      window.dashboardChartInstance.destroy();
    }
    window.dashboardChartInstance = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: ['Entradas', 'Saídas', 'Saldo', 'Estoque baixo'],
        datasets: [{
          label: 'Dashboard da loja',
          data: dados,
          backgroundColor: ['#198754', '#dc3545', '#0d6efd', '#f4b740'],
          borderRadius: 8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
      }
    });

    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalDashboardIA')).show();
  }

  async function processarPrompt(prompt) {
    const pergunta = String(prompt || '').trim();
    if (!pergunta) return;

    localStorage.setItem(STORAGE_KEYS.lastQuestion, pergunta);
    localStorage.setItem(STORAGE_KEYS.draft, pergunta);
    adicionarMensagem(pergunta, 'user');
    promptInput.value = '';
    submitButton.disabled = true;

    try {
      const payload = { pergunta, tipo: 'consulta' };
      if (conversaAtualId !== null) payload.interacaoIdContexto = conversaAtualId;
      const response = await fetch('/ia/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const result = await response.json();
      if (!response.ok) {
        const mensagemErro = typeof result === 'object'
          ? Object.values(result).join(' ')
          : String(result);
        throw new Error(mensagemErro || `Erro HTTP ${response.status}`);
      }

      adicionarMensagem(result.resposta || 'A IA não retornou uma resposta.', 'bot');
      if (result.interacaoId != null) {
        conversaAtualId = Number(result.interacaoId);
        localStorage.setItem(STORAGE_KEYS.conversationId, String(conversaAtualId));
        adicionarAoHistorico({
          id: conversaAtualId,
          usuarioPergunta: pergunta,
          iaResposta: result.resposta || 'A IA não retornou uma resposta.',
          dataInteracao: result.dataInteracao
        });
      }
      localStorage.removeItem(STORAGE_KEYS.draft);
      if (deveAbrirDashboard(pergunta)) {
        abrirDashboardModal();
      }
    } catch (error) {
      console.error('Erro ao consultar o assistente:', error);
      const mensagem = error.message || 'Não foi possível consultar o assistente.';
      adicionarMensagem(mensagem, 'system');
      mostrarToast(mensagem, 'danger');
    } finally {
      submitButton.disabled = false;
      promptInput.focus();
    }
  }

  formChat.addEventListener('submit', async (event) => {
    event.preventDefault();
    await processarPrompt(promptInput.value);
  });

  promptInput.addEventListener('input', () => {
    localStorage.setItem(STORAGE_KEYS.draft, promptInput.value);
  });

  historicoLista.addEventListener('click', (event) => {
    const botao = event.target.closest('[data-interacao-id]');
    if (!botao) return;
    const interacao = historico.find(
      (item) => Number(item.id) === Number(botao.dataset.interacaoId)
    );
    if (interacao) exibirInteracao(interacao);
  });

  novaConversaButton.addEventListener('click', iniciarNovaConversa);

  limparHistoricoButton.addEventListener('click', async () => {
    if (!window.confirm('Tem certeza de que deseja excluir permanentemente todo o histórico de conversas?')) {
      return;
    }
    limparHistoricoButton.disabled = true;
    try {
      const response = await fetch('/ia/historico', { method: 'DELETE' });
      if (!response.ok) throw new Error(`Erro HTTP ${response.status}`);
      historico = [];
      iniciarNovaConversa();
      mostrarToast('Histórico de conversas excluído.', 'success');
    } catch (error) {
      console.error('Erro ao excluir histórico do assistente:', error);
      mostrarToast('Não foi possível excluir o histórico.', 'danger');
    } finally {
      limparHistoricoButton.disabled = false;
    }
  });

  promptButtons.forEach((button) => {
    button.addEventListener('click', async () => {
      await processarPrompt(button.dataset.prompt);
    });
  });

  carregarHistorico();
  carregarResumoLoja();
});
