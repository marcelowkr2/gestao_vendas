package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.conexao.ConexaoMysql;
import infosystema_informatica.gestao.vendas.modelo.dao.CategoriaDao;
import infosystema_informatica.gestao.vendas.modelo.entidades.Categoria;
import infosystema_informatica.gestao.vendas.modelo.exception.NegocioException;
import infosystema_informatica.gestao.vendas.modelo.util.CategoriaTableModel;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class CategoriaController implements ActionListener, MouseListener {

    // Atributos que representam a conexão, tela, DAO, modelo da tabela e lista de categorias.
    private ConexaoMysql conexao;
    private Dashboard dashboard;
    private CategoriaDao categoriaDao;
    private CategoriaTableModel categoriaTableModel;
    private List<Categoria> categorias;
    private Categoria categoria;

    // Construtor que inicializa a conexão, DAO e atualiza a tabela de categorias e o comboBox.
    public CategoriaController(Dashboard dashboard) {
        this.conexao = new ConexaoMysql();
        this.categoriaDao = new CategoriaDao();
        this.dashboard = dashboard;
        actualizarTabelaCategoria();
        inicializarComboBoxCategoriaNoProduto();
    }

    // Método para inicializar o comboBox com as categorias disponíveis.
    private void inicializarComboBoxCategoriaNoProduto() {
        this.dashboard.getComboBoxProdutoCategoria().removeAllItems();
        this.dashboard.getComboBoxProdutoCategoria().addItem("Selecione a categoria");
        categorias.stream().forEach(c -> {
            this.dashboard.getComboBoxProdutoCategoria().addItem(c.getNome());
        });
    }

    // Método para atualizar a tabela de categorias com os dados do banco de dados.
    private void actualizarTabelaCategoria() {
        this.categorias = categoriaDao.todasCategorias();
        this.categoriaTableModel = new CategoriaTableModel(this.categorias);
        this.dashboard.getTabelaCategoria().setModel(categoriaTableModel);
    }

    // Implementação do método actionPerformed para tratar eventos de ação (cliques em botões).
    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase();
        switch (accao) {
            case "salvar":
                salvar();
                break;
            case "apagar":
                apagar();
                break;
            case "limpar":
                limpar();
                break;
            case "cancelar":
                cancelar();
                break;
        }
    }

    // Método para salvar uma nova categoria ou atualizar uma existente.
    private void salvar() {
        Categoria categoriaTemp = pegarValoresDoFormulario();
        String mensagem = categoriaDao.salvar(categoriaTemp);

        if (mensagem.startsWith("Categoria")) {
            mensagemNaTela(mensagem, Color.GREEN);
            actualizarTabelaCategoria();
            limpaCampos();
        } else {
            mensagemNaTela(mensagem, Color.RED);
        }
    }

    // Método para limpar os campos do formulário.
    private void limpaCampos() {
        this.dashboard.getTxtCategoriaId().setText("0");
        this.dashboard.getTxtCategoriaNome().setText("");
        this.dashboard.getTxtCategoriaDescricao().setText("");
        this.categoria = null;
        actualizarTabelaCategoria();
        inicializarComboBoxCategoriaNoProduto();
    }

    // Método para validar se o campo nome foi preenchido.
    private void validacaoDoCampo(String campo) {
        if (campo.isEmpty()) {
            String mensagem = "Deves preencher o campo nome";
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    // Método para exibir mensagens na tela com a cor especificada.
    private void mensagemNaTela(String mensagem, Color color) {
        this.dashboard.getLabelCategoriaMensagem().setText(mensagem);
        this.dashboard.getLabelCategoriaMensagem().setBackground(color);
    }

    // Método para obter os valores do formulário e criar uma nova categoria.
    private Categoria pegarValoresDoFormulario() {
        String idString = this.dashboard.getTxtCategoriaId().getText();
        String nome = this.dashboard.getTxtCategoriaNome().getText();
        String descricao = this.dashboard.getTxtCategoriaDescricao().getText();

        Long id = Long.valueOf(idString);

        validacaoDoCampo(nome);

        return new Categoria(id, nome, descricao);
    }

    // Método para preencher o formulário com os valores da categoria selecionada.
    private void preencherValoresNoFormulario() {
        this.dashboard.getTxtCategoriaId().setText(Long.toString(this.categoria.getId()));
        this.dashboard.getTxtCategoriaNome().setText(this.categoria.getNome());
        this.dashboard.getTxtCategoriaDescricao().setText(this.categoria.getDescricao());
    }

    // Método para apagar uma categoria selecionada.
    private void apagar() {
        if (categoria != null) {
            String mensagem = categoriaDao.deleteCategoriaPeloId(this.categoria.getId());

            if (mensagem.startsWith("Categoria")) {
                mensagemNaTela(mensagem, Color.GREEN);
                limpaCampos();
            } else {
                mensagemNaTela(mensagem, Color.RED);
            }

        } else {
            mensagemNaTela("Deves selecionar uma categoria na tabela", Color.RED);
        }
    }

    // Método para limpar o formulário e as mensagens da tela.
    private void limpar() {
        limpaCampos();
        mensagemNaTela("", Color.WHITE);
    }

    // Método para cancelar a operação e fechar o diálogo de categoria.
    private void cancelar() {
        this.dashboard.getDialogCategoria().setVisible(false);
        this.dashboard.getDialogProduto().setVisible(true);
        limpar();
    }

    // Implementação dos métodos da interface MouseListener para tratar eventos do mouse.
    @Override
    public void mouseClicked(MouseEvent me) {
        int linhaSelecionada = this.dashboard.getTabelaCategoria().getSelectedRow();
        this.categoria = this.categoriaTableModel.getCategorias().get(linhaSelecionada);
        preencherValoresNoFormulario();
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
}
