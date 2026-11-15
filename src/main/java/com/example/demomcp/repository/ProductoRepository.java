package com.example.demomcp.repository;

import com.example.demomcp.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockGreaterThan(Integer min);
    List<Producto> findByCategoriaIgnoreCaseAndStockGreaterThan(String categoria, Integer min);
    List<Producto> findByNombreContainingIgnoreCase(String fragmento);
}