package infosystema_informatica.gestao.vendas.modelo.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMysql {
    private static final String URL = "jdbc:mysql://localhost:3306/gestao_vendas";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public Connection obterConexao() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}


