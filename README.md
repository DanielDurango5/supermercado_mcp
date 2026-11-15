# Laboratorio MCP: pedidos de supermercado a domicilio

## Autor

Daniel David Durango Durango

Servidor **MCP (Model Context Protocol)** construido con **Spring Boot + Spring AI** y conectado a **MySQL**, que permite hacer pedidos a domicilio de un supermercado desde un ChatBot (**Claude Desktop**) usando lenguaje natural.

Laboratorio de la asignatura Arquitectura de Software, Universidad de Antioquia (seccional suroeste). Docente: Diego José Luis Botia Valderrama.

## Arquitectura

```
Usuario → Claude Desktop (host + cliente MCP) → mcp-remote (STDIO ↔ HTTP)
        → Servidor MCP (Spring Boot + Spring AI, /mcp en el puerto 8088)
        → Spring Data JPA → MySQL (supermercado_mcp)
```

## Herramientas MCP

| Herramienta | Descripción |
|---|---|
| `listarProductos(categoria)` | Lista los productos con stock; filtra por categoría (opcional) |
| `buscarProductos(fragmento)` | Busca productos por nombre |
| `crearPedido(cliente, direccion, telefono)` | Crea un pedido en estado BORRADOR |
| `agregarProducto(pedidoId, productoId, cantidad)` | Agrega una línea validando stock y estado |
| `verPedido(pedidoId)` | Muestra el resumen y el total |
| `confirmarPedido(pedidoId)` | Confirma el pedido y descuenta el stock |
| `cancelarPedido(pedidoId)` | Cancela un pedido en BORRADOR |

Las reglas de negocio (stock, estados del pedido y totales) se validan en el servidor.

## Ejecución

1. **Base de datos:** ejecutar el script desde la raíz del proyecto.

   ```powershell
   mysql -u root -p --default-character-set=utf8mb4 -e "source src/main/resources/schema.sql"
   ```

2. **Servidor MCP:** definir la contraseña de MySQL como variable de entorno (no se guarda en el repositorio) y arrancar.

   ```powershell
   $env:PWD_UDEA = "tu_contraseña_de_mysql"
   .\mvnw.cmd spring-boot:run
   ```

   Debe aparecer `Registered tools: 7` y el servidor queda en `http://localhost:8088/mcp`.

3. **Probar con MCP Inspector:**

   ```powershell
   npx @modelcontextprotocol/inspector@0.12.0
   ```

   Transporte *Streamable HTTP*, URL `http://127.0.0.1:8088/mcp`.

4. **Conectar Claude Desktop:** agregar el servidor en `claude_desktop_config.json`, dentro de `mcpServers`, y reiniciar la aplicación.

   ```json
   "mcpServers": {
     "supermercado-mcp": {
       "command": "npx",
       "args": ["-y", "mcp-remote", "http://localhost:8088/mcp"]
     }
   }
   ```

## Ejemplo de uso

> **Usuario:** Hola, quiero hacer un pedido a domicilio
> **ChatBot:** (consulta el catálogo) Estos son los productos disponibles…
> **Usuario:** Quiero 3 leches y un cartón de huevos
> **ChatBot:** (crea el pedido, agrega los productos y muestra el resumen) ¿Lo confirmo?
> **Usuario:** Sí, confírmalo
> **ChatBot:** (confirmarPedido) Tu pedido quedó confirmado.
