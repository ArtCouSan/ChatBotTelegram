package br.com.telegram.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.telegram.enums.OperacaoEnum;
import br.com.telegram.services.MensagensService;

public class MensagensServiceImpl implements MensagensService {

	/**
	 * Separa mensagem por /n recebido no chat
	 */
	@Override
	public List<String> listarMensagensRecebidas(String mensagens) {
		return Arrays.asList(mensagens.split("/n"));
	}

	@Override
	public List<String> listarPalavrasRecebidas(List<String> linhas) {

		List<String> palavras = new ArrayList<String>();

		linhas.forEach(linha -> {
			Arrays.asList(linha.split(" ")).forEach(palavra -> {
				palavras.add(palavra);
			});
		});

		return palavras;
	}

	public List<String> extrairOperacoes(List<String> palavras) {

		List<String> operacoes = palavras.stream().filter(palavra -> palavra.substring(0, 1).equalsIgnoreCase("/"))
				.collect(Collectors.toList());

		return operacoes;

	}

}
