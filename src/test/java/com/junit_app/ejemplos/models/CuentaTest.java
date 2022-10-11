package com.junit_app.ejemplos.models;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.junit_app.ejemplos.exceptions.DineroInsuficienteException;

class CuentaTest {

	Cuenta cuenta;

	private TestInfo testInfo;
	private TestReporter testReporter;

	@BeforeAll
	static void beforeAll() {
		System.out.println("Inicializando el test...");
	}

	@AfterAll
	static void afterAll() {
		System.out.println("Finalizando el test...");
	}

	@BeforeEach
	void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
		this.testInfo = testInfo;
		this.testReporter = testReporter;
		this.cuenta = new Cuenta("Iván", new BigDecimal("1000.12345"));
		testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " "
				+ testInfo.getTestMethod().get().getName() + " con las etiquetas (TAGS): " + testInfo.getTags());
	}

	@AfterEach
	void tearDown() {
		System.out.println("Finalizando el método");
	}

	@Tag("cuenta")
	@Nested
	@DisplayName("Probando atributos de cuenta")
	class CuentaTestNombreSaldo {

		@Test
		void testNombreCuenta() {
			System.out.println(testInfo.getTags());

			if (testInfo.getTags().contains("cuenta")) {
				System.out.println("Hacer algo con la etiqueta cuenta");
			}
			System.out.println("Ejecutando: " + testInfo.getDisplayName() + " "
					+ testInfo.getTestMethod().get().getName() + " con las etiquetas (TAGS): " + testInfo.getTags());
			// cuenta.setPersona("Iván");real
			assertNotNull(cuenta.getSaldo(), () -> "La cuenta no puede ser nula");
			String esperado = "Iván";
			String real = cuenta.getPersona();

			assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba. Se esperaba "
					+ esperado + "sin embargo fue: " + real);
			assertTrue(real.equals("Iván"), () -> "Nombre esperado debe ser igual al real . Se esperaba " + esperado
					+ " sin embargo fue: " + real);

		}

		@Test
		void testSaldoCuenta() {

			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);

		}

		@Test
		void testReferenciaCuenta() {
			cuenta = new Cuenta("Andrés", new BigDecimal("1900.4864"));
			Cuenta cuenta2 = new Cuenta("Andrés", new BigDecimal("1900.4864"));
			assertNotNull(cuenta.getSaldo());
			// assertNotEquals(cuenta2, cuenta);
			assertEquals(cuenta2, cuenta);
		}

	}

	class CuentaOperacionesTest {
		@Tag("cuenta")
		@Test
		void testDebitoCuenta() {

			cuenta.debito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(900, cuenta.getSaldo().intValue());
			assertEquals("900.12345", cuenta.getSaldo().toPlainString());

		}

		@Tag("cuenta")
		@Test
		void testCreditoCuenta() {

			cuenta.credito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(1100, cuenta.getSaldo().intValue());
			assertEquals("1100.12345", cuenta.getSaldo().toPlainString());

		}

		@Tag("cuenta")
		@Tag("error")
		@Test
		void testDineroinsuficienteException() {

			cuenta = new Cuenta("Andrés", new BigDecimal("1000.12345"));
			Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
				cuenta.debito(new BigDecimal(1000.12345));
			});

			String actual = exception.getMessage();
			String esperado = "Dinero Insuficiente";
			assertEquals(esperado, actual);

		}

		@Tag("cuenta")
		@Tag("banco")
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

					() -> assertEquals("1999.42", cuenta1.getSaldo().toPlainString()),
					() -> assertEquals("2001.4789", cuenta2.getSaldo().toPlainString()),
					() -> assertEquals("2001.4789", cuenta2.getSaldo().toPlainString()), () -> {
						assertEquals(2, banco.getCuentas().size());
					}, () -> assertEquals("Nación", cuenta1.getBanco().getNombre()),
					() -> assertEquals("Iván",
							banco.getCuentas().stream().filter(c -> c.getPersona().equals("Iván")).findFirst().get()
									.getPersona()),
					() -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Iván"))));
		}

	}

	@Nested
	class SistemaOperativoTest {
		@Test
		@EnabledOnOs(OS.WINDOWS)
		void testSoloWindows() {

		}

		@Test
		@EnabledOnOs({ OS.LINUX, OS.MAC })
		void testSoloLinuxMac() {

		}

		@Test
		@DisabledOnOs(OS.WINDOWS)
		void testNoWindows() {

		}

	}

	@Nested
	class JavaVersionTest {

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

	}

	@Nested
	class SystemPropertiesTest {

		@Test
		void imprimirSystemProperties() {
			Properties properties = System.getProperties();
			properties.forEach((k, v) -> System.out.println(k + ": " + v));
		}

		@Test
		@EnabledIfSystemProperty(named = "java.version", matches = ".*17.*")
		void testJavaVersion() {

		}

		@Test
		@DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
		void testSolo64() {

		}

		@Test
		@EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
		void testSolo32() {

		}

		@Test
		@EnabledIfSystemProperty(named = "user.name", matches = ".*ivan.*")
		void testSoloIvan() {

		}

		@Test
		@EnabledIfSystemProperty(named = "ENV", matches = "dev")
		void testDev() {

		}

	}

	@Nested
	class VariableAmbienteTest {
		@Test
		void imprimirVariablesAmbiente() {
			Map<String, String> getenv = System.getenv();
			getenv.forEach((k, v) -> System.out.println(k + " = " + v));
		}

		@Test
		@EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.0.3.1")
		void testJavaHome() {

		}

		@Test
		@EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
		void testProcesadores() {

		}

		@Test
		@EnabledIfEnvironmentVariable(named = "ENVIROMENT", matches = "dev")
		void testEnv() {

		}

		@Test
		void testSaldoCuentaDev() {
			boolean esDev = "dev".equals(System.getProperty("ENV"));
			assumeTrue(esDev);
			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);

		}

		@Test
		void testSaldoCuentaDev2() {
			boolean esDev = "dev".equals(System.getProperty("ENV"));

			assumingThat(esDev, () -> {
				assertNotNull(cuenta.getSaldo());
				assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
				assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
			});

		}
	}

	@Tag("param")
	@Nested
	class PruebasParametrizadasTest {
		@DisplayName("Probando Debito Cuenta Repetir!")
		@RepeatedTest(value = 5, name = "{displayName} - Repetición número {currentRepetition} de {totalRepetitions}")
		void testDebitoCuentaRep(RepetitionInfo info) {
			if (info.getCurrentRepetition() == 3) {
				System.out.println("Estamos en la repetición " + info.getCurrentRepetition());
			}
			cuenta.debito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(900, cuenta.getSaldo().intValue());
			assertEquals("900.12345", cuenta.getSaldo().toPlainString());

		}

		@ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@ValueSource(strings = { "100", "200", "300", "500", "700", "1000.12345", "1200" })
		void testDebitoCuentaValueSource(String monto) {

			cuenta.debito(new BigDecimal(monto));
			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}

		@ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvSource({ "1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345", "7,1200" })
		void testDebitoCuentaCsvSource(String index, String monto) {

			System.out.println(index + "->" + monto);

			cuenta.debito(new BigDecimal(monto));
			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}

		@ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvSource({ "200,100,Ivan,Andres", "250,200,Pepe,Pepe", "299,300,Ivan,Ivan", "400,500,Eve,Eve",
				"750,700,Juan,Juan", "1000.12345,1000.12345,Vale,Vale", "1300,1200,Ailen,Ailen" })
		void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {

			System.out.println(saldo + "->" + monto);
			cuenta.setSaldo(new BigDecimal(saldo));
			cuenta.debito(new BigDecimal(monto));
			cuenta.setPersona(actual);

			assertNotNull(cuenta.getSaldo());
			assertNotNull(cuenta.getPersona());
			assertEquals(esperado, actual);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}

		@ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvFileSource(resources = "/data.csv")
		void testDebitoCuentaCsvFileSource(String monto) {
			cuenta.debito(new BigDecimal(monto));
			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}

		@ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@CsvFileSource(resources = "/data2.csv")
		void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {

			System.out.println(saldo + "->" + monto);
			cuenta.setSaldo(new BigDecimal(saldo));
			cuenta.debito(new BigDecimal(monto));
			cuenta.setPersona(actual);

			assertNotNull(cuenta.getSaldo());
			assertNotNull(cuenta.getPersona());
			assertEquals(esperado, actual);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}

		@ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
		@MethodSource("montoList")
		void testDebitoCuentaMethodSource(String monto) {
			cuenta.debito(new BigDecimal(monto));
			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}


	}

	static  List<String> montoList() {
		return Arrays.asList("100", "200", "300", "500", "700", "1000.12345", "1200");

	}
	
	@Nested
	class TimeoutTest {
	
	@Test
	@Tag("timeout")
	@Timeout(1)
	void pruebaTimeout() throws InterruptedException {
		TimeUnit.SECONDS.sleep(2);
	}
	
	@Test
	@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
	void pruebaTimeout2() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(501);
	}
	
	@Test
	void testTimeoutAssertiones() {
		assertTimeout(Duration.ofSeconds(1), ()->{
			TimeUnit.MILLISECONDS.sleep(999);
		});
	}
	}
}
