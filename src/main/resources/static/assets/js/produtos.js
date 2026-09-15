document.addEventListener('DOMContentLoaded', async () => {

  const tbody = document.getElementById('produtos-tbody');
  const buscaInput = document.getElementById('produto-busca');

  const formProduto = document.getElementById('formProduto');
  const modalProduto = document.getElementById('modalProduto');

  const selectCategoria = document.getElementById('produto-categoria');

  const formCategoria = document.getElementById('formCategoria');
  const modalCategoria = document.getElementById('modalCategoria');

  /*
   * ============================================================
   * CATEGORIAS
   * ============================================================
   */

  async function carregarCategorias() {

    const categorias = await apiGet('/categorias');

    selectCategoria.innerHTML = `
      <option value="">Selecione uma categoria</option>
    `;

    categorias.forEach(categoria => {

      const option = document.createElement('option');

      option.value = categoria.id;
      option.textContent = categoria.nome;

      selectCategoria.appendChild(option);
    });
  }


  /*
   * ============================================================
   * PRODUTOS
   * ============================================================
   */

  async function carregarProdutos() {

    const produtos = await apiGet('/produtos');

    const texto = (buscaInput.value || '').toLowerCase();

    const filtrados = produtos.filter(produto => {

      const nome = (produto.nome || '').toLowerCase();

      const codigo = String(
          produto.codigoCatalogo || ''
      ).toLowerCase();

      return nome.includes(texto) || codigo.includes(texto);
    });


    tbody.innerHTML = filtrados.map(produto => {

      const categoriaNome =
          produto.categoria?.nome || 'Sem categoria';

      return `
        <tr>

          <td>
            ${produto.codigoCatalogo}
          </td>

          <td>
            ${produto.nome}
          </td>

          <td>
            ${categoriaNome}
          </td>

          <td>
            ${formatarMoeda(produto.precoVenda)}
          </td>

          <td class="text-end">

            <button
              class="btn btn-sm btn-outline-primary me-2"
              data-editar="${produto.id}">
              <i class="bi bi-pencil"></i>
            </button>

            <button
              class="btn btn-sm btn-outline-danger"
              data-excluir="${produto.id}">
              <i class="bi bi-trash"></i>
            </button>

          </td>

        </tr>
      `;

    }).join('');


    /*
     * ========================================================
     * EDITAR
     * ========================================================
     */

    document.querySelectorAll('[data-editar]')
        .forEach(botao => {

          botao.addEventListener('click', async () => {

            const id = Number(botao.dataset.editar);

            const produto = filtrados.find(
                item => Number(item.id) === id
            );

            if (!produto) {
              return;
            }


            document.getElementById('produto-id').value =
                produto.id;

            document.getElementById('produto-codigo').value =
                produto.codigoCatalogo;

            document.getElementById('produto-nome').value =
                produto.nome;

            document.getElementById('produto-preco').value =
                produto.precoVenda;


            /*
             * Seleciona a categoria do produto
             */

            if (produto.categoria) {

              selectCategoria.value =
                  produto.categoria.id;

            } else {

              selectCategoria.value = '';

            }


            bootstrap.Modal
                .getOrCreateInstance(modalProduto)
                .show();

          });

        });


    /*
     * ========================================================
     * EXCLUIR
     * ========================================================
     */

    document.querySelectorAll('[data-excluir]')
        .forEach(botao => {

          botao.addEventListener('click', async () => {

            const id = Number(botao.dataset.excluir);

            if (!confirm('Deseja excluir este produto?')) {
              return;
            }

            await apiDelete(`/produtos/${id}`);

            mostrarToast(
                'Produto excluído com sucesso!',
                'success'
            );

            await carregarProdutos();

          });

        });

  }


  /*
   * ============================================================
   * BUSCA
   * ============================================================
   */

  buscaInput.addEventListener(
      'input',
      carregarProdutos
  );


  /*
   * ============================================================
   * CADASTRAR / EDITAR PRODUTO
   * ============================================================
   */

  formProduto.addEventListener(
      'submit',
      async (event) => {

        event.preventDefault();


        const id =
            document.getElementById('produto-id').value;

        const codigoCatalogo =
            document
                .getElementById('produto-codigo')
                .value
                .trim();

        const nome =
            document
                .getElementById('produto-nome')
                .value
                .trim();

        const precoVenda =
            Number(
                document
                    .getElementById('produto-preco')
                    .value
            );

        const categoriaId =
            Number(selectCategoria.value);


        /*
         * ========================================================
         * VALIDAÇÃO
         * ========================================================
         */

        if (!codigoCatalogo) {

          mostrarToast(
              'Informe o código do produto.',
              'warning'
          );

          return;
        }


        if (!nome) {

          mostrarToast(
              'Informe o nome do produto.',
              'warning'
          );

          return;
        }


        if (!precoVenda || precoVenda <= 0) {

          mostrarToast(
              'Informe um preço de venda válido.',
              'warning'
          );

          return;
        }


        if (!categoriaId) {

          mostrarToast(
              'Selecione uma categoria.',
              'warning'
          );

          return;
        }


        /*
         * ========================================================
         * PAYLOAD
         * ========================================================
         *
         * O backend espera:
         *
         * categoria: {
         *     id: 3
         * }
         *
         */

        const payload = {

          codigoCatalogo: codigoCatalogo,

          nome: nome,

          precoVenda: precoVenda,

          precoCusto: 0,

          categoria: {
            id: categoriaId
          }

        };


        /*
         * ========================================================
         * ENVIO
         * ========================================================
         */

        if (id) {

          await apiPut(
              `/produtos/${id}`,
              payload
          );

          mostrarToast(
              'Produto atualizado.',
              'success'
          );

        } else {

          await apiPost(
              '/produtos',
              payload
          );

          mostrarToast(
              'Produto cadastrado.',
              'success'
          );

        }


        /*
         * Fecha modal
         */

        bootstrap.Modal
            .getInstance(modalProduto)
            .hide();


        /*
         * Limpa formulário
         */

        formProduto.reset();

        document.getElementById('produto-id').value = '';


        /*
         * Atualiza tabela
         */

        await carregarProdutos();

      }
  );


  /*
   * ============================================================
   * MODAL PRODUTO FECHADO
   * ============================================================
   */

  modalProduto.addEventListener(
      'hidden.bs.modal',
      () => {

        formProduto.reset();

        document.getElementById('produto-id').value = '';

        selectCategoria.value = '';

      }
  );


  /*
   * ============================================================
   * ABRIR MODAL NOVA CATEGORIA
   * ============================================================
   */

  document
      .getElementById('btnNovaCategoria')
      .addEventListener('click', () => {

        formCategoria.reset();

        bootstrap.Modal
            .getOrCreateInstance(modalCategoria)
            .show();

      });


  /*
   * ============================================================
   * CADASTRAR CATEGORIA
   * ============================================================
   */

  formCategoria.addEventListener(
      'submit',
      async (event) => {

        event.preventDefault();


        const nome =
            document
                .getElementById('categoria-nome')
                .value
                .trim();


        const descricao =
            document
                .getElementById('categoria-descricao')
                .value
                .trim();


        if (!nome) {

          mostrarToast(
              'Informe o nome da categoria.',
              'warning'
          );

          return;
        }


        const payload = {

          nome: nome,

          descricao: descricao

        };


        /*
         * Salva no PostgreSQL através do backend
         */

        const novaCategoria =
            await apiPost(
                '/categorias',
                payload
            );


        /*
         * Fecha modal da categoria
         */

        bootstrap.Modal
            .getInstance(modalCategoria)
            .hide();


        /*
         * Recarrega categorias
         */

        await carregarCategorias();


        /*
         * Seleciona automaticamente
         * a categoria recém criada
         */

        selectCategoria.value =
            novaCategoria.id;


        mostrarToast(
            'Categoria criada com sucesso!',
            'success'
        );

      }
  );


  /*
   * ============================================================
   * MODAL CATEGORIA FECHADO
   * ============================================================
   */

  modalCategoria.addEventListener(
      'hidden.bs.modal',
      () => {

        formCategoria.reset();

      }
  );


  /*
   * ============================================================
   * INICIALIZAÇÃO
   * ============================================================
   */

  await carregarCategorias();

  await carregarProdutos();

});