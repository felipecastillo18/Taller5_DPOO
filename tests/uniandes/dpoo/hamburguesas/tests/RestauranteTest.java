package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

class RestauranteTest {

	private Restaurante restaurante;
	private File facturaGenerada;

	private static final File INGREDIENTES = new File("./data/ingredientes.txt");
	private static final File MENU = new File("./data/menu.txt");
	private static final File COMBOS = new File("./data/combos.txt");
	private static final String CARPETA_PRUEBAS = "./data/pruebas/";

	@BeforeEach
	void setUp() throws Exception{
		restaurante = new Restaurante();
		facturaGenerada = null;
	}

	@AfterEach
	void tearDown() throws Exception{
		if (facturaGenerada != null) {
			facturaGenerada.delete();
		}
	}

	@Test
	void testRestauranteNuevoEstaVacio() {
		assertTrue(restaurante.getIngredientes().isEmpty(), "Un restaurante nuevo no debe tener ingredientes.");
		assertTrue(restaurante.getMenuBase().isEmpty(), "Un restaurante nuevo no debe tener productos.");
		assertTrue(restaurante.getMenuCombos().isEmpty(), "Un restaurante nuevo no debe tener combos.");
		assertTrue(restaurante.getPedidos().isEmpty(), "Un restaurante nuevo no debe tener pedidos.");
		assertNull(restaurante.getPedidoEnCurso(), "Un restaurante nuevo no debe tener un pedido en curso.");
	}

	// Carga de archivos

	@Test
	void testCargarInformacionMenu() throws Exception {
		restaurante.cargarInformacionRestaurante(INGREDIENTES, MENU, COMBOS);
		assertEquals(15, restaurante.getIngredientes().size(), "No se cargaron los ingredientes correctamente.");
		assertEquals(22, restaurante.getMenuBase().size(), "No se cargó el menú base correctamente.");
		assertEquals(4, restaurante.getMenuCombos().size(), "No se cargaron los combos correctamente.");
	}

	@Test
	void testCargarInformacionDatosDeLosElementos() throws Exception {
		restaurante.cargarInformacionRestaurante(INGREDIENTES, MENU, COMBOS);

		assertEquals("lechuga", restaurante.getIngredientes().get(0).getNombre(), "El primer ingrediente no es el esperado.");
		assertEquals(1000, restaurante.getIngredientes().get(0).getCostoAdicional(), "El costo del primer ingrediente no es el esperado.");

		ProductoMenu primerProducto = restaurante.getMenuBase().get(0);
		assertEquals("corral", primerProducto.getNombre(), "El primer producto no es el esperado.");
		assertEquals(14000, primerProducto.getPrecio(), "El precio del primer producto no es el esperado.");
	}

	@Test
	void testCargarCombosCalculaDescuento() throws Exception {
		restaurante.cargarInformacionRestaurante(INGREDIENTES, MENU, COMBOS);

		// combo corral;10%;corral;papas medianas;gaseosa -> 24500 * 0.9 = 22050
		Combo comboCorral = restaurante.getMenuCombos().get(0);
		assertEquals("combo corral", comboCorral.getNombre(), "El primer combo no es el esperado.");
		assertEquals(22050, comboCorral.getPrecio(), "El descuento del combo no se cargó bien.");

		// combo especial;9.5%;especial;papas medianas;gaseosa -> 34500 * 0.905 = 31222
		Combo comboEspecial = restaurante.getMenuCombos().get(3);
		assertEquals("combo especial", comboEspecial.getNombre(), "El último combo no es el esperado.");
		assertEquals(31222, comboEspecial.getPrecio(), "El descuento decimal del combo no se cargó bien.");
	}

	@Test
	void testCargarIngredientesRepetidos() {
		File archivo = new File(CARPETA_PRUEBAS + "ingredientes_repetidos.txt");
		assertThrows(IngredienteRepetidoException.class,
				() -> restaurante.cargarInformacionRestaurante(archivo, MENU, COMBOS));
	}

	@Test
	void testCargarIngredientesPrecioInvalido() {
		File archivo = new File(CARPETA_PRUEBAS + "ingredientes_precio_invalido.txt");
		assertThrows(NumberFormatException.class,
				() -> restaurante.cargarInformacionRestaurante(archivo, MENU, COMBOS));
	}

	@Test
	void testCargarIngredientesConLineaVacia() throws Exception {
		File archivo = new File(CARPETA_PRUEBAS + "ingredientes_con_linea_vacia.txt");
		restaurante.cargarInformacionRestaurante(archivo, MENU, COMBOS);
		assertEquals(2, restaurante.getIngredientes().size(), "Las líneas vacías se deben ignorar.");
	}

