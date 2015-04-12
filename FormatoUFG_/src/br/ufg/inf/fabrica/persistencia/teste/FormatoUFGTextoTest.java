package br.ufg.inf.fabrica.persistencia.teste;

import org.junit.*;
import org.junit.rules.ExpectedException;

import br.ufg.inf.fabrica.persistencia.FormatoUFG;

/**
 * Testes específicos do Formato UFG, versão texto.
 */
public class FormatoUFGTextoTest {

    private static String classe;
    private String fimDeLinha = System.getProperty("line.separator");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private FormatoUFG formatoUFG;

    @BeforeClass
    public static void setUpClass() {
        classe = System.getenv("FORMATOUFGCLASS");
    }

    @Before
    public void setUp() throws Exception {
        /* executado antes de cada teste */
        formatoUFG = FormatoUFG.newInstance(classe);
    }

    @After
    public void tearDown() throws Exception {
        /* executado após cada teste */
    }

    @Test
    public void testNullFalha() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.acrescentaTexto(null);
    }

    @Test
    public void testTextoVazioFalha() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.acrescentaTexto("");
    }

    @Test
    public void testLinhaEmBrancoFalha() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.acrescentaTexto("   ");
    }

    @Test
    public void testApenasUmaLinhaComentarioFalha() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.acrescentaTexto("; comentário aqui!" + fimDeLinha);
    }

    @Test
    public void testLinhaSemFimDeLinhaEsperadoFalha() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.acrescentaTexto("a=b");
    }

    @Test
    public void testLinhaValidaInserida() throws Exception {
        formatoUFG.acrescentaTexto("a=b" + fimDeLinha);

        Assert.assertEquals("b", formatoUFG.valor("a"));
    }

    @Test
    public void testLinhaValidaComComentarioInserida() throws Exception {
        formatoUFG.acrescentaTexto(" ;a=;" + fimDeLinha + "a=;" + fimDeLinha);

        Assert.assertEquals(";", formatoUFG.valor("a"));
    }

    @Test
    public void testInsereVerificaRemove() throws Exception {
        Assert.assertFalse(formatoUFG.existe("chave1"));
        Assert.assertTrue(formatoUFG.insere("chave1", "valor1"));
        Assert.assertTrue(formatoUFG.existe("chave1"));

        Assert.assertFalse(formatoUFG.existe("chave2"));
        Assert.assertTrue(formatoUFG.insere("chave2", "valor2"));
        Assert.assertTrue(formatoUFG.existe("chave2"));

        Assert.assertFalse(formatoUFG.existe("chave3"));
        Assert.assertTrue(formatoUFG.insere("chave3", "valor3"));
        Assert.assertTrue(formatoUFG.existe("chave3"));

        Assert.assertTrue(formatoUFG.remove("chave1"));
        Assert.assertTrue(formatoUFG.remove("chave2"));
        Assert.assertTrue(formatoUFG.remove("chave3"));

        Assert.assertFalse(formatoUFG.existe("chave1"));
        Assert.assertFalse(formatoUFG.existe("chave2"));
        Assert.assertFalse(formatoUFG.existe("chave3"));
    }

    @Test
    public void testEspacosNosExtremosEliminados() {
        Assert.assertTrue(formatoUFG.insere(" a     ", "   b   "));
        Assert.assertEquals("b", formatoUFG.valor("a"));
    }

    @Test
    public void testEspacosInternosPreservados() {
        Assert.assertTrue(formatoUFG.insere(" o k ", " o k "));
        Assert.assertEquals("o k", formatoUFG.valor(" o k "));
    }

    @Test
    public void testChaveDevePossuirPeloMenosUmCaractere() {
        Assert.assertFalse(formatoUFG.insere("", "b"));
    }

    @Test
    public void testValorPodeSerVazio() {
        Assert.assertTrue(formatoUFG.insere("k", ""));
    }

    @Test
    public void testChaveNaoAdmiteRepeticao() {
        Assert.assertTrue(formatoUFG.insere("k", ""));
        Assert.assertFalse(formatoUFG.insere(" k ", ""));
    }

    @Test
    public void testMaiusculasDistintasDeMinusculas() {
        Assert.assertTrue(formatoUFG.insere("a","a"));
        Assert.assertTrue(formatoUFG.insere("A","a"));
    }

    @Test
    public void testChaveNaoPodeSerIgual() {
        Assert.assertFalse(formatoUFG.insere("=", ""));
    }

    @Test
    public void testChaveNaoPodePontoVirgula() {
        Assert.assertFalse(formatoUFG.insere(";", ""));
    }

    @Test
    public void testBrancoNaoPodeSerChave() {
        Assert.assertFalse(formatoUFG.insere(" ",""));
    }
}
