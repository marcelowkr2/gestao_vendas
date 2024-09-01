package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.dao.AutenticacaoDao;
import infosystema_informatica.gestao.vendas.modelo.entidades.Usuario;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import infosystema_informatica.gestao.vendas.view.formulario.LoginForm;
import infosystema_informatica.gestao.vendas.view.modelo.LoginDto;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class AutenticacaoController implements ActionListener {

    // Atributos que representam o formulário de login e o DAO de autenticação.
    private final LoginForm loginForm;
    private final AutenticacaoDao autenticacao;

    // Construtor que inicializa o formulário de login e o DAO de autenticação.
    public AutenticacaoController(LoginForm loginForm) {
        this.loginForm = loginForm;
        autenticacao = new AutenticacaoDao();
    }

    // Implementação do método actionPerformed para tratar eventos de ação (cliques em botões).
    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase();

        switch (accao) {
            case "login":
                login();
                break;
            case "cancelar":
                cancelar();
                break;
        }
    }

    // Método para realizar o login do usuário.
    private void login() {
        String username = loginForm.getTxtLoginUsername().getText();
        String senha = loginForm.getTxtLoginSenha().getText();

        // Verifica se os campos de username e senha estão preenchidos.
        if (username.equals("") || senha.equals("")) {
            loginForm.getLabelLoginMensagem().setText("Username e senha devem ser preenchido");
            return;
        }

        // Cria um objeto LoginDto com os dados do formulário.
        LoginDto login = new LoginDto(username, senha);

        // Verifica as credenciais do usuário no banco de dados.
        Usuario usuario = autenticacao.login(login);

        // Se o usuário for autenticado com sucesso.
        if (usuario != null) {
            System.out.println("Sucesso: " + usuario.getUsername());
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
            dashboard.getLabelBenvindoUsuario().setText(String.format("Bem-vindo %s", usuario.getNome()));
            dashboard.getLabelUsuarioLogadoId().setText(Long.toString(usuario.getId()));
            this.loginForm.setVisible(false);
            limpaTela();
        } else {
            // Caso as credenciais estejam incorretas.
            loginForm.getLabelLoginMensagem().setText("Username ou senha incorreta.");
//            JOptionPane.showMessageDialog(null, "Username ou senha incorreta.");
        }
    }

    // Método para cancelar o login e fechar a aplicação se o usuário confirmar.
    private void cancelar() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tens certeza que desejas sair?", "Sair do login", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) System.exit(0);
    }

    // Método para limpar os campos da tela de login.
    private void limpaTela() {
        loginForm.getLabelLoginMensagem().setText("");
        loginForm.getTxtLoginUsername().setText("");
        loginForm.getTxtLoginSenha().setText("");
    }
}
