package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.dao.AutenticacaoDao;
import infosystema_informatica.gestao.vendas.modelo.entidades.Usuario;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import infosystema_informatica.gestao.vendas.view.formulario.LoginForm;
import infosystema_informatica.gestao.vendas.view.modelo.LoginDto;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class AutenticacaoController implements ActionListener{

    private final LoginForm loginForm;
    private final AutenticacaoDao autenticacao;

    public AutenticacaoController(LoginForm loginForm) {
        this.loginForm = loginForm;
        autenticacao = new AutenticacaoDao();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase();

        switch(accao) {
            case "login": login(); break;
            case "cancelar": cancelar(); break;
        }
    }


    private void login() {
        String username = loginForm.getTxtLoginUsername().getText();
        String senha = loginForm.getTxtLoginSenha().getText();

        if(username.equals("") || senha.equals("")) {
            loginForm.getLabelLoginMensagem().setText("Username e senha devem ser preenchido");
            return;
        }

        LoginDto login = new LoginDto(username, senha);
        Usuario usuario = autenticacao.login(login);

        if(usuario != null) {
            System.out.println("Sucesso: " + usuario.getUsername());
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
            dashboard.getLabelBenvindoUsuario().setText(String.format("Bem-vindo %s", usuario.getNome()));
            dashboard.getLabelUsuarioLogadoId().setText(Long.toString(usuario.getId()));
            this.loginForm.setVisible(false);
            limpaTela();
        }else{
            loginForm.getLabelLoginMensagem().setText("Username ou senha incorreta.");
//            JOptionPane.showMessageDialog(null, "Username ou senha incorreta.");
        }
    }

    private void cancelar() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tens certeza que desejas sair?","Sair do login", JOptionPane.YES_NO_OPTION);

        if(confirma == JOptionPane.YES_OPTION) System.exit(0);
    }

    private void limpaTela() {
        loginForm.getLabelLoginMensagem().setText("");
        loginForm.getTxtLoginUsername().setText("");
        loginForm.getTxtLoginSenha().setText("");
    }

}

