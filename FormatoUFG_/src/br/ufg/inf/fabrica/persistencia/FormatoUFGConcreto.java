package br.ufg.inf.fabrica.persistencia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.binary.Base64;

/*
 * Essa classe utilizar o jar commons-codec-1.5.jar
 */

public class FormatoUFGConcreto extends FormatoUFG {
	public Map<String, String> colecaoChaveValor;

	public FormatoUFGConcreto() {
		colecaoChaveValor = new HashMap<String, String>();
	}

	public void estadoAtualDoMap(Map<String, String> colecaoChaveValor,
			Map<String, String> colecaoChaveValorEstadoAtual) {
		for (String chave : colecaoChaveValor.keySet()) {
			colecaoChaveValorEstadoAtual.put(chave,
					colecaoChaveValor.get(chave));
		}
	}

	@Override
	public void acrescentaTexto(String texto) {
		Map<String, String> colecaoChaveValorEstadoAtual = new HashMap<String, String>();
		estadoAtualDoMap(colecaoChaveValor, colecaoChaveValorEstadoAtual);

		if (texto == null || texto.trim().equals("")) {
			throw new IllegalArgumentException();
		} else {

			String linha = "";
			int contadorDeLinhasChaveValor = 0, caractere = 0, contadorLoop = 0;

			while (texto.length() > contadorLoop) {
				linha += texto.substring(contadorLoop, (contadorLoop + 1));
				contadorLoop++;
				/*
				 * O System.getProperty("line.separator") retorna \r\n, como
				 * dentro do while está lendo linha por linha, o argumento do
				 * .equals da sentença abaixo é \n.
				 */
				if (linha.substring(caractere).equals("\n")) {
					caractere = 0;
					linha = linha.trim();

					if (!(linha.isEmpty())) {

						if ((linha.substring(0, 1).equals(";"))) {
							linha = "";
						}

						else {

							if (ValidarLinhaChaveValor(linha)) {
								contadorDeLinhasChaveValor++;
								linha = "";
							} else {
								 /*caso haja alguma chave = valor invalida o
								   map volta ao seu estado anterior.*/
								colecaoChaveValor = colecaoChaveValorEstadoAtual;
								throw new IllegalArgumentException();
							}
						}
					}
				} else {
					caractere++;
				}

			}

			if (contadorDeLinhasChaveValor == 0) {
				throw new IllegalArgumentException();
			}
		}
	}

	public boolean ValidarLinhaChaveValor(String linha) {
		String chave = "", valor = "";
		int contadorDeIgual = 0;

		for (int i = 0; i < linha.length(); i++) {

			chave += linha.substring(i, (i + 1));

			if (chave.substring(i).equals("=")) {
				contadorDeIgual++;
				chave = chave.replace("=", "");
				valor += linha.substring((i + 1), linha.length());
				break;
			}
		}

		if (contadorDeIgual == 0) {
			return false;
		} else {
			return insere(chave, valor);
		}
	}

	@Override
	public void acrescentaBase64(String base64) {
		 String par ="", chave ="", valor="";
		 int tamanhoDoPar=0;
		 int flagDefineSeEChaveOuValor=1;
		 byte[]arrayDescomprimido = new byte[1024];
         byte[]arrayDecodificado=Base64.decodeBase64(base64);
		 ByteArrayInputStream bais = new ByteArrayInputStream(arrayDecodificado);
		 ZipInputStream zipIn = new ZipInputStream(bais);
		 try {
			ZipEntry entry = zipIn.getNextEntry();
			int i=0;
			while (zipIn.available() > 0){
		       arrayDescomprimido[i]=(byte) zipIn.read();
		       i++;
		    } 
			
			 for(int j=4; j<arrayDescomprimido.length; j++){
				 tamanhoDoPar+=(int)arrayDescomprimido[j];
				 
				 if(tamanhoDoPar>0){
				 while(tamanhoDoPar>0){
					 j++;
					 par+=(char)arrayDescomprimido[j];
					 tamanhoDoPar=0;
				 }
				 
				 }else{
					 j++;
				 }
				 
				if((flagDefineSeEChaveOuValor/2)!=1){
					chave+=par;
					par="";
					valor="";
					flagDefineSeEChaveOuValor++;
				}else{
					valor+=par;
					insere(chave,valor);
					chave="";
					valor="";
					par="";
					flagDefineSeEChaveOuValor++;
				}
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean existe(String chave) throws IllegalArgumentException {

		if (!(chave == null)) {
			chave = chave.trim();
			if (colecaoChaveValor.containsKey(chave)) {
				return true;
			} else {
				return false;
			}
		}

		throw new IllegalArgumentException();

	}

	@Override
	public boolean insere(String chave, String valor)
			throws IllegalArgumentException {

		if (!(chave == null) && !(valor == null)) {
			chave = chave.trim();
			valor = valor.trim();

			if ((chave.isEmpty())
					|| (chave.equals(System.getProperty("line.separator")))
					|| (chave.substring(0, 1).equals(";"))) {
				return false;
			}

			for (int i = 0; i < chave.length(); i++) {
				if (chave.substring(i, (i + 1)).equals("=")) {
					return false;
				}
			}

			if (existe(chave)) {
				return false;
			}

			colecaoChaveValor.put(chave, valor);
			return true;

		} else {
			throw new IllegalArgumentException();
		}

	}

	@Override
	public boolean remove(String chave) throws IllegalArgumentException {

		if ((chave != null) && !(chave.trim().isEmpty())) {
			chave = chave.trim();

			for (String chaveDaColecao : colecaoChaveValor.keySet()) {

				if (chaveDaColecao.equals(chave)) {
					colecaoChaveValor.remove(chave);
					break;
				}
			}
			return true;
		}

		throw new IllegalArgumentException();
	}

	@Override
	public String valor(String chave) throws IndexOutOfBoundsException,
			IllegalArgumentException {

		chave = chave.trim();

		if (colecaoChaveValor.keySet().contains(chave)) {
			return colecaoChaveValor.get(chave);
		}

		throw new IndexOutOfBoundsException();

	}

	@Override
	public String toString() throws IllegalStateException {
		String textoFormatoUFGTexto = "", valor = "", chaveValidaParaFormatoTexto = "";

		if (!colecaoChaveValor.keySet().isEmpty()) {

			for (String chave : colecaoChaveValor.keySet()) {
				valor += colecaoChaveValor.get(chave);
				chaveValidaParaFormatoTexto += chave.replace("=", "").trim();
				textoFormatoUFGTexto += chaveValidaParaFormatoTexto + "="
						+ valor + System.getProperty("line.separator");
			}
			return textoFormatoUFGTexto;
		}

		throw new IllegalStateException();

	}

	@Override
	public String toBase64() {
		if (colecaoChaveValor.keySet().isEmpty()) {
			throw new IllegalStateException();
		}
		byte f = (byte) 15;
		byte a = (byte) 10;
		byte c = (byte) 12;
		int tamanhoEmBytesDoPar = 0;
		@SuppressWarnings("unused")
		byte[] colecaoChaveValorCompactado;
		byte[] colecaoChaveValorEmByte = new byte[calcularTamanhoDoArrayDeBytes()];
		colecaoChaveValorEmByte[0] = f;
		colecaoChaveValorEmByte[1] = a;
		colecaoChaveValorEmByte[2] = c;
		colecaoChaveValorEmByte[3] = a;
		int posicaoInsercaonoArray = 4;

		for (String chave : colecaoChaveValor.keySet()) {
			tamanhoEmBytesDoPar += chave.getBytes().length;
			colecaoChaveValorEmByte[posicaoInsercaonoArray] = (byte) tamanhoEmBytesDoPar;
			posicaoInsercaonoArray++;
			for (int i = 0; i < tamanhoEmBytesDoPar; i++) {
				colecaoChaveValorEmByte[posicaoInsercaonoArray] = (byte) chave
						.charAt(i);
				posicaoInsercaonoArray++;
			}
			tamanhoEmBytesDoPar = 0;
			tamanhoEmBytesDoPar += colecaoChaveValor.get(chave).getBytes().length;
			colecaoChaveValorEmByte[posicaoInsercaonoArray] = (byte) tamanhoEmBytesDoPar;
			posicaoInsercaonoArray++;
			for (int i = 0; i < tamanhoEmBytesDoPar; i++) {
				colecaoChaveValorEmByte[posicaoInsercaonoArray] = (byte) colecaoChaveValor
						.get(chave).charAt(i);
				posicaoInsercaonoArray++;
			}
		}

		colecaoChaveValorCompactado = zip(colecaoChaveValorEmByte);
		return  Base64.encodeBase64String(colecaoChaveValorCompactado);
	}
	

	public byte[] zip(byte[] input) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry entry = new ZipEntry("output");
		entry.setSize(input.length);
		try {
			zos.putNextEntry(entry);
			zos.write(input);
			zos.closeEntry();
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	public int calcularTamanhoDoArrayDeBytes() {

		String colecaoFormatoTexto = "";
		int numeroDeParesDaColecao = 0;
		for (String chaveEncontrada : colecaoChaveValor.keySet()) {
			colecaoFormatoTexto += chaveEncontrada;
			colecaoFormatoTexto += colecaoChaveValor.get(chaveEncontrada);
			numeroDeParesDaColecao += 2;
		}

		return colecaoFormatoTexto.getBytes().length + numeroDeParesDaColecao
				+ 4;
	}
}
