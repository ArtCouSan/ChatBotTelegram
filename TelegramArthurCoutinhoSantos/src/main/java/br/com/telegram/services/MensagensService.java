package br.com.telegram.services;

import java.util.List;

public interface MensagensService {

	public List<String> listarMensagensRecebidas(String mensagens);

	public List<String> listarPalavrasRecebidas(List<String> linhas);

}
