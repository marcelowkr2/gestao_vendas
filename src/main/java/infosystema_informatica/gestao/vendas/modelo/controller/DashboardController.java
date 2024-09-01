package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.dao.UsuarioDao;
import infosystema_informatica.gestao.vendas.modelo.util.UsuarioTableModel;
import infosystema_informatica.gestao.vendas.modelo.entidades.Usuario;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Controlador para gerenciar as interações no painel de controle (Dashboard).
 */
public final class DashboardController implements ActionListener {

    // Atributos que representam o painel principal (Dashboard) e o DAO de usuários.
    private final Dashboard dashboard;
    private final UsuarioDao usuarioDao;
    private UsuarioTableModel usuarioTableModel;

    // Construtor que inicializa o Dashboard e o DAO de usuário.
    public DashboardController(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.usuarioDao = new UsuarioDao();
    }

    // Implementação do método actionPerformed para tratar eventos de ação (cliques em botões).
    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase();

        // Verifica qual ação foi executada e chama o método correspondente.
        switch (accao) {
            case "home":
                panelHome();
                break;
            case "clientes":
                panelClientes();
                break;
            case "produtos":
                panelProdutos();
                break;
            case "vendas":
                panelVendas();
                break;
            case "usuarios":
                panelUsuarios();
                break;
            case "sair":
                sair();
                break;
        }
    }

    // Método para alterar o painel visível no painel principal (Dashboard).
    private void painelComponentes(JPanel panel) {
        this.dashboard.getPanelBody().removeAll();
        this.dashboard.getPanelBody().repaint();
        this.dashboard.getPanelBody().revalidate();
        this.dashboard.getPanelBody().add(panel);
        this.dashboard.getPanelBody().repaint();
        this.dashboard.getPanelBody().revalidate();
    }

    // Método para exibir o painel de clientes.
    private void panelClientes() {
        painelComponentes(this.dashboard.getPanelCliente());
    }

    // Método para exibir o painel de produtos.
    private void panelProdutos() {
        painelComponentes(this.dashboard.getPanelProduto());
    }

    // Método para exibir um diálogo de confirmação e sair do sistema se confirmado.
    private void sair() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tens certeza que desejas sair?", "Sair do login", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) System.exit(0);
    }

    // Método para exibir o painel de usuários.
    private void panelUsuarios() {
        painelComponentes(this.dashboard.getPanelUsuario());
    }

    // Método para exibir o painel de vendas.
    private void panelVendas() {
        painelComponentes(this.dashboard.getPanelVenda());
    }

    // Método para exibir o painel inicial (Home).
    private void panelHome() {
        painelComponentes(this.dashboard.getPanelHome());
    }
}
