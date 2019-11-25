package br.com.telegram.services.impl;

import java.util.List;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.telegram.enums.OperacaoEnum;
import br.com.telegram.services.OperacaoService;

public class OperacaoServiceImpl implements OperacaoService {

	private static MensagensServiceImpl mensagensService = new MensagensServiceImpl();

	private static Integer operacaoId = 0;
	private static Boolean logado = false;
	private static Boolean primeiraVez = true;

	@Override
	public String executarOperacao(String mensagem) {

		List<String> mensagens = mensagensService.listarMensagensRecebidas(mensagem);
		List<String> palavras = mensagensService.listarPalavrasRecebidas(mensagens);
		List<String> operacoes = mensagensService.extrairOperacoes(palavras);

		if (logado) {

			if (!operacoes.isEmpty()) {

				if (operacaoId.compareTo(OperacaoEnum.menu.getOperacao()) != 0
						&& !(menu(operacoes.get(0)))) {

					return OperacaoEnum.operacaoEmAberto.getOperacaoMensagem()
							.concat(OperacaoEnum.valueOf(operacaoId).getOperacaoMensagem());

				} else {

					return operacoes(operacoes.get(0));

				}

			}

			return null;

		} else {

			return login(mensagem);

		}

	}

	public String login(String mensagem) {

		if (primeiraVez) {

			primeiraVez = false;

			return mensagensService.mensagemBoasVindas();

		} else {

			if (validaCPF(mensagem)) {

				logado = true;
				operacaoId = OperacaoEnum.menu.getOperacao();
				return OperacaoEnum.bemVindo.getOperacaoMensagem();

			} else {

				operacaoId = OperacaoEnum.errologin.getOperacao();
				return OperacaoEnum.errologin.getOperacaoMensagem();

			}

		}

	}

	public boolean validaCPF(String cpf) {

		CPFValidator validador = new CPFValidator();

		if (validador.isEligible(cpf)) {

			return true;

		}

		return false;

	}

	public String operacoes(String mensagem) {

		switch (OperacaoEnum.valueOf(mensagem)) {
		case depositar:

			operacaoId = OperacaoEnum.depositar.getOperacao();
			return OperacaoEnum.depositar.getOperacaoMensagem();
		case menu:

			operacaoId = OperacaoEnum.menu.getOperacao();
			return OperacaoEnum.menu.getOperacaoMensagem();
		default:
			return OperacaoEnum.naoEntendi.getOperacaoMensagem();
		}

	}

	public boolean menu(String operacao) {
		return OperacaoEnum.menu.name().equalsIgnoreCase(operacao);
	}

}
