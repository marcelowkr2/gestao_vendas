package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.conexao.ConexaoMysql;
import infosystema_informatica.gestao.vendas.modelo.dao.AutenticacaoDao;
import infosystema_informatica.gestao.vendas.modelo.dao.CategoriaDao;
import infosystema_informatica.gestao.vendas.modelo.dao.ProdutoDao;
import infosystema_informatica.gestao.vendas.modelo.dao.UsuarioDao;
import infosystema_informatica.gestao.vendas.modelo.entidades.Categoria;
import infosystema_informatica.gestao.vendas.modelo.entidades.Produto;
import infosystema_informatica.gestao.vendas.modelo.entidades.Usuario;
import infosystema_informatica.gestao.vendas.modelo.exception.NegocioException;
import infosystema_informatica.gestao.vendas.modelo.util.ProdutoTableModel;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 * Controlador responsável por gerenciar as operações relacionadas aos produtos no sistema.
 * Ele implementa ActionListener, MouseListener, e KeyListener para capturar eventos de ação,
 * clique e teclado, respectivamente.
 */
public class ProdutoController implements ActionListener, MouseListener, KeyListener {

    // Atributos necessários para a conexão com o banco de dados e manipulação de dados.
    private final ConexaoMysql conexao;
    private final Dashboard dashboard;
    private final AutenticacaoDao autenticacaoDao;
    private final UsuarioDao usuarioDao;
    private final CategoriaDao categoriaDao;
    private final ProdutoDao produtoDao;
    private ProdutoTableModel produtoTableModel;
    private Produto produto;

    // Construtor que inicializa os DAOs e atualiza a tabela de produtos.
    public ProdutoController(Dashboard dashboard) {
        this.conexao = new ConexaoMysql();
        this.usuarioDao = new UsuarioDao();
        this.autenticacaoDao = new AutenticacaoDao();
        this.dashboard = dashboard;
        this.categoriaDao = new CategoriaDao();
        this.produtoDao = new ProdutoDao();
        actualizarTabela(produtoDao.todosProdutos());
    }

    // Implementação do método actionPerformed para tratar eventos de ação.
    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase();

