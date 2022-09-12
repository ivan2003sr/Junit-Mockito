package com.junit_app.ejemplos.models;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

import com.junit_app.ejemplos.exceptions.DineroInsuficienteException;

class CuentaTest {
	
	Cuenta cuenta;
	
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("Inicializando el test...");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("Finalizando el test...");
	}
	
	
	@BeforeEach
	void initMetodoTest() {

		this.cuenta = new Cuenta("Iván", new BigDecimal("1000.12345"));
		System.out.println("Iniciando el método.");
	}
	
	@AfterEach
	void tearDown() {
		System.out.println("Finalizando el método");
	}

	@Test
	void testNombreCuenta() {
		
		//cuenta.setPersona("Iván");real
		assertNotNull(cuenta.getSaldo(),()-> "La cuenta no puede ser nula");
		String esperado = "Iván";
		String real = cuenta.getPersona();
		
		assertEquals(esperado, real,()->"El nombre de la cuenta no es el que se esperaba. Se esperaba " + esperado + "sin embargo fue: " + real);
		assertTrue(real.equals("Iván"),()-> "Nombre esperado debe ser igual al real . Se esperaba " + esperado + " sin embargo fue: " + real); 
		
	}
	
	@Test
	void testSaldoCuenta() {
		
		assertNotNull(cuenta.getSaldo());
		assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
		assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO)<0);
		
		
	}
	
	@Test
	void testReferenciaCuenta() {
		cuenta = new Cuenta("Andrés", new BigDecimal("1900.4864"));
		Cuenta cuenta2 = new Cuenta("Andrés", new BigDecimal("1900.4864"));
		assertNotNull(cuenta.getSaldo());
		//assertNotEquals(cuenta2, cuenta);
		assertEquals(cuenta2, cuenta);
	}
	
	@Test
	void testDebitoCuenta() {
		
		cuenta.debito(new BigDecimal(100));
		assertNotNull(cuenta.getSaldo());
		assertEquals(900,cuenta.getSaldo().intValue());
		assertEquals("900.12345", cuenta.getSaldo().toPlainString());
		
		
	}
	@Test
	void testCreditoCuenta() {
		
		cuenta.credito(new BigDecimal(100));
		assertNotNull(cuenta.getSaldo());
		assertEquals(1100,cuenta.getSaldo().intValue());
		assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
		
		
	}
	
	@Test
	void testDineroinsuficienteException() {
		
		cuenta = new Cuenta("Andrés", new BigDecimal("1000.12345"));
		Exception exception = assertThrows(DineroInsuficienteException.class, ()->{
			cuenta.debito(new BigDecimal(1000.12345));
		});
		
		String actual = exception.getMessage();
		String esperado = "Dinero Insuficiente";
		assertEquals(esperado, actual);
		
	}
	
	@Test
	void testTransferirDineroCuenta() {
		Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
		Cuenta cuenta2 = new Cuenta("Iván", new BigDecimal("1500.8989"));
		Banco banco = new Banco();
		banco.setNombre("Nación");
		banco.transferir(cuenta1, cuenta2, new BigDecimal("500.58"));
		assertEquals("1999.42", cuenta1.getSaldo().toPlainString());
		assertEquals("2001.4789", cuenta2.getSaldo().toPlainString());
	}
	
	@Test
	void testRelacionBancoCuenta() {
		Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
		Cuenta cuenta2 = new Cuenta("Iván", new BigDecimal("1500.8989"));
		Banco banco = new Banco();
		
		banco.addCuenta(cuenta1);
		banco.addCuenta(cuenta2);
		
		banco.setNombre("Nación");
		banco.transferir(cuenta1, cuenta2, new BigDecimal("500.58"));
		
		assertAll(
				
				()->assertEquals("1999.42", cuenta1.getSaldo().toPlainString()),
				()->assertEquals("2001.4789", cuenta2.getSaldo().toPlainString()),
				()->assertEquals("2001.4789", cuenta2.getSaldo().toPlainString()),
				()->{assertEquals(2,banco.getCuentas().size());},
				()->assertEquals("Nación", cuenta1.getBanco().getNombre()),
				()->assertEquals("Iván",banco.getCuentas().stream()
						.filter(c->c.getPersona().equals("Iván")).findFirst().get().getPersona()),
				()->assertTrue(banco.getCuentas().stream()
						.anyMatch(c->c.getPersona().equals("Iván")))				
				);	
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testSoloWindows() {
		
	}
	
	@Test
	@EnabledOnOs({OS.LINUX, OS.MAC})
	void testSoloLinuxMac() {
		
	}
	
	
	@Test
	@DisabledOnOs(OS.WINDOWS)
	void testNoWindows(){
		
	}
	
	@Test
	@EnabledOnJre(JRE.JAVA_8)
	void soloJdk8() {
		
	}
	
	@Test
	@EnabledOnJre(JRE.JAVA_17)
	void soloJdk17() {
		
	}
	
	@Test
	@DisabledOnJre(JRE.JAVA_17)
	void testNoJdk17() {
		
	}
	
	
	@Test
	void imprimirSystemProperties() {
		Properties properties = System.getProperties();
		properties.forEach((k,v)->System.out.println(k+": "+v));
	}
	

	@Test
	@EnabledIfSystemProperty(named="java.version",matches=".*17.*")
	void  testJavaVersion() {
		
	}
	
	@Test
	@DisabledIfSystemProperty(named="os.arch", matches=".*32.*")
	void testSolo64() {
		
	}
	
	@Test
	@EnabledIfSystemProperty(named="os.arch", matches=".*32.*")
	void testSolo32() {
		
	}
	
	@Test
	@EnabledIfSystemProperty(named="user.name", matches=".*ivan.*")
	void testSoloIvan() {
		
	}
	
	@Test
	@EnabledIfSystemProperty(named="ENV", matches="dev")
	void testDev() {
		
	}
	
	@Test
	void imprimirVariablesAmbiente() {
		Map<String, String> getenv=System.getenv();
		getenv.forEach((k,v)->System.out.println(k+" = "+v));
	}
	
	@Test
	@EnabledIfEnvironmentVariable(named="JAVA_HOME", matches=".*jdk-17.0.3.1")
	void testJavaHome(){
		
	}
	
	@Test
	@EnabledIfEnvironmentVariable(named="NUMBER_OF_PROCESSORS", matches="8")
	void testProcesadores(){
		
	}
	
	@Test
	@EnabledIfEnvironmentVariable(named="ENVIROMENT", matches="dev")
	void testEnv() {
		
	}
	
	@Test
	void testSaldoCuentaDev() {
		boolean esDev = "dev".equals(System.getProperty("ENV"));
		assumeTrue(esDev);
		assertNotNull(cuenta.getSaldo());
		assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
		assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO)<0);
		
		
	}
	
	@Test
	void testSaldoCuentaDev2() {
		boolean esDev = "dev".equals(System.getProperty("ENV"));
	
		assumingThat(esDev,()->{
			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO)<0);		
		});

		
		
	}
	

	

}
