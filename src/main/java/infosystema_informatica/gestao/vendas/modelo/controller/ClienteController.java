package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.dao.ClienteDao;
import infosystema_informatica.gestao.vendas.modelo.dao.ProdutoDao;
import infosystema_informatica.gestao.vendas.modelo.entidades.Cliente;
import infosystema_informatica.gestao.vendas.modelo.util.ClienteTableModel;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClienteController implements ActionListener {

    // Atributos que representam o painel principal, o DAO de cliente e o modelo da tabela de clientes.
    private Dashboard dashboard;
    private ClienteDao clienteDao;
    private ClienteTableModel clienteTableModel;

    // Construtor que inicializa o painel e o DAO, e atualiza a tabela de clientes.
    public ClienteController(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.clienteDao = new ClienteDao();
        actualizarTabelaCliente();
    }

    // Implementação do método actionPerformed para tratar eventos de ação (cliques em botões).
    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase();

        // Verifica qual ação foi executada e chama o método correspondente.
        switch (accao) {
            case "adicionar":
                adicionar();
                break;
            case "salvar":
                salvar();
                break;
            case "cancelar":
                cancelar();
                break;
        }
    }

    // Método para salvar ou atualizar as informações de um cliente.
    public void salvar() {
        String idString = this.dashboard.getTxtClienteId().getText();
        String nome = this.dashboard.getTxtClienteNome().getText();
        String telefone = this.dashboard.getTxtClienteTelefone().getText();
        String endereco = this.dashboard.getTxtClienteEndereco().getText();

        Long id = Long.valueOf(idString);

        // Cria um objeto Cliente com os valores do formulário.
        Cliente cliente = new Cliente(id, nome, telefone, endereco);
        String mensagem = clienteDao.salvar(cliente);

        // Exibe uma mensagem na tela dependendo do sucesso da operação.
        if (mensagem.startsWith("Cliente")) {
            mensagemNaTela(mensagem, Color.GREEN);
            actualizarTabelaCliente();
        } else {
            mensagemNaTela(mensagem, Color.RED);
        }
    }

    // Método para exibir mensagens na tela com a cor especificada.
    private void mensagemNaTela(String mensagem, Color color) {
        this.dashboard.getLabelClienteMensagem().setBackground(color);
        this.dashboard.getLabelClienteMensagem().setText(mensagem);
    }

    // Método para cancelar a operação e fechar o diálogo de cliente.
    private void cancelar() {
        limp
