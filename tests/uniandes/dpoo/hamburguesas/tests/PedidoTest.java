package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class PedidoTest {
	private Pedido pedido;
	private File archivoFactura;
	
	
	@BeforeEach
	void setUp() throws Exception {
		pedido = new Pedido("Felipe", "Calle 127");
		archivoFactura = new File("./factura_prueba.txt");
	}
	
	@AfterEach
	void tearDown() throws Exception {
		archivoFactura.delete(); // limpia el archivo que creó la prueba
	}

	@Test
	void testGetNombreCliente() {
		assertEquals("Felipe", pedido.getNombreCliente(), "El nombre del cliente no coincide.");
	}

	@Test
	void testIdsConsecutivos() {
		Pedido otro = new Pedido("Juan", "Carrera 15");
		assertEquals(pedido.getIdPedido() + 1, otro.getIdPedido(), "Los ids no son consecutivos");
	}
	
	@Test
	void testGetPrecioTotalUnProducto() {
		ProductoMenu producto1 = new ProductoMenu("corral", 14000);
		pedido.agregarProducto(producto1);
		int precio = 16660;
		assertEquals(precio, pedido.getPrecioTotalPedido(), "El precio total no es correcto");
	}
	
	@Test
	void testGenerarTextoFactura() {
		ProductoMenu producto2 = new ProductoMenu("corral", 14000);
		pedido.agregarProducto(producto2);
		
		String textoEsperado = "Cliente: Felipe\n" 
						+ "Dirección: Calle 127\n" 
						+ "----------------\n" 
						
						+ "corral\n"
						+ "            14000\n"
		
						+ "----------------\n" 
						+ "Precio Neto:  14000\n"
						+ "IVA:          2660\n"
						+ "Precio Total: 16660\n";
		
		assertEquals(textoEsperado, pedido.generarTextoFactura(), "El texto de la factura no es el esperado.");
	}
	
	@Test
	void testGuardarFactura() throws Exception{
		ProductoMenu producto3 = new ProductoMenu("corral", 14000);
		pedido.agregarProducto(producto3);
		pedido.guardarFactura(archivoFactura);
		
		assertTrue(archivoFactura.exists(), "El archivo de la factura no se creó");
		
		String contenido = Files.readString(archivoFactura.toPath());
		
		assertEquals(pedido.generarTextoFactura(), contenido, "El archivo no tiene el texto esperado de la factura.");
		
	}
	
	@Test
	void testGuardarFacturaCarpetaInexistente() {
		File archivo = new File("./carpeta1112222/factura.txt");
		assertThrows(FileNotFoundException.class, () -> pedido.guardarFactura(archivo));
	}

	@Test
	void testGetPrecioTotalPedidoVacio() {
		assertEquals(0, pedido.getPrecioTotalPedido(), "Un pedido sin productos debe costar 0.");
	}

	@Test
	void testGenerarTextoFacturaPedidoVacio() {
		String textoEsperado = "Cliente: Felipe\n"
						+ "Dirección: Calle 127\n"
						+ "----------------\n"
						+ "----------------\n"
						+ "Precio Neto:  0\n"
						+ "IVA:          0\n"
						+ "Precio Total: 0\n";

		assertEquals(textoEsperado, pedido.generarTextoFactura(), "La factura de un pedido vacío no es la esperada.");
	}

	@Test
	void testGetPrecioTotalIVAConDecimales() {
		// combo especial: (24000 + 5500 + 5000) * 0.905 = 31222
		pedido.agregarProducto(crearComboEspecial());

		// IVA: 31222 * 0.19 = 5932.18, pero evita decimeales entonces 5932 -> total 31222 + 5932 = 37154
		assertEquals(37154, pedido.getPrecioTotalPedido(), "El IVA debe truncarse a un valor entero.");
	}

	@Test
	void testGetPrecioTotalVariosTiposDeProducto() {
		agregarProductosDeVariosTipos();

		// neto: 14000 + 22050 + 22500 = 58550; IVA: 58550 * 0.19 = 11124.5 -> 11124
		assertEquals(69674, pedido.getPrecioTotalPedido(),
				"El total debe sumar productos del menú, combos y productos ajustados más el IVA.");
	}

	@Test
	void testGenerarTextoFacturaVariosTiposDeProducto() {
		agregarProductosDeVariosTipos();

		String textoEsperado = "Cliente: Felipe\n"
						+ "Dirección: Calle 127\n"
						+ "----------------\n"

						+ "corral\n"
						+ "            14000\n"

						+ "Combo combo corral\n"
						+ " Descuento: 0.1\n"
						+ "            22050\n"

						+ "hawaiana\n"
						+ "    +queso mozzarella                2500\n"
						+ "    -cebolla\n"
						+ "            22500\n"

						+ "----------------\n"
						+ "Precio Neto:  58550\n"
						+ "IVA:          11124\n"
						+ "Precio Total: 69674\n";

		assertEquals(textoEsperado, pedido.generarTextoFactura(),
				"La factura con varios tipos de producto no es la esperada.");
	}

	@Test
	void testGuardarFacturaSobrescribeArchivo() throws Exception {
		pedido.agregarProducto(new ProductoMenu("corral", 14000));
		pedido.guardarFactura(archivoFactura);

		pedido.agregarProducto(new ProductoMenu("gaseosa", 5000));
		pedido.guardarFactura(archivoFactura);

		String contenido = Files.readString(archivoFactura.toPath());
		assertEquals(pedido.generarTextoFactura(), contenido,
				"Al guardar de nuevo, el archivo debe tener la factura actualizada.");
	}

	/**
	 * Agrega al pedido un producto del menú, un combo y un producto ajustado
	 */
	private void agregarProductosDeVariosTipos() {
		pedido.agregarProducto(new ProductoMenu("corral", 14000));

		// combo corral: (14000 + 5500 + 5000) * 0.9 = 22050
		ArrayList<ProductoMenu> itemsCombo = new ArrayList<ProductoMenu>();
		itemsCombo.add(new ProductoMenu("corral", 14000));
		itemsCombo.add(new ProductoMenu("papas medianas", 5500));
		itemsCombo.add(new ProductoMenu("gaseosa", 5000));
		pedido.agregarProducto(new Combo("combo corral", 0.1, itemsCombo));

		// hawaiana + queso mozzarella - cebolla: 20000 + 2500 = 22500
		ProductoAjustado hawaiana = new ProductoAjustado(new ProductoMenu("hawaiana", 20000));
		hawaiana.agregarIngrediente(new Ingrediente("queso mozzarella", 2500));
		hawaiana.eliminarIngrediente(new Ingrediente("cebolla", 1000));
		pedido.agregarProducto(hawaiana);
	}

	private Combo crearComboEspecial() {
		ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>();
		items.add(new ProductoMenu("especial", 24000));
		items.add(new ProductoMenu("papas medianas", 5500));
		items.add(new ProductoMenu("gaseosa", 5000));
		return new Combo("combo especial", 0.095, items);
	}

}
