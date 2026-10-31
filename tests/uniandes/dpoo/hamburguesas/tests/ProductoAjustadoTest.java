package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest {
	private ProductoMenu productoBase;
	private ProductoAjustado productoAjustado;
	private Ingrediente queso;
	private Ingrediente tocineta;
	private Ingrediente tomate;

	@BeforeEach
	void setUp() throws Exception {
		productoBase = new ProductoMenu("hawaiana", 20000);
		productoAjustado = new ProductoAjustado(productoBase);
		queso = new Ingrediente("queso mozzarella", 2500);
		tocineta = new Ingrediente("tocineta express", 2500);
		tomate = new Ingrediente("tomate", 1000);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNombre() {
		assertEquals("hawaiana", productoAjustado.getNombre(), "El nombre debe ser el del producto base.");
	}

	@Test
	void testGetPrecioSinAjustes() {
		assertEquals(20000, productoAjustado.getPrecio(),
				"Sin ajustes, el precio debe ser igual al del producto base.");
	}

	@Test
	void testGetPrecioConAgregados() {
		productoAjustado.agregarIngrediente(queso);
		productoAjustado.agregarIngrediente(tocineta);
		assertEquals(25000, productoAjustado.getPrecio(),
				"El precio debe ser el del producto base más el de los ingredientes agregados.");
	}

	@Test
	void testGetPrecioConIngredienteRepetido() {
		productoAjustado.agregarIngrediente(queso);
		productoAjustado.agregarIngrediente(queso);
		assertEquals(25000, productoAjustado.getPrecio(),
				"Un ingrediente agregado varias veces se debe cobrar cada vez.");
	}

	@Test
	void testGetPrecioConEliminados() {
		productoAjustado.eliminarIngrediente(tomate);
		assertEquals(20000, productoAjustado.getPrecio(), "Eliminar ingredientes no debe reducir el precio.");
	}

	@Test
	void testGetPrecioConAgregadosYEliminados() {
		productoAjustado.agregarIngrediente(queso);
		productoAjustado.eliminarIngrediente(tomate);
		assertEquals(22500, productoAjustado.getPrecio(),
				"Solo los ingredientes agregados deben modificar el precio.");
	}

	@Test
	void testGenerarTextoFacturaSinAjustes() {
		String esperado = "hawaiana\n" + "            20000\n";
		assertEquals(esperado, productoAjustado.generarTextoFactura(),
				"El texto de la factura sin ajustes no es el esperado.");
	}

	@Test
	void testGenerarTextoFacturaConAjustes() {
		productoAjustado.agregarIngrediente(queso);
		productoAjustado.eliminarIngrediente(tomate);
		String esperado = "hawaiana\n" + "    +queso mozzarella                2500\n" + "    -tomate\n"
				+ "            22500\n";
		assertEquals(esperado, productoAjustado.generarTextoFactura(),
				"El texto de la factura con ajustes no es el esperado.");
	}

	@Test
	void testGenerarTextoFacturaUsaNombreDelProductoBase() {
		String factura = productoAjustado.generarTextoFactura();
		assertTrue(factura.startsWith("hawaiana"), "La factura debe empezar con el nombre del producto base.");
		assertFalse(factura.contains("ProductoMenu@"), "La factura no debe mostrar la referencia del objeto.");
	}

}
