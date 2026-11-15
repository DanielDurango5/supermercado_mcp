package com.example.demomcp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

// model/PedidoItem.java
@Entity
@Table(name = "pedido_item")
public class PedidoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pedido_id") private Long pedidoId;
    @Column(name = "producto_id") private Long productoId;
    private Integer cantidad;
    @Column(name = "precio_unitario") private BigDecimal precioUnitario;
    // getters y setters

    public PedidoItem(BigDecimal precioUnitario, Integer cantidad, Long productoId, Long pedidoId, Long id) {
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.productoId = productoId;
        this.pedidoId = pedidoId;
        this.id = id;
    }

    public PedidoItem() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}