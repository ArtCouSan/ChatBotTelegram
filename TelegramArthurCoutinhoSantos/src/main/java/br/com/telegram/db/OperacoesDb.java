package br.com.telegram.db;

import java.util.Arrays;
import java.util.List;

/**
 * Classe para guardar infos de IA
 * @author Arthu
 *
 */
public class OperacoesDb {

	public List<String> depositar = Arrays.asList("depositar", "guardar", "deposito");
	public List<String> sacar = Arrays.asList("sacar", "retirar", "tirar");
	public List<String> saldo = Arrays.asList("saldo", "quanto");
	public List<String> transferir = Arrays.asList("transferir", "enviar");

}
