package br.com.telegram.services.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.telegram.services.MensagensService;

public class MensagensServiceImpl implements MensagensService {

	/**
	 * Separa mensagem por /n recebido no chat
	 */
	@Override
	public List<String> listarMensagensRecebidas(String mensagens) {
		return Arrays.asList(mensagens.split("/n"));
	}

	/**
	 * Separa palavras recebidas
	 */
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

	/**
	 * Extrai operações na mensagem
	 * @param palavras
	 * @return
	 */
	public List<String> extrairOperacoes(List<String> palavras) {

		List<String> operacoes = palavras.stream().filter(palavra -> palavra.substring(0, 1).equalsIgnoreCase("/"))
				.map(palavra -> palavra.replace("/", "")).collect(Collectors.toList());

		return operacoes;

	}

	/**
	 * Mensagem de boas vindas
	 * @return
	 */
	public String mensagemBoasVindas() {

		LocalTime time = LocalTime.now();

		if (time.isAfter(LocalTime.of(5, 30)) && time.isBefore(LocalTime.of(11, 59))) {

			return "Bom dia\nPor favor, digite seu CPF:";

		} else if (time.isAfter(LocalTime.of(11, 59)) && time.isBefore(LocalTime.of(17, 59))) {

			return "Boa tarde\nPor favor, digite seu CPF:";

		} else {

			return "Boa noite\nPor favor, digite seu CPF:";

		}

	}

}