	@Test
	void testCargarMenuRepetido() {
		File archivo = new File(CARPETA_PRUEBAS + "menu_repetido.txt");
		assertThrows(ProductoRepetidoException.class,
				() -> restaurante.cargarInformacionRestaurante(INGREDIENTES, archivo, COMBOS));
	}

	@Test
	void testCargarMenuConLineaVacia() throws Exception {
		File menu = new File(CARPETA_PRUEBAS + "menu_con_linea_vacia.txt");
		// Sin combos, porque los combos reales usan productos que no están en este menú
		File combosVacio = new File(CARPETA_PRUEBAS + "combos_vacio.txt");
		restaurante.cargarInformacionRestaurante(INGREDIENTES, menu, combosVacio);
		assertEquals(2, restaurante.getMenuBase().size(), "Las líneas vacías se deben ignorar.");
		assertTrue(restaurante.getMenuCombos().isEmpty(), "Un archivo de combos vacío no debe cargar combos.");
	}

	@Test
	void testCargarCombosRepetidos() {
		File archivo = new File(CARPETA_PRUEBAS + "combos_repetidos.txt");
		assertThrows(ProductoRepetidoException.class,
				() -> restaurante.cargarInformacionRestaurante(INGREDIENTES, MENU, archivo));
	}

	@Test
	void testCargarCombosProductoFaltante() {
		File archivo = new File(CARPETA_PRUEBAS + "combos_producto_faltante.txt");
		assertThrows(ProductoFaltanteException.class,
				() -> restaurante.cargarInformacionRestaurante(INGREDIENTES, MENU, archivo));
	}

	@Test
	void testCargarCombosConLineaVacia() throws Exception {
		File archivo = new File(CARPETA_PRUEBAS + "combos_con_linea_vacia.txt");
		restaurante.cargarInformacionRestaurante(INGREDIENTES, MENU, archivo);
		assertEquals(2, restaurante.getMenuCombos().size(), "Las líneas vacías se deben ignorar.");
	}

	@Test
	void testCargarArchivoInexistente() {
		File archivo = new File(CARPETA_PRUEBAS + "no_existe.txt");
		assertThrows(FileNotFoundException.class,
				() -> restaurante.cargarInformacionRestaurante(archivo, MENU, COMBOS));
	}

	// Pedidos

	@Test
	void testIniciarPedido() throws Exception {
		restaurante.iniciarPedido("Felipe", "Calle 127");

		Pedido enCurso = restaurante.getPedidoEnCurso();
		assertNotNull(enCurso, "Después de iniciar un pedido debe haber un pedido en curso.");
		assertEquals("Felipe", enCurso.getNombreCliente(), "El pedido en curso no es del cliente esperado.");
	}

	@Test
	void testIniciarPedidoConPedidoEnCurso() throws Exception {
		restaurante.iniciarPedido("Felipe", "Calle 127");
		assertThrows(YaHayUnPedidoEnCursoException.class, () -> restaurante.iniciarPedido("Juan", "Carrera 15"));
		assertEquals("Felipe", restaurante.getPedidoEnCurso().getNombreCliente(),
				"El pedido en curso no debe cambiar si se lanza la excepción.");
	}

	@Test
	void testCerrarPedidoSinPedidoEnCurso() {
		assertThrows(NoHayPedidoEnCursoException.class, () -> restaurante.cerrarYGuardarPedido());
	}

	@Test
	void testCerrarYGuardarPedido() throws Exception {
		restaurante.iniciarPedido("Felipe", "Calle 127");
		Pedido pedido = restaurante.getPedidoEnCurso();
		pedido.agregarProducto(new ProductoMenu("corral", 14000));
		facturaGenerada = new File("./facturas/factura_" + pedido.getIdPedido() + ".txt");

		restaurante.cerrarYGuardarPedido();

		assertNull(restaurante.getPedidoEnCurso(), "Después de cerrar no debe haber pedido en curso.");
		assertTrue(facturaGenerada.exists(), "No se creó el archivo de la factura.");
		assertEquals(pedido.generarTextoFactura(), Files.readString(facturaGenerada.toPath()),
				"El archivo no tiene el texto de la factura.");
		assertEquals(1, restaurante.getPedidos().size(), "El pedido cerrado debe quedar en el histórico.");
		assertSame(pedido, restaurante.getPedidos().get(0), "El histórico debe tener el pedido que se cerró.");
	}

	@Test
	void testIniciarPedidoDespuesDeCerrar() throws Exception {
		restaurante.iniciarPedido("Felipe", "Calle 127");
		facturaGenerada = new File("./facturas/factura_" + restaurante.getPedidoEnCurso().getIdPedido() + ".txt");
		restaurante.cerrarYGuardarPedido();

		restaurante.iniciarPedido("Juan", "Carrera 15");
		assertEquals("Juan", restaurante.getPedidoEnCurso().getNombreCliente(),
				"Después de cerrar un pedido se debe poder iniciar otro.");
	}

}
