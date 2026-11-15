package com.example.demomcp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

// model/Pedido.java
@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cliente_nombre") private String clienteNombre;
    private String direccion;
    private String telefono;
    private String estado;          // BORRADOR, CONFIRMADO, CANCELADO
    private BigDecimal total;
    // getters y setters

    public Pedido(Long id, String clienteNombre, String direccion, String telefono, String estado, BigDecimal total) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.estado = estado;
        this.total = total;
    }

    public Pedido() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}