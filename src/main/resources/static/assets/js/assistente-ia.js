document.addEventListener('DOMContentLoaded', () => {
  const formChat = document.getElementById('form-chat');
  const promptInput = document.getElementById('prompt-usuario');
  const chatMensagens = document.getElementById('chat-mensagens');
  const promptButtons = document.querySelectorAll('.prompt-chip');
  const submitButton = formChat.querySelector('button[type="submit"]');
  let resumoAtual = null;
  let produtosBaixoEstoque = [];

  function adicionarMensagem(texto, tipo = 'bot') {
    const elemento = document.createElement('div');
    elemento.className = `message ${tipo}`;
    elemento.textContent = texto;
    chatMensagens.appendChild(elemento);
    chatMensagens.scrollTop = chatMensagens.scrollHeight;
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

    adicionarMensagem(pergunta, 'user');
    promptInput.value = '';
    submitButton.disabled = true;

    try {
      const response = await fetch('/ia/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pergunta, tipo: 'consulta' })
      });
      const result = await response.json();
      if (!response.ok) {
        const mensagemErro = typeof result === 'object'
          ? Object.values(result).join(' ')
          : String(result);
        throw new Error(mensagemErro || `Erro HTTP ${response.status}`);
      }

      adicionarMensagem(result.resposta || 'A IA não retornou uma resposta.', 'bot');
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

  promptButtons.forEach((button) => {
    button.addEventListener('click', async () => {
      await processarPrompt(button.dataset.prompt);
    });
  });

  carregarResumoLoja();
});
