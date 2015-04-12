package br.ufg.inf.fabrica.persistencia.teste;
import org.junit.*;
import org.junit.rules.ExpectedException;

import br.ufg.inf.fabrica.persistencia.FormatoUFG;

import java.util.UUID;

public class FormatoUFGTest {

    private static String classe;

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
    public void testClasseInexistenteGeraExcecao() throws Exception {
        thrown.expect(ClassNotFoundException.class);

        /* Lembre-se, não existe classe String, existe java.lang.String */
        FormatoUFG f = FormatoUFG.newInstance("String");
    }

    @Test
    public void testClasseNaoImplementaFormatoUFG() throws Exception {
        thrown.expect(ClassCastException.class);

        FormatoUFG f = FormatoUFG.newInstance("java.lang.String");
    }

    @Test
    public void testSemConstrutor() throws Exception {
        thrown.expect(InstantiationException.class);

        FormatoUFG f = FormatoUFG.newInstance("br.ufg.inf.fabrica.persistencia.FormatoUFG");
    }

    @Test
    public void testValorSemEntradaGeraExcecao() throws Exception {
        thrown.expect(IndexOutOfBoundsException.class);

        /* coleção vazia, contudo, por segurança, id único é gerado */
        /* Teste não é brinquedo, deve ser considerado seriamente. */
        String guid = UUID.randomUUID().toString();

        formatoUFG.valor(guid);
    }

    @Test
    public void testChaveDeveEliminarEspacosExtremosAntesDaBusca() {
        formatoUFG.insere("a"," b ");
        Assert.assertEquals("b", formatoUFG.valor(" a    "));
    }

    @Test
    public void testToStringColecaoVaziaGeraExcecao() throws Exception {
        thrown.expect(IllegalStateException.class);
        formatoUFG.toString();
    }

    @Test
    public void testToBase64ColecaoVaziaGeraExcecao() throws Exception {
        thrown.expect(IllegalStateException.class);
        formatoUFG.toBase64();
    }

    @Test
    public void testRemoveNullGeraExcecao() {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.remove(null);
    }

    @Test
    public void testRemoveChaveVaziaGeraExcecao() {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.remove("");
    }

    @Test
    public void testRemoveChaveInterpretadaComoVaziaGeraExcecao() {
        thrown.expect(IllegalArgumentException.class);

        formatoUFG.remove(" ");
    }

    @Test
    public void testChavesInexistentesRemoveSucesso() {
        Assert.assertTrue(formatoUFG.remove(UUID.randomUUID().toString()));
        Assert.assertTrue(formatoUFG.remove("="));
        Assert.assertTrue(formatoUFG.remove(" ; "));
    }

    @Test
    public void testChaveRemovidaNaoPermaneceNaColecao() {
        Assert.assertTrue(formatoUFG.insere("a", "b"));
        Assert.assertTrue(formatoUFG.remove("a"));
        Assert.assertTrue(formatoUFG.remove("a"));
        Assert.assertFalse(formatoUFG.existe("a"));
    }

    @Test
    public void testChavesInvalidasNaoPodemSerEncontradas() {
        Assert.assertFalse(formatoUFG.insere("=", ""));
        Assert.assertFalse(formatoUFG.insere(";", ""));
        Assert.assertFalse(formatoUFG.insere(" ", ""));

        Assert.assertFalse(formatoUFG.existe("="));
        Assert.assertFalse(formatoUFG.existe(" "));
        Assert.assertFalse(formatoUFG.existe(" ; "));
    }

    @Test
    public void testInsercaoDeChaveValorGrandes() {
        String chave = new String(new char[1024]).replace('\0', 'c');
        String valor = new String(new char[1024]).replace('\0', 'v');

        formatoUFG.insere(chave, valor);

        Assert.assertTrue(formatoUFG.existe(chave));
        Assert.assertEquals(valor, formatoUFG.valor(chave));
    }

    @Test
    public void testCicloCriaExportaVerificaTexto() throws Exception {
        formatoUFG.insere(" a ", " b ");

        String exportadoTexto = formatoUFG.toString();

        FormatoUFG importado = FormatoUFG.newInstance(classe);
        importado.acrescentaTexto(exportadoTexto);

        Assert.assertEquals("b", importado.valor("a"));
    }

    @Test
    public void testCicloCriaExportaVerificaBase64() throws Exception {
        formatoUFG.insere(" a ", " b ");

        String exportadoBase64 = formatoUFG.toBase64();

        FormatoUFG importado = FormatoUFG.newInstance(classe);
        importado.acrescentaBase64(exportadoBase64);

        Assert.assertEquals("b", importado.valor("a"));
    }
    
}