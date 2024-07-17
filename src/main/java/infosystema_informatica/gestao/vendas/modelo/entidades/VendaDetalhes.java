/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infosystema_informatica.gestao.vendas.modelo.entidades;

import java.math.BigDecimal;

/**
 *
 * @author quitumba
 */
public class VendaDetalhes {

    private Venda venda;
    private Produto produto;
    private int quantidade;
    private BigDecimal desconto;
    private BigDecimal total;

    public VendaDetalhes() {
    }

    public VendaDetalhes(Venda venda, Produto produto, int quantidade, BigDecimal desconto, BigDecimal total) {
        this.venda = venda;
        this.produto = produto;
        this.quantidade = quantidade;
        this.desconto = desconto;
        this.total = total;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }




}