        // Verifica qual ação foi executada e chama o método correspondente.
        switch (accao) {
            case "adicionar":
                adicionar();
                break;
            case "editar":
                editar();
                break;
            case "apagar":
                apagar();
                break;
            case "adicionarcategoria":
                mostrarTelaCategoria();
                break;
            case "salvar":
                salvar();
                break;
            case "cancelar":
                cancelar();
                break;
        }
    }

    // Método para adicionar um novo produto.
    private void adicionar() {
        Usuario usuario = usuarioLogado();
        if (autenticacaoDao.temPermissao(usuario))
            mostrarTelaProduto();
    }

    // Método para obter o usuário logado.
    private Usuario usuarioLogado() {
        Long usuarioLogadoId = Long.valueOf(this.dashboard.getLabelUsuarioLogadoId().getText());
        return usuarioDao.buscarUsuarioPeloId(usuarioLogadoId);
    }

    // Método para mostrar a tela de produto.
    private void mostrarTelaProduto() {
        this.dashboard.getDialogProduto().pack();
        this.dashboard.getDialogProduto().setLocationRelativeTo(dashboard);
        this.dashboard.getDialogProduto().setVisible(true);
    }

    // Método para mostrar a tela de categoria.
    private void mostrarTelaCategoria() {
        this.dashboard.getDialogCategoria().pack();
        this.dashboard.getDialogCategoria().setLocationRelativeTo(dashboard);
        this.dashboard.getDialogCategoria().setVisible(true);
        ocultaTelaProduto();
    }

    // Método para ocultar a tela de produto.
    private void ocultaTelaProduto() {
        this.dashboard.getDialogProduto().setVisible(false);
    }

    // Métodos de validação para os campos de entrada.
    private void validacaoDoCampo(String campo, String nomeDaVariavel) {
        if (campo.isEmpty()) {
            String mensagem = String.format("Deves preencher o campo %s", nomeDaVariavel);
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    private void validacaoDaQuantidade(Integer quantidade) {
        if (quantidade < 0) {
            String mensagem = "Quantidade não pode ser um número negativo (Menor que zero)";
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    private void validacaoDoPreco(BigDecimal preco) {
        if (preco.doubleValue() < 1) {
            String mensagem = "Preço não pode ser menor que zero";
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    private void validacaoDaCategoria() {
        if (this.dashboard.getComboBoxProdutoCategoria().getSelectedIndex() == 0) {
            String mensagem = "Deves preencher o perfil";
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    // Métodos para validação de conversão de entrada de texto em número.
    private Integer validacaoDaQuantidadeSeENumero(String quantidadeString) {
        try {
            Integer quantidade = Integer.valueOf(quantidadeString);
            return quantidade;
        } catch (NumberFormatException e) {
            mensagemNaTela("Deves inserir apenas número.", Color.RED);
            return 0;
        }
    }

    private BigDecimal validacaoDaPrecoSeENumero(String precoString) {
        try {
            BigDecimal preco = new BigDecimal(precoString);
            return preco;
        } catch (Exception e) {
            mensagemNaTela("Deves inserir apenas número.", Color.RED);
            return BigDecimal.ONE;
        }
    }

    // Método para obter os valores do formulário e validar as entradas.
    private Produto pegarValoresDoFormulario() {
        Usuario usuario = usuarioLogado();

        String idString = this.dashboard.getTxtProdutoId().getText();
        String nome = this.dashboard.getTxtProdutoNome().getText();
        String descricao = this.dashboard.getTxtProdutoDescricao().getText();
        String precoString = this.dashboard.getTxtProdutoPreco().getText();
        String quantidadeString = this.dashboard.getTxtProdutoQuantidade().getValue().toString();
        String categoriaTemp = this.dashboard.getComboBoxProdutoCategoria().getSelectedItem().toString();

        validacaoDoCampo(nome, "nome");
        validacaoDoCampo(precoString, "preço");
        validacaoDoCampo(quantidadeString, "quantidade");
        validacaoDaCategoria();

        Long id = Long.valueOf(idString);
        Integer quantidade = validacaoDaQuantidadeSeENumero(quantidadeString);
        BigDecimal preco = validacaoDaPrecoSeENumero(precoString);
        Categoria categoria = categoriaDao.buscarCategoriaPeloNome(categoriaTemp);

        validacaoDaQuantidade(quantidade);
        validacaoDoPreco(preco);

        return new Produto(id, nome, descricao, preco, categoria, quantidade, usuario, null);
    }

    // Método para salvar um produto após validação.
    private void salvar() {
        Produto produtoTemp = pegarValoresDoFormulario();

        String mensagem = produtoDao.salvar(produtoTemp);

        if (mensagem.startsWith("Produto")) {
            mensagemNaTela(mensagem, Color.GREEN);
            limpaCampo();
        } else {
            mensagemNaTela(mensagem, Color.RED);
        }
    }

    // Método para cancelar a operação atual e limpar o formulário.
    private void cancelar() {
        this.dashboard.getDialogProduto().setVisible(false);
        limpaCampo();
        mensagemNaTela("", Color.WHITE);
    }

    // Método para exibir uma mensagem na tela.
    private void mensagemNaTela(String mensagem, Color color) {
        this.dashboard.getLabelProdutoMensagem().setBackground(color);
        this.dashboard.getLabelProdutoMensagem().setText(mensagem);
    }

    // Método para limpar os campos do formulário e atualizar a tabela de produtos.
    private void limpaCampo() {
        this.dashboard.getTxtProdutoId().setText("0");
        this.dashboard.getTxtProdutoNome().setText("");
        this.dashboard.getTxtProdutoDescricao().setText("");
        this.dashboard.getTxtProdutoPreco().setText("1");
        this.dashboard.getTxtProdutoQuantidade().setValue(0);
        this.dashboard.getComboBoxProdutoCategoria().setSelectedIndex(0);
        actualizarTabela(produtoDao.todosProdutos());
        this.produto = null;
    }

    // Método para atualizar a tabela de produtos na interface.
    private void actualizarTabela(List<Produto> produtos) {
        this.produtoTableModel = new ProdutoTableModel(produtos);
        this.dashboard.getTabelaProduto().setModel(produtoTableModel);
        this.dashboard.getLabelHomeProduto().setText(String.format("%d", produtos.size()));
    }

    // Método para editar um produto selecionado.
    private void editar() {
        Usuario usuario = usuarioLogado();
        if (autenticacaoDao.temPermissao(usuario)) {
            if (this.produto != null) {
                mostrarTelaProduto();
            } else {
                JOptionPane.showMessageDialog(dashboard, "Deves selecionar um produto na tabela", "Seleciona um produto", 0);
            }
        }
    }

    // Método para apagar um produto selecionado.
    private void apagar() {
        Usuario usuario = usuarioLogado();
        if (autenticacaoDao.temPermissao(usuario)) {
            if (this.produto != null) {
                int confirmar = JOptionPane.showConfirmDialog(dashboard,
                        String.format("Tens certeza que desejas apagar? \nNome: %s", this.produto.getNome()),
                        "Apagar produto", JOptionPane.YES_NO_OPTION);

                if (confirmar == JOptionPane.YES_OPTION) {
                    String mensagem = produtoDao.deletaProdutoPeloId(this.produto.getId());
                    JOptionPane.showMessageDialog(dashboard, mensagem);
                    limpaCampo();
                }
            } else {
                JOptionPane.showMessageDialog(dashboard, "Deves selecionar um produto na tabela", "Seleciona um produto", 0);
            }
        }
    }

    // Método para preencher o formulário com os valores do produto selecionado.
    private void preencherOsValoresNoFormulario() {
        this.dashboard.getTxtProdutoId().setText(Long.toString(this.produto.getId()));
        this.dashboard.getTxtProdutoNome().setText(this.produto.getNome());
        this.dashboard.getTxtProdutoDescricao().setText(this.produto.getDescricao());
        this.dashboard.getTxtProdutoPreco().setText(this.produto.getPreco().toString());
        this.dashboard.getTxtProdutoQuantidade().setValue(this.produto.getQuantidade());
        this.dashboard.getComboBoxProdutoCategoria().setSelectedItem(this.produto.getCategoria().getNome());
    }

    // Implementações dos métodos de MouseListener para capturar eventos de mouse.
    @Override
    public void mouseClicked(MouseEvent me) {
        int linhaSelecionada = this.dashboard.getTabelaProduto().getSelectedRow();
        this.produto = this.produtoTableModel.getProdutos().get(linhaSelecionada);
        preencherOsValoresNoFormulario();
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}

    // Implementações dos métodos de KeyListener para capturar eventos de teclado.
    @Override
    public void keyTyped(KeyEvent ke) {}

    @Override
    public void keyPressed(KeyEvent ke) {}

    @Override
    public void keyReleased(KeyEvent ke) {
        String pesquisar = this.dashboard.getTxtProdutoPesquisar().getText();

        // Pesquisa produtos com base no nome ou na categoria.
        if (pesquisar.isEmpty()) {
            actualizarTabela(produtoDao.todosProdutos());
        } else {
            List<Produto> produtoTemp = this.produtoDao.todosProdutos()
                    .stream()
                    .filter((Produto p) -> {
                        return p.getNome().toLowerCase().contains(pesquisar.toLowerCase()) ||
                                p.getCategoria().getNome().toLowerCase().contains(pesquisar.toLowerCase());
                    })
                    .collect(Collectors.toList());

            actualizarTabela(produtoTemp);
        }
    }
}
