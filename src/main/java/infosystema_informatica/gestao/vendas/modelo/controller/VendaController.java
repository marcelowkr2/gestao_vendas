// Importações necessárias para o funcionamento do controlador de vendas
package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.dao.*;
import infosystema_informatica.gestao.vendas.modelo.entidades.*;
import infosystema_informatica.gestao.vendas.modelo.exception.NegocioException;
import infosystema_informatica.gestao.vendas.modelo.util.*;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import java.awt.Color;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

// Classe controladora para gerenciar a lógica das vendas
public class VendaController extends AbstractMouseListener implements ActionListener, KeyListener {

    // Declaração de variáveis que representam o Dashboard e os DAOs necessários para a venda
    private Dashboard dashboard;
    private ProdutoDao produtoDao;
    private List<Produto> produtos;
    private CategoriaDao categoriaDao;
    private HashMap<String, VendaDetalhes> vendaDetalhesCesto;
    private VendaRegistroTableModel vendaRegistroTableModel;
    private Produto produto;
    private String nomeDoProduto;
    private AutenticacaoDao autenticacaoDao;
    private ClienteDao clienteDao;
    private UsuarioDao usuarioDao;
    private VendaDao vendaDao;
    private List<VendaDetalhes> vendaDetalhes;
    private VendaTableModel vendaTableModel;

    // Construtor da classe que inicializa o controlador com o dashboard e DAOs
    public VendaController(Dashboard dashboard) {
        this.dashboard = dashboard;
        produtoDao = new ProdutoDao();
        produtos = produtoDao.todosProdutos();
        inicializarCategoria();
        this.vendaDetalhesCesto = new HashMap<>();
        actualizarCesto(vendaDetalhesCesto);
        autenticacaoDao = new AutenticacaoDao();
        usuarioDao = new UsuarioDao();
        clienteDao = new ClienteDao();
        vendaDao = new VendaDao();
        actualizarTabelaVenda();
    }

    // Método para lidar com ações de botões ou outros componentes
    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase(); // Pega o comando da ação

