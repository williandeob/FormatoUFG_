package br.ufg.inf.fabrica.persistencia;

/**
 * Define serviços para manipulação de conteúdo
 * no formato UFG, ou seja, para manipulação
 * de um dicionário (conjunto de pares
 * chave-valor).
 *
 * <p>O formato UFG faz uso de duas versões, texto e base64,
 * para serializar um dicionário. Essas versões
 * são devidamente detalhadas em documento próprio.
 * </p>
 *
 * <p>Um uso típico do formato UFG é a definição
 * de configurações para um sistema, onde cada
 * valor a ser armazenado é associado a uma chave,
 * que permite a recuperação posterior do valor
 * correspondente.</p>
 *
 * <p>O fluxo típico de uso é definido pela obtenção
 * de uma instância (@see #newInstance), realização de
 * inserções de valores no dicionário (@see #insere)
 * seguida da posterior serialização em arquivo texto
 * (@see #toString) ou na base64 (@see #toBase64).</p>
 *
 * <p>Uma instância desta classe pode "importar" o
 * conteúdo serializado de um dicionário em texto
 * (@see #acrescentaTexto) ou base64 (@see #acrescentaBase64).
 * </p>
 */
public abstract class FormatoUFG {

    /**
     * Obtém uma instância que implementa {@code FormatoUFG}. A coleção é
     * inicialmente vazia, ou seja, não possui nenhum par (chave, valor).
     *
     * <p>Uma instância desta classe mantém um "dicionário", ou seja,
     * um conjunto de pares (chave, valor). Esta "coleção" pode ser
     * serializada no formato UFG, tanto na versão texto quanto na
     * versão base64.</p>
     *
     * @param classe nome completo da classe, disponível no <i>classpath</i>
     *               da aplicação, que implementa {@code FormatoUFG}.
     * @return instância que implementa {@code FormatoUFG}.
     *
     * @throws java.lang.ClassNotFoundException se a classe não pode ser
     * encontrada.
     *
     * @throws java.lang.InstantiationException erro ao tentar criar uma
     * instância da classe indicada.
     *
     * @throws java.lang.IllegalAccessException se não há acesso à classe
     * indicada.
     *
     * @throws java.lang.ClassCastException instância obtida não implementa
     * a classe {@code FormatoUFG}.
     */
    public static FormatoUFG newInstance(String classe)
            throws ClassNotFoundException,
            IllegalAccessException,
            InstantiationException {
        Class c = Class.forName(classe);
        return (FormatoUFG)c.newInstance();
    }

    /**
     * Acrescenta à coleção representada pelo presente objeto o conteúdo do
     * {@code texto}, contendo entradas no formato UFG, versão texto.
     *
     * <p>Nesta inserção comentários e linhas em branco são eliminadas. Ou seja,
     * a inserção de um dado texto, em uma coleção vazia, seguida da exportação
     * por meio do método {@code #toString} não resulta necessariamente na entrada
     * fornecida.</p>
     *
     * @param texto a serialização de uma coleção de entradas no formato UFG,
     *              versão texto.
     *
     * @throws java.lang.IllegalArgumentException caso a sequência de
     * caracteres fornecida pelo argumento {@code texto} não esteja em
     * conformidade com o formato UFG, versão texto. Essa exceção também é
     * gerada caso uma entrada a ser inserida já exista, ou seja, a chave
     * correspondente já faz parte da coleção existente. Em qualquer caso
     * onde esta exceção deve ser gerada, a coleção (o presente objeto), não deve
     * ser alterado. Ou seja, se não for possível executar de forma satisfatória
     * o presente método, então, então o estado do objeto em questão deve
     * permanecer exatamente como antes da chamada. Esta exceção também deve
     * ser gerada caso o argumento fornecido seja {@code null}.
     *
     * @see #acrescentaBase64(String)
     */
    public abstract void acrescentaTexto(String texto);

    /**
     * Acrescenta à coleção representada pelo presente projeto, as entradas
     * correspondentes ao conteúdo no formato Base64 ({@code base64}).
     * @param base64 a serialização de uma coleção de entradas no formato UFG,
     *               versão base64.
     *
     * @throws java.lang.IllegalArgumentException caso a sequência de
     * caracteres fornecida em {@code base64} não esteja em conformidade com
     * o formato UFG, versão base64. Essa exceção também é gerada caso uma
     * entrada a ser inserida já exista, ou seja, a chave correspondente já
     * faz parte da coleção existente. A presente instância só é alterada
     * com o conteúdo fornecido caso esta exceção não seja gerada. Ou seja,
     * primeiro deve ser verificada a possibilidade de inserir o conteúdo
     * indicado e, só então, proceder a inserção, se possível.
     *
     * @see #acrescentaTexto(String)
     */
    public abstract void acrescentaBase64(String base64);

    /**
     * Verifica se a coleção contém uma entrada para a {@code chave} fornecida.
     *
     * @param chave a chave de uma suposta entrada a ser verificada. Espaços
     *              nos extremos são eliminados antes da verificação.
     *
     * @return {@code true} se e somente se há uma entrada na coleção
     * com a {@code chave} indicada.
     *
     * @throws java.lang.IllegalArgumentException se a {@code chave} é
     * {@code null}.
     *
     * @see #insere(String, String)
     * @see #remove(String)
     * @see #valor(String)
     */
    public abstract boolean existe(String chave);

    /**
     * Insere a chave e o valor correspondente na coleção de
     * pares.
     *
     * <p> Eventuais espaços em branco "nos extremos" devem ser eliminados,
     * tanto para a {@code chave} quanto para o {@code valor}.</p>
     *
     * @param chave sequência de caracteres que definem a chave.
     * @param valor sequência de caracteres que definem o valor
     *              correspondente à {@code chave}.
     *
     * @return O valor {@code true} se e somente se a chave e o
     * valor foram inseridos satisfatoriamente na coleção de pares, o
     * que significa que ambos estão em conformidade com o Formato UFG.
     *
     * @throws java.lang.IllegalArgumentException se a {@code chave} ou o
     * {@code valor} for {@code null}.
     *
     * @see #remove(String)
     * @see #valor(String)
     * @see #existe(String)
     */
    public abstract boolean insere(String chave, String valor);

    /**
     * Remove da coleção a entrada com a {@code chave}
     * fornecida.
     *
     * @param chave a chave cuja entrada correspondente será
     *              removida. Espaços nos extremos são ignorados
     *              antes de realizar a busca na coleção.
     *
     * @return {@code true} se e somente se: a entrada foi removida
     * ou se a {@code chave} não identifica uma entrada
     * da coleção.
     *
     * @throws java.lang.IllegalArgumentException se a {@code chave} é
     * {@code null} ou vazia. Observe que essa exceção também é
     * gerada quando a {@code chave} fornecida é composta apenas
     * por brancos, por exemplo, "  ".
     *
     * @see #existe(String)
     * @see #insere(String, String)
     * @see #valor(String)
     */
    public abstract boolean remove(String chave);

    /**
     * Recupera o valor correspondente à {@code chave}.
     *
     * @param chave a chave cujo valor correspondente é desejado. Espaços em
     *              branco nos extremos são eliminados antes da consulta.
     *
     * @return a sequência de caracteres correspondente à {@code chave}.
     *
     * @throws java.lang.IndexOutOfBoundsException caso a {@code chave} não
     * identifica nenhuma entrada na coleção.
     *
     * @see #existe(String)
     * @see #insere(String, String)
     * @see #remove(String)
     */
    public abstract String valor(String chave)
            throws IndexOutOfBoundsException;

    /**
     * Serializa a coleção de pares representada pela instância em uma
     * sequência de caracteres, formato UFG na versão texto.
     *
     * <p>A ordem dos pares produzida por este método é indefinida. Ou seja,
     * a ordem de inserção não necessariamente é aquela empregado pelo
     * presente método para gerar a saída esperada.</p>
     *
     * @return sequência de caracteres correspondente ao objeto, no formato
     * UFG, versão texto.
     *
     * @see #toBase64()
     *
     * @throws java.lang.IllegalStateException se a coleção está vazia. Por
     * exemplo, a instância foi criada e nenhuma inserção ou "importação"
     * realizada.
     */
    public abstract String toString();

    /**
     * Serializa a coleção de pares representada pela instância em uma
     * sequência de caracteres, formato UFG na versão base64.
     *
     * <p>A ordem dos pares produzida por este método é indefinida. Ou seja,
     * a ordem de inserção não necessariamente é aquela empregado pelo
     * presente método para gerar a saída esperada.</p>
     *
     * @return sequência de caracteres, na Base64, em conformidade com
     * o formato UFG, na versão base64.
     *
     * @see #toString()
     *
     * @throws java.lang.IllegalStateException se a coleção está vazia. Por
     * exemplo, a instância foi criada e nenhuma inserção ou "importação"
     * realizada.
     */
    public abstract String toBase64();
}