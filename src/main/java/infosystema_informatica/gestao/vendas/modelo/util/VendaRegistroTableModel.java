package infosystema_informatica.gestao.vendas.modelo.util;

import infosystema_informatica.gestao.vendas.modelo.entidades.Produto;
import infosystema_informatica.gestao.vendas.modelo.entidades.VendaDetalhes;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author quitumba
 */
public class VendaRegistroTableModel extends AbstractTableModel {

    private HashMap<String, VendaDetalhes> vendaDetalhes;
    private String [] colunas = {"PRODUTO", "PREÇO", "QTD", "DESCONTO", "TOTAL"};

    public VendaRegistroTableModel(HashMap<String, VendaDetalhes> vendaDetalhes) {
        this.vendaDetalhes = vendaDetalhes;
    }

    @Override
    public int getRowCount() {
        return vendaDetalhes.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        VendaDetalhes vendaDetalhess = vendaDetalhes
                .values()
                .stream()
                .collect(Collectors.toList()).get(linha);

        switch(coluna) {
            case 0: return vendaDetalhess.getProduto().getNome();
            case 1: return vendaDetalhess.getProduto().getPreco();
            case 2: return vendaDetalhess.getQuantidade();
            case 3: return vendaDetalhess.getDesconto();
            case 4: return vendaDetalhess.getTotal();
            default: return "";
        }
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public HashMap<String, VendaDetalhes> getVendaDetalhes() {
        return vendaDetalhes;
    }

    public void setVendaDetalhes(HashMap<String, VendaDetalhes> vendaDetalhes) {
        this.vendaDetalhes = vendaDetalhes;
    }


}

