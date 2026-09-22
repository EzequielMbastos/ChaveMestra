// ============================================
// Página de Produtos
// ============================================
const ENDPOINT_PRODUTOS = '/produtos';

async function buscarProdutos() {
    try {
        const produtos = await apiGet(ENDPOINT_PRODUTOS);
        popularTabela(produtos);
    } catch (error) {
        console.error('Erro ao buscar produtos:', error);
        alert('Não foi possível carregar os produtos.');
    }
}

function popularTabela(produtos) {
    let html = '';

    for (const produto of produtos) {
        html += `
            <tr>
                <td>${produto.id}</td>
                <td>${produto.codigoCatalogo ?? '-'}</td>
                <td>${produto.nome ?? '-'}</td>
                <td>${produto.precoVenda ?? '-'}</td>
                <td>${produto.precoCusto ?? '-'}</td>
                <td>${produto.categoria?.nome ?? '-'}</td>
                <td>${produto.fornecedor?.nome ?? '-'}</td>
                <td>
                    <button class="btn btn-danger" onclick="apagarProduto(${produto.id})">
                        Remover
                    </button>
                </td>
            </tr>
        `;
    }

    document.querySelector('#table_produtos tbody').innerHTML = html;
}

async function criarProduto() {
    const produto = {
        codigoCatalogo: document.querySelector('#codigoCatalogo').value,
        nome: document.querySelector('#nome').value,
        precoVenda: Number(document.querySelector('#precoVenda').value),
        precoCusto: Number(document.querySelector('#precoCusto').value),
        categoria: { id: Number(document.querySelector('#categoriaId').value) },
        fornecedor: document.querySelector('#fornecedorId').value
            ? { id: Number(document.querySelector('#fornecedorId').value) }
            : null
    };

    try {
        await apiPost(ENDPOINT_PRODUTOS, produto);
        limparFormulario();
        fecharModal();
        buscarProdutos();
    } catch (error) {
        console.error('Erro ao criar produto:', error);
        alert('Não foi possível adicionar o produto.');
    }
}

async function apagarProduto(id) {
    if (!confirm('Tem certeza que deseja remover este produto?')) return;

    try {
        await apiDelete(`${ENDPOINT_PRODUTOS}/${id}`);
        buscarProdutos();
    } catch (error) {
        console.error('Erro ao apagar produto:', error);
        alert('Não foi possível remover o produto.');
    }
}

function limparFormulario() {
    document.querySelector('#codigoCatalogo').value = '';
    document.querySelector('#nome').value = '';
    document.querySelector('#precoVenda').value = '';
    document.querySelector('#precoCusto').value = '';
    document.querySelector('#categoriaId').value = '';
    document.querySelector('#fornecedorId').value = '';
}

function fecharModal() {
    const modalHtml = document.querySelector('#modalProduto');
    const modal = bootstrap.Modal.getOrCreateInstance(modalHtml);
    modal.hide();
}

buscarProdutos();