package com.example.demomcp.service;

import com.example.demomcp.model.Pedido;
import com.example.demomcp.model.PedidoItem;
import com.example.demomcp.model.Producto;
import com.example.demomcp.repository.PedidoItemRepository;
import com.example.demomcp.repository.PedidoRepository;
import com.example.demomcp.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SupermercadoToolsService {

    public record LineaPedido(String producto, int cantidad,
                              BigDecimal precioUnitario, BigDecimal subtotal) {}
    public record ResumenPedido(Long pedidoId, String cliente, String direccion,
                                String estado, List<LineaPedido> lineas, BigDecimal total) {}

    private final ProductoRepository productos;
    private final PedidoRepository pedidos;
    private final PedidoItemRepository items;

    public SupermercadoToolsService(ProductoRepository productos,
                                    PedidoRepository pedidos,
                                    PedidoItemRepository items) {
        this.productos = productos; this.pedidos = pedidos; this.items = items;
    }

    @Tool(description = "Lista los productos disponibles (con stock). Si se indica categoría, filtra por ella.")
    public List<Producto> listarProductos(
            @ToolParam(description = "Categoría, por ejemplo Lácteos; null para todas", required = false)
            String categoria) {
        if (categoria == null || categoria.isBlank())
            return productos.findByStockGreaterThan(0);
        return productos.findByCategoriaIgnoreCaseAndStockGreaterThan(categoria.trim(), 0);
    }

    @Tool(description = "Busca productos cuyo nombre contiene el texto indicado.")
    public List<Producto> buscarProductos(
            @ToolParam(description = "Texto a buscar en el nombre") String fragmento) {
        return productos.findByNombreContainingIgnoreCase(fragmento);
    }

    @Tool(description = "Crea un pedido nuevo en estado BORRADOR con los datos de entrega. Devuelve el id del pedido.")
    @Transactional
    public Pedido crearPedido(
            @ToolParam(description = "Nombre del cliente") String cliente,
            @ToolParam(description = "Dirección de entrega") String direccion,
            @ToolParam(description = "Teléfono de contacto") String telefono) {
        if (cliente == null || cliente.isBlank() || direccion == null || direccion.isBlank())
            throw new IllegalArgumentException("Cliente y dirección son obligatorios");
        Pedido p = new Pedido();
        p.setClienteNombre(cliente.trim());
        p.setDireccion(direccion.trim());
        p.setTelefono(telefono);
        p.setEstado("BORRADOR");
        p.setTotal(BigDecimal.ZERO);
        return pedidos.save(p);
    }

    @Tool(description = "Agrega un producto con su cantidad a un pedido en BORRADOR. Valida stock.")
    @Transactional
    public ResumenPedido agregarProducto(
            @ToolParam(description = "Id del pedido") Long pedidoId,
            @ToolParam(description = "Id del producto") Long productoId,
            @ToolParam(description = "Cantidad, mayor que cero") Integer cantidad) {
        Pedido p = obtenerBorrador(pedidoId);
        if (cantidad == null || cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        Producto prod = productos.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto " + productoId));
        if (prod.getStock() < cantidad)
            throw new IllegalArgumentException("Stock insuficiente de " + prod.getNombre()
                    + ". Disponible: " + prod.getStock());
        PedidoItem it = new PedidoItem();
        it.setPedidoId(pedidoId);
        it.setProductoId(productoId);
        it.setCantidad(cantidad);
        it.setPrecioUnitario(prod.getPrecio());
        items.save(it);
        recalcularTotal(p);
        return resumen(p);
    }

    @Tool(description = "Muestra el resumen de un pedido: líneas, total y estado.")
    public ResumenPedido verPedido(@ToolParam(description = "Id del pedido") Long pedidoId) {
        Pedido p = pedidos.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el pedido " + pedidoId));
        return resumen(p);
    }

    @Tool(description = "Confirma un pedido en BORRADOR y descuenta el stock. Solo llamar con aprobación explícita del cliente.")
    @Transactional
    public ResumenPedido confirmarPedido(@ToolParam(description = "Id del pedido") Long pedidoId) {
        Pedido p = obtenerBorrador(pedidoId);
        List<PedidoItem> lineas = items.findByPedidoId(pedidoId);
        if (lineas.isEmpty()) throw new IllegalStateException("El pedido no tiene productos");
        for (PedidoItem it : lineas) {
            Producto prod = productos.findById(it.getProductoId()).orElseThrow();
            if (prod.getStock() < it.getCantidad())
                throw new IllegalStateException("Stock insuficiente de " + prod.getNombre());
            prod.setStock(prod.getStock() - it.getCantidad());
            productos.save(prod);
        }
        p.setEstado("CONFIRMADO");
        pedidos.save(p);
        return resumen(p);
    }

    @Tool(description = "Cancela un pedido que esté en BORRADOR.")
    @Transactional
    public String cancelarPedido(@ToolParam(description = "Id del pedido") Long pedidoId) {
        Pedido p = obtenerBorrador(pedidoId);
        p.setEstado("CANCELADO");
        pedidos.save(p);
        return "Pedido " + pedidoId + " cancelado.";
    }

    // ---- métodos auxiliares (sin @Tool, no se exponen) ----
    private Pedido obtenerBorrador(Long id) {
        Pedido p = pedidos.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el pedido " + id));
        if (!"BORRADOR".equals(p.getEstado()))
            throw new IllegalStateException("El pedido " + id + " ya está " + p.getEstado());
        return p;
    }

    private void recalcularTotal(Pedido p) {
        BigDecimal total = items.findByPedidoId(p.getId()).stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        p.setTotal(total);
        pedidos.save(p);
    }

    private ResumenPedido resumen(Pedido p) {
        List<LineaPedido> lineas = items.findByPedidoId(p.getId()).stream().map(i -> {
            String nombre = productos.findById(i.getProductoId()).map(Producto::getNombre).orElse("?");
            return new LineaPedido(nombre, i.getCantidad(), i.getPrecioUnitario(),
                    i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())));
        }).toList();
        return new ResumenPedido(p.getId(), p.getClienteNombre(), p.getDireccion(),
                p.getEstado(), lineas, p.getTotal());
    }
}