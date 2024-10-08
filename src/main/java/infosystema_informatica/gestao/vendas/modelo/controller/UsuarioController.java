package infosystema_informatica.gestao.vendas.modelo.controller;

import infosystema_informatica.gestao.vendas.modelo.dao.AutenticacaoDao;
import infosystema_informatica.gestao.vendas.modelo.dao.UsuarioDao;
import infosystema_informatica.gestao.vendas.modelo.util.UsuarioTableModel;
import infosystema_informatica.gestao.vendas.modelo.entidades.PERFIL;
import infosystema_informatica.gestao.vendas.modelo.entidades.Usuario;
import infosystema_informatica.gestao.vendas.modelo.exception.NegocioException;
import infosystema_informatica.gestao.vendas.view.formulario.Dashboard;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 * Controlador responsável pela gestão dos usuários na aplicação.
 * Implementa ActionListener, MouseListener e KeyListener para
 * capturar eventos de ação, mouse e teclado, respectivamente.
 */
public class UsuarioController implements ActionListener, MouseListener, KeyListener {

    // Referências aos objetos necessários para a interação com a interface e a base de dados.
    private Dashboard dashboard;
    private AutenticacaoDao autenticacaoDao;
    private UsuarioTableModel usuarioTableModel;
    private UsuarioDao usuarioDao;
    private Usuario usuario;

    // Construtor que inicializa o controlador, configurando a interface e atualizando a tabela de usuários.
    public UsuarioController(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.autenticacaoDao = new AutenticacaoDao();
        this.usuarioDao = new UsuarioDao();
        inicializaComboBoxUsuarioPerfil(); // Inicializa a comboBox com os perfis de usuário.
        actualizarTabelaUsuario(usuarioDao.todosUsuarios()); // Atualiza a tabela de usuários.
    }

    // Método que trata os eventos de ação (botões).
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
                remover();
                break;
            case "salvar":
                salvar();
                break;
            case "cancelar":
                ocultaTelaUsuario();
                break;
        }
    }

    // Inicializa a comboBox com os perfis disponíveis.
    private void inicializaComboBoxUsuarioPerfil() {
        this.dashboard.getComboBoxUsuarioPerfil().removeAllItems();
        this.dashboard.getComboBoxUsuarioPerfil().addItem("Seleciona o perfil");

        for (PERFIL perfil : PERFIL.values()) {
            this.dashboard.getComboBoxUsuarioPerfil().addItem(perfil);
        }
    }

    // Mostra a tela de cadastro/edição de usuário.
    private void mostrarTelaUsuario() {
        this.dashboard.getDialogUsuario().pack();
        this.dashboard.getDialogUsuario().setLocationRelativeTo(dashboard);
        this.dashboard.getDialogUsuario().setVisible(true);
    }

    // Oculta a tela de usuário e limpa os campos do formulário.
    private void ocultaTelaUsuario() {
        this.dashboard.getDialogUsuario().pack();
        this.dashboard.getDialogUsuario().setLocationRelativeTo(dashboard);
        this.dashboard.getDialogUsuario().setVisible(false);
        limpaCampos();
        mensagemNaTela("", Color.WHITE);
    }

    // Obtém o usuário atualmente logado no sistema.
    private Usuario usuarioLogado() {
        Long usuarioLogadoId = Long.valueOf(this.dashboard.getLabelUsuarioLogadoId().getText());
        return usuarioDao.buscarUsuarioPeloId(usuarioLogadoId);
    }

    // Adiciona um novo usuário ao sistema, caso o usuário logado tenha permissão.
    private void adicionar() {
        Usuario usuarioLogado = usuarioLogado();
        if (autenticacaoDao.temPermissao(usuarioLogado)) {
            mostrarTelaUsuario();
        }
    }

    // Edita o usuário selecionado na tabela, caso o usuário logado tenha permissão.
    private void editar() {
        Usuario usuarioLogado = usuarioLogado();
        if (autenticacaoDao.temPermissao(usuarioLogado)) {
            if (this.usuario != null) {
                preencherOsValoresNoFormularioUsuario();
                mostrarTelaUsuario();
            } else {
                JOptionPane.showMessageDialog(dashboard, "Deves selecionar um usuario na tabela", "Seleciona um usuario", 0);
            }
        }
    }

    // Remove o usuário selecionado na tabela, após confirmação, caso o usuário logado tenha permissão.
    private void remover() {
        Usuario usuarioLogado = usuarioLogado();
        if (autenticacaoDao.temPermissao(usuarioLogado)) {
            if (this.usuario != null) {
                if (this.usuario.equals(usuarioLogado)) {
                    JOptionPane.showMessageDialog(dashboard, "Usuario logado nao pode ser removido", "Usuario logado", 0);
                } else {
                    int confirmar = JOptionPane.showConfirmDialog(dashboard,
                            String.format("Tens certeza que desejas apagar? \nNome: %s", this.usuario.getNome()),
                            "Apagar usuario", JOptionPane.YES_NO_OPTION);

                    if (confirmar == JOptionPane.YES_OPTION) {
                        String mensagem = usuarioDao.deleteUsuarioPeloId(this.usuario.getId());
                        JOptionPane.showMessageDialog(dashboard, mensagem);
                        limpaCampos();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dashboard, "Deves selecionar um usuario na tabela", "Seleciona um usuario", 0);
            }
        }
    }

    // Limpa os campos do formulário de usuário.
    private void limpaCampos() {
        this.dashboard.getTxtUsuarioId().setText("0");
        this.dashboard.getTxtUsuarioNome().setText("");
        this.dashboard.getTxtUsuarioUsername().setText("");
        this.dashboard.getTxtUsuarioSenha().setText("");
        this.dashboard.getComboBoxUsuarioPerfil().setSelectedIndex(0);
        this.dashboard.getRadioBotaoActivo().setSelected(true);
        actualizarTabelaUsuario(usuarioDao.todosUsuarios());
        this.usuario = null;
    }

    // Valida se os campos obrigatórios foram preenchidos.
    private void validacaoDosCampos(String campo, String nomeDaVariavel) {
        if (campo.isEmpty()) {
            String mensagem = String.format("Deves preencher o campo %s", nomeDaVariavel);
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    // Valida se um perfil foi selecionado.
    private void validacaoDoPerfil() {
        if (this.dashboard.getComboBoxUsuarioPerfil().getSelectedIndex() == 0) {
            String mensagem = "Deves preencher o perfil";
            mensagemNaTela(mensagem, Color.RED);
            throw new NegocioException(mensagem);
        }
    }

    // Preenche o formulário com os valores do usuário selecionado na tabela.
    private void preencherOsValoresNoFormularioUsuario() {
        this.dashboard.getTxtUsuarioId().setText(this.usuario.getId().toString());
        this.dashboard.getTxtUsuarioNome().setText(this.usuario.getNome());
        this.dashboard.getTxtUsuarioUsername().setText(this.usuario.getUsername());
        this.dashboard.getTxtUsuarioSenha().setText("");
        this.dashboard.getComboBoxUsuarioPerfil().setSelectedItem(this.usuario.getPerfil());

        if (usuario.isEstado()) {
            this.dashboard.getRadioBotaoActivo().setSelected(true);
        } else {
            this.dashboard.getRadioBotaoDesativo().setSelected(true);
        }
    }

    // Obtém os valores do formulário de usuário e valida as entradas.
    private Usuario pegarOsValoresDoFormularioUsuario() {
        String idString = this.dashboard.getTxtUsuarioId().getText();
        String nome = this.dashboard.getTxtUsuarioNome().getText();
        String username = this.dashboard.getTxtUsuarioUsername().getText();
        String senha = this.dashboard.getTxtUsuarioSenha().getText();
        String perfil = this.dashboard.getComboBoxUsuarioPerfil().getSelectedItem().toString();

        Long id = Long.valueOf(idString);

        validacaoDosCampos(nome, "nome");
        validacaoDosCampos(username, "username");
        validacaoDosCampos(senha, "senha");
        validacaoDoPerfil();

        Usuario usuarioTemp = new Usuario(id, nome, username, senha, PERFIL.valueOf(perfil), null, null);

        if (this.dashboard.getRadioBotaoDesativo().isSelected()) {
            usuarioTemp.mudarEstado();
        }

        return usuarioTemp;
    }

    // Salva o usuário após validação dos dados.
    private void salvar() {
        Usuario usuarioTemp = pegarOsValoresDoFormularioUsuario();
        String mensagem = usuarioDao.salvar(usuarioTemp);

        if (mensagem.startsWith("Usuario")) {
            mensagemNaTela(mensagem, Color.GREEN);
            limpaCampos();
        } else {
            mensagemNaTela(mensagem, Color.RED);
        }
    }

    // Exibe uma mensagem na tela.
    public void mensagemNaTela(String mensagem, Color color) {
        this.dashboard.getLabelUsuarioMensagem().setText(mensagem);
        this.dashboard.getLabelUsuarioMensagem().setBackground(color);
    }

    // Atualiza a tabela de usuários na interface.
    private void actualizarTabelaUsuario(List<Usuario> usuarios) {
        this.usuarioTableModel = new UsuarioTableModel(usuarios);
        this.dashboard.getTabelaUsuarios().setModel(usuarioTableModel);
    }

    // Implementações dos métodos de MouseListener para capturar eventos de mouse.
    @Override
    public void mouseClicked(MouseEvent me) {
        int linhaSelecionada = this.dashboard.getTabelaUsuarios().getSelectedRow();
        this.usuario = usuarioTableModel.getUsuarios().get(linhaSelecionada);
    }

    @Override
    public void mousePressed(MouseEvent me) { }

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
        String pesquisar = this.dashboard.getTxtUsuarioPesquisar().getText();

        if (pesquisar.isEmpty()) {
            actualizarTabelaUsuario(usuarioDao.todosUsuarios());
        } else {
            List<Usuario> usuariosTemp = this.usuarioDao.todosUsuarios()
                    .stream()
                    .filter((Usuario u) -> {
                        return u.getNome().toLowerCase().contains(pesquisar.toLowerCase()) ||
                                u.getUsername().toLowerCase().contains(pesquisar.toLowerCase()) ||
                                u.getPerfil().name().toLowerCase().contains(pesquisar.toLowerCase());
                    })
                    .collect(Collectors.toList());

            actualizarTabelaUsuario(usuariosTemp);
        }

    }
}
