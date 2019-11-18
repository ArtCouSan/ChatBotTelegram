package br.com.telegram.services.impl;

import java.util.List;
import java.util.Optional;

import br.com.telegram.enums.OperacaoEnum;
import br.com.telegram.services.OperacaoService;

public class OperacaoServiceImpl implements OperacaoService {

	private static MensagensServiceImpl mensagensService = new MensagensServiceImpl();

	private static Integer operacaoId = 0;
	private static Integer logado = 0;

	@Override
	public String executarOperacao(String mensagem) {

		List<String> mensagens = mensagensService.listarMensagensRecebidas(mensagem);
		List<String> palavras = mensagensService.listarPalavrasRecebidas(mensagens);
		List<String> operacoes = mensagensService.extrairOperacoes(palavras);

		if (logado == 0) {

			return login("cpf");

		} else {

			if (!operacoes.isEmpty()) {

			}

		}

		return null;
	}

	public String login(String mensagem) {

		logado = 1;

		return OperacaoEnum.login.getOperacaoMensagem();

	}

}
