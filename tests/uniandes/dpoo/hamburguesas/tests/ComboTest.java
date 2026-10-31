package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest {

	private Combo comboCorral;
	private ArrayList<ProductoMenu> items;

	@BeforeEach
	void setUp() throws Exception {
		// combo corral;10%;corral;papas medianas;gaseosa
		items = new ArrayList<ProductoMenu>();
		items.add(new ProductoMenu("corral", 14000));
		items.add(new ProductoMenu("papas medianas", 5500));
		items.add(new ProductoMenu("gaseosa", 5000));

		comboCorral = new Combo("combo corral", 0.1, items);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNombre() {
		assertEquals("combo corral", comboCorral.getNombre(), "El nombre del combo no es el esperado.");
	}

	@Test
	void testGetPrecio() {
		// (14000 + 5500 + 5000) * 0.9 = 22050
		assertEquals(22050, comboCorral.getPrecio(),
				"El precio del combo debe ser la suma de sus productos menos el descuento.");
	}

	@Test
	void testGetPrecioSinDescuento() {
		Combo comboSinDescuento = new Combo("combo sin descuento", 0, items);
		assertEquals(24500, comboSinDescuento.getPrecio(),
				"Sin descuento, el precio del combo debe ser la suma de sus productos.");
	}

	@Test
	void testGetPrecioConDescuentoDecimal() {
		// combo especial;9.5%;especial;papas medianas;gaseosa
		ArrayList<ProductoMenu> itemsEspecial = new ArrayList<ProductoMenu>();
		itemsEspecial.add(new ProductoMenu("especial", 24000));
		itemsEspecial.add(new ProductoMenu("papas medianas", 5500));
		itemsEspecial.add(new ProductoMenu("gaseosa", 5000));
		Combo comboEspecial = new Combo("combo especial", 0.095, itemsEspecial);

		assertEquals(31222, comboEspecial.getPrecio(), "El precio del combo con descuento decimal no es el esperado.");
	}

	@Test
	void testGetPrecioComboVacio() {
		Combo comboVacio = new Combo("combo vacio", 0.1, new ArrayList<ProductoMenu>());
		assertEquals(0, comboVacio.getPrecio(), "Un combo sin productos debe costar 0.");
	}

	@Test
	void testConstructorCopiaLaLista() {
		items.add(new ProductoMenu("papas grandes", 6900));
		assertEquals(22050, comboCorral.getPrecio(),
				"Modificar la lista original no debe cambiar los productos del combo.");
	}

	@Test
	void testGenerarTextoFactura() {
		String esperado = "Combo combo corral\n" + " Descuento: 0.1\n" + "            22050\n";
		assertEquals(esperado, comboCorral.generarTextoFactura(), "El texto de la factura del combo no es el esperado.");
	}

}