        // Verifica qual ação foi disparada e chama o método correspondente
        switch(accao) {
            case "adicionar": mostrarTelaRegistro(); break;
            case "comboboxvendacategoria": pesquisarProdutoPeloCategoria(); break;
            case "comboboxvendaproduto": selecionarProdutoNaComboBox(); break;
            case "adicionarnocesto": adicionarProdutoNoCesto(); break;
            case "checkboxvendadesconto": ativaCheckBoxDesconto(); break;
            case "remover": removerProdutoNoCesto(); break;
            case "vender": vender(); break;
            case "cancelar": cancelar(); break;
            case "detalhes": detalhes(); break;
        }
    }

    // Mostra o diálogo de registro de venda
    private void mostrarTelaRegistro() {
        this.dashboard.getDialogVenda().pack();
        this.dashboard.getDialogVenda().setLocationRelativeTo(dashboard);
        this.dashboard.getDialogVenda().setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent ke) {}

    @Override
    public void keyPressed(KeyEvent ke) {}

    // Método chamado ao soltar uma tecla
    @Override
    public void keyReleased(KeyEvent ke) {
        String pesquisar = this.dashboard.getTxtVendaPesquisarProduto().getText(); // Pega o texto do campo de pesquisa
        Optional<Produto> produtosTemp = produtos.stream()
                .filter((p) -> p.getId().toString().equals(pesquisar) || p.getNome().equalsIgnoreCase(pesquisar)) // Filtra produtos com base no ID ou nome
                .findFirst();

        if (produtosTemp.isPresent()) {
            this.produto = produtosTemp.get(); // Se o produto for encontrado, atualiza os detalhes do produto
            detalhesDoProduto();
        } else {
            informacaoPadraoDaLabelVendaProduto(); // Se não encontrado, mostra informações padrão
        }
    }

    // Atualiza os detalhes do produto selecionado no dashboard
    private void detalhesDoProduto() {
        this.dashboard.getLabelVendaPrecoDoProduto().setText(this.produto.getPreco().toString());
        this.dashboard.getLabelVendaQuantidadeDoProduto().setText(this.produto.getQuantidade().toString());
        this.dashboard.getLabelVendaNomedeDoProduto().setText(produto.getNome());
    }

    // Define as informações padrão dos labels de produto
    private void informacaoPadraoDaLabelVendaProduto() {
        this.dashboard.getLabelVendaPrecoDoProduto().setText("0,00");
        this.dashboard.getLabelVendaQuantidadeDoProduto().setText("0");
        this.dashboard.getLabelVendaNomedeDoProduto().setText("");
        this.produto = null;
    }

    // Inicializa as categorias disponíveis para a venda
    private void inicializarCategoria() {
        categoriaDao = new CategoriaDao();

        this.dashboard.getComboBoxVendaPesquisarProdutoPelaCategoria().removeAllItems();
        this.dashboard.getComboBoxVendaPesquisarProdutoPelaCategoria().addItem("Selecione");

        inicializarProduto(); // Limpa e inicializa o combobox de produtos

        categoriaDao.todasCategorias()
                .stream()
                .forEach(c -> this.dashboard.getComboBoxVendaPesquisarProdutoPelaCategoria().addItem(c.getNome())); // Adiciona as categorias ao combobox
    }

    // Limpa e inicializa o combobox de produtos
    private void inicializarProduto() {
        this.dashboard.getComboBoVendaProduto().removeAllItems();
        this.dashboard.getComboBoVendaProduto().addItem("Selecione");
    }

    // Pesquisa produtos com base na categoria selecionada
    private void pesquisarProdutoPeloCategoria() {
        inicializarProduto();
        String categoria = this.dashboard.getComboBoxVendaPesquisarProdutoPelaCategoria().getSelectedItem().toString();

        if(!categoria.equals("Selecione")) {
            List<Produto> produtosTemp = produtoDao.buscarProdutosPeloCategoria(categoria);
            produtosTemp.stream().forEach(p -> this.dashboard.getComboBoVendaProduto().addItem(p.getNome())); // Adiciona produtos ao combobox
        }
    }

    // Seleciona um produto no combobox e atualiza seus detalhes
    private void selecionarProdutoNaComboBox() {
        if(this.dashboard.getComboBoVendaProduto().getSelectedIndex() > 0) {
            String produtoSelecionado = this.dashboard.getComboBoVendaProduto().getSelectedItem().toString();
            this.produto = produtoDao.buscarProdutoPeloNome(produtoSelecionado);
            if(produto != null)
                detalhesDoProduto(); // Atualiza os detalhes do produto selecionado
        }
    }

    // Valida se um campo obrigatório está preenchido
    private void validacaoDoCampo(String campo, String nomeDaVariavel) {
        if(campo.isEmpty()) {
            String mensagem = String.format("Deves preencher o campo %s", nomeDaVariavel);
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem); // Lança exceção em caso de erro
        }
    }

    // Valida se a quantidade é maior que zero
    private void validacaoDaQuantidade(Integer quantidade) {
        if(quantidade <= 0) {
            String mensagem = String.format("Quantidade nao pode ser um numero negativo ou menor que zero");
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    // Verifica se a quantidade solicitada é maior que a disponível
    private void validacaoDoQuantidadeDoProdutoMaiorQueSolicitado(int quantidade) {
        if(this.produto.getQuantidade() < quantidade) {
            mensagemNaTela("Quantidade indisponivel", Color.RED);
            throw new NegocioException("Quantidade indisponivel");
        }
    }

    // Verifica se o valor digitado é um número válido e retorna como Integer
    private Integer validacaoDaQuantidadeSeENumero(String quantidadeString) {
        try {
            return Integer.valueOf(quantidadeString);
        } catch (NumberFormatException e) {
            mensagemNaTela("Deves inserir apenas numero.", Color.RED);
            return 0;
        }
    }

    // Verifica se o valor digitado é um número válido e retorna como BigDecimal
    private BigDecimal validacaoDaPrecoSeENumero(String precoString) {
        try {
            return new BigDecimal(precoString);
        } catch (Exception e) {
            mensagemNaTela("Deves inserir apenas numero.", Color.RED);
            return BigDecimal.ONE;
        }
    }

    // Exibe uma mensagem na tela
    private void mensagemNaTela(String mensagem, Color color) {
        this.dashboard.getLabelVendaMensagem().setBackground(color);
        this.dashboard.getLabelVendaMensagem().setText(mensagem);
    }

    // Adiciona um produto no cesto (carrinho) de compras
    private void adicionarProdutoNoCesto() {
        if(produto != null) {
            int quantidadeExistente = 0;

            // Verifica se o produto já está no cesto e recupera a quantidade existente
            if(vendaDetalhesCesto.containsKey(this.produto.getNome())) {
                quantidadeExistente = vendaDetalhesCesto.get(this.produto.getNome()).getQuantidade();
            }

            VendaDetalhes vendaDetalhesTemp  = new VendaDetalhes();
            String quantidadeString = this.dashboard.getTxtVendaQuantidadeDoProduto().getText();

            // Validações para garantir que a quantidade e o produto sejam válidos
            validacaoDoCampo(quantidadeString, "Quantidade");
            Integer quantidade = validacaoDaQuantidadeSeENumero(quantidadeString);
            validacaoDaQuantidade(quantidade);
            validacaoDoQuantidadeDoProdutoMaiorQueSolicitado(quantidade + quantidadeExistente);

            vendaDetalhesTemp.setProduto(this.produto);
            vendaDetalhesTemp.setQuantidade(quantidade + quantidadeExistente);
            vendaDetalhesTemp.setNomeDoProduto(this.produto.getNome());

            BigDecimal valorDaCompra = produto.getPreco().multiply(new BigDecimal(quantidade));
            BigDecimal valorDaCompraTotal = vendaDetalhesTemp.getValorDaCompra() != null ? vendaDetalhesTemp.getValorDaCompra().add(valorDaCompra) : valorDaCompra;
            vendaDetalhesTemp.setValorDaCompra(valorDaCompraTotal);
            vendaDetalhesTemp.setDesconto(desconto());

            vendaDetalhesCesto.put(vendaDetalhesTemp.getNomeDoProduto(), vendaDetalhesTemp); // Adiciona ou atualiza o produto no cesto
            actualizarCesto(vendaDetalhesCesto); // Atualiza a exibição do cesto
        }
    }

    // Verifica se o desconto foi ativado
    private void ativaCheckBoxDesconto() {
        if (dashboard.getCheckBoxVendaDesconto().isSelected()) {
            this.dashboard.getTxtVendaDesconto().setEnabled(true);
        } else {
            this.dashboard.getTxtVendaDesconto().setEnabled(false);
        }
    }

    // Calcula o valor do desconto
    private BigDecimal desconto() {
        BigDecimal desconto = BigDecimal.ZERO;
        if(dashboard.getCheckBoxVendaDesconto().isSelected()) {
            desconto = validacaoDaPrecoSeENumero(this.dashboard.getTxtVendaDesconto().getText());
        }
        return desconto;
    }

    // Remove um produto do cesto de compras
    private void removerProdutoNoCesto() {
        String produtoSelecionado = this.dashboard.getTxtVendaProdutoRemover().getText();
        validacaoDoCampo(produtoSelecionado, "Produto a ser removido");

        vendaDetalhesCesto.remove(produtoSelecionado); // Remove o produto do cesto
        actualizarCesto(vendaDetalhesCesto); // Atualiza a exibição do cesto
    }

    // Exibe os detalhes da venda, incluindo o total e as informações do cliente
    private void detalhes() {
        BigDecimal total = vendaDetalhesCesto.values().stream().map(VendaDetalhes::getValorDaCompra).reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<Cliente> clienteTemp = clienteDao.buscarClientePorNome(dashboard.getTxtVendaCliente().getText());
        clienteTemp.ifPresentOrElse(
                c -> dashboard.getLabelVendaClienteId().setText(c.getId().toString()),
                () -> mensagemNaTela("Cliente nao encontrado", Color.RED)
        );

        dashboard.getLabelVendaValorTotal().setText(total.toString());
    }

    // Atualiza o cesto de compras exibido na interface
    private void actualizarCesto(HashMap<String, VendaDetalhes> cesto) {
        vendaRegistroTableModel = new VendaRegistroTableModel(cesto.values().stream().collect(Collectors.toList()));
        this.dashboard.getVendaTabela().setModel(vendaRegistroTableModel);
        this.dashboard.getVendaTabela().repaint();
    }

    // Atualiza a tabela de vendas com os registros atuais
    private void actualizarTabelaVenda() {
        vendaTableModel = new VendaTableModel(vendaDao.todos());
        dashboard.getVendaTabelaPrincipal().setModel(vendaTableModel);
        dashboard.getVendaTabelaPrincipal().repaint();
    }

    // Realiza a venda, salvando os dados no banco
    private void vender() {
        try {
            String cliente = dashboard.getTxtVendaCliente().getText();
            validacaoDoCampo(cliente, "Cliente");

            Venda venda = new Venda();
            venda.setCliente(clienteDao.buscarClientePorNome(cliente).orElseThrow(() -> new NegocioException("Cliente nao encontrado")));
            venda.setUsuario(usuarioDao.buscarPorNome(dashboard.getLabelNomeDoUsuario().getText()).orElseThrow(() -> new NegocioException("Usuario nao encontrado")));
            venda.setData(LocalDateTime.now());
            venda.setDetalhes(new ArrayList<>(vendaDetalhesCesto.values()));

            BigDecimal total = vendaDetalhesCesto.values().stream().map(VendaDetalhes::getValorDaCompra).reduce(BigDecimal.ZERO, BigDecimal::add);
            venda.setValorTotal(total);

            vendaDao.salvar(venda); // Salva a venda no banco
            mensagemNaTela("Venda realizada com sucesso!", Color.GREEN);

            actualizarTabelaVenda(); // Atualiza a tabela de vendas
            cancelar(); // Limpa os campos e o cesto
        } catch (NegocioException ex) {
            mensagemNaTela(ex.getMessage(), Color.RED);
        }
    }

    // Cancela a venda atual e limpa o cesto
    private void cancelar() {
        vendaDetalhesCesto.clear(); // Limpa o cesto de compras
        actualizarCesto(vendaDetalhesCesto);
        limparCampos();
    }

    // Limpa os campos da interface
    private void limparCampos() {
        this.dashboard.getTxtVendaCliente().setText("");
        this.dashboard.getTxtVendaPesquisarProduto().setText("");
        this.dashboard.getTxtVendaQuantidadeDoProduto().setText("");
        this.dashboard.getTxtVendaProdutoRemover().setText("");
        this.dashboard.getTxtVendaDesconto().setText("");
        informacaoPadraoDaLabelVendaProduto();
        this.dashboard.getLabelVendaValorTotal().setText("0,00");
    }
}
