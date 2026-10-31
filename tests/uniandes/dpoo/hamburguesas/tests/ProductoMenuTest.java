package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest {
	private ProductoMenu producto1;

	@BeforeEach
	void setUp() throws Exception {
		producto1 = new ProductoMenu("hawaiana", 20000);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNombre() {
		assertEquals("hawaiana", producto1.getNombre(), "El nombre del producto no es el esperado.");
	}

	@Test
	void testGetPrecio() {
		assertEquals(20000, producto1.getPrecio(), "El precio del producto no es el esperado.");
	}

	@Test
	void testGenerarTextoFactura() {
		String esperado = "hawaiana\n" + "            20000\n";
		assertEquals(esperado, producto1.generarTextoFactura(),
				"El texto de la factura del producto no es el esperado.");
	}

	@Test
	void testGenerarTextoFacturaContieneNombreYPrecio() {
		ProductoMenu producto2 = new ProductoMenu("papas medianas", 5500);
		String factura = producto2.generarTextoFactura();
		assertTrue(factura.contains("papas medianas"), "La factura no incluye el nombre del producto.");
		assertTrue(factura.contains("5500"), "La factura no incluye el precio del producto.");
	}

}
