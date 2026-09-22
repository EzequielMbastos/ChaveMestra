// Endpoints do backend
const API = {
  clientes:              '/clientes',
  fornecedores:          '/fornecedores',
  categorias:            '/categorias',
  categoriasFinanceiras: '/categoria-financeira',  // singular!
  produtos:              '/produtos',
  servicos:              '/servicos',
  estoques:              '/estoques',
  movimentosFinanceiros: '/movimentos-financeiros',
  atendimentos:          '/atendimentos'
};


async function apiGet(endpoint) {
  const response = await fetch(endpoint);

  if (!response.ok) {
    throw new Error(`Erro HTTP ${response.status}`);
  }

  return response.json();
}


async function apiPost(endpoint, data) {
  const response = await fetch(endpoint, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  });

  if (!response.ok) {
    throw new Error(`Erro HTTP ${response.status}`);
  }

  return response.json();
}


async function apiPut(endpoint, data) {
  const response = await fetch(endpoint, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  });

  if (!response.ok) {
    throw new Error(`Erro HTTP ${response.status}`);
  }

  return response.json();
}


async function apiDelete(endpoint) {
  const response = await fetch(endpoint, {
    method: 'DELETE'
  });

  if (!response.ok) {
    throw new Error(`Erro HTTP ${response.status}`);
  }

  const contentType = response.headers.get('content-type');

  if (contentType && contentType.includes('application/json')) {
    return response.json();
  }

  return true;
}