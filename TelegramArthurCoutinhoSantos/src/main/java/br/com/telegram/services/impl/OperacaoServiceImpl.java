package br.com.telegram.services.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.telegram.enums.OperacaoEnum;
import br.com.telegram.services.OperacaoService;

public class OperacaoServiceImpl implements OperacaoService {

	private static MensagensServiceImpl mensagensService = new MensagensServiceImpl();

	private static Integer operacaoId = 0;
	private static Boolean logado = false;
	private static Boolean primeiraVez = true;
	private static BigDecimal saldo = new BigDecimal(0);

	@Override
	public String executarOperacao(String mensagem) {

		List<String> mensagens = mensagensService.listarMensagensRecebidas(mensagem);
		List<String> palavras = mensagensService.listarPalavrasRecebidas(mensagens);
		List<String> operacoes = mensagensService.extrairOperacoes(palavras);

		// Verifica se esta logado
		if (logado) {

			// Verifica se nao tem operação
			if (!operacoes.isEmpty()) {

				// Verifica se tem operação em aberto e apresenta menu
				if (operacaoId.compareTo(OperacaoEnum.menu.getOperacao()) != 0 && !(menu(operacoes.get(0)))) {

					return OperacaoEnum.operacaoEmAberto.getOperacaoMensagem()
							.concat(OperacaoEnum.valueOf(operacaoId).getOperacaoMensagem());

					// Retorna operação
				} else {

					return operacoes(operacoes.get(0));

				}

			} else {

				// Verifica se tem operação em abertp
				if (operacaoId.compareTo(OperacaoEnum.menu.getOperacao()) != 0) {

					return operacaoExecutar(operacaoId, mensagem);

				}

			}

		} else {

			return login(mensagem);

		}

		return null;

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

	/**
	 * Retorna a mensagem de entrada para uma operacao
	 * 
	 * @param mensagem
	 * @return
	 */
	public String operacoes(String mensagem) {

		switch (OperacaoEnum.valueOf(mensagem)) {
		case depositar:

			operacaoId = OperacaoEnum.depositar.getOperacao();
			return OperacaoEnum.depositar.getOperacaoMensagem();
		case sacar:
			
			operacaoId = OperacaoEnum.sacar.getOperacao();
			return OperacaoEnum.sacar.getOperacaoMensagem();
		case menu:

			operacaoId = OperacaoEnum.menu.getOperacao();
			return OperacaoEnum.menu.getOperacaoMensagem();
		default:
			return OperacaoEnum.naoEntendi.getOperacaoMensagem();
		}

	}

	/**
	 * Realiza a operacao
	 * 
	 * @param idOperacao
	 * @param valor
	 * @return
	 */
	public String operacaoExecutar(Integer idOperacao, String valorString) {

		try {

			BigDecimal valor = new BigDecimal(valorString);

			// Verifica se eh positivo
			if (valorValido(valor)) {

				// Moeda
				String valorMoeda = tranformarMoeda(valor);
				// Mensagem de sucesso
				String mensagem = String.format("%s", valorMoeda);

				switch (OperacaoEnum.valueOf(idOperacao)) {
				case depositar:

					saldo = saldo.add(valor);
					mensagem = mensagem.concat(" depositado, retornando ao menu... \n")
							.concat(OperacaoEnum.menu.getOperacaoMensagem());
					operacaoId = OperacaoEnum.menu.getOperacao();

					break;
				case sacar:

					if (valorEmCaixa(valor)) {

						saldo = saldo.subtract(valor);
						mensagem = mensagem.concat(" sacado, retornando ao menu... \n")
								.concat(OperacaoEnum.menu.getOperacaoMensagem());
						operacaoId = OperacaoEnum.menu.getOperacao();

					} else {

						return "Valor em conta inferior";

					}

					break;
				default:

					break;
				}

				return mensagem;

			} else {

				return "Valor negativo";

			}

		} catch (Exception e) {

			return "Valor incorreto";

		}

	}

	/**
	 * Verifica se valor eh maior que o em conta
	 * 
	 * @param valor
	 * @return
	 */
	public boolean valorEmCaixa(BigDecimal valor) {
		if (valor.compareTo(saldo) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * Verifica se valor eh positivo
	 * 
	 * @param valor
	 * @return
	 */
	public boolean valorValido(BigDecimal valor) {
		if (valor.compareTo(BigDecimal.ZERO) < 1) {
			return false;
		}
		return true;
	}

	/**
	 * Transforma em moeda
	 * 
	 * @param valor
	 * @return
	 */
	public String tranformarMoeda(BigDecimal valor) {
		Locale ptBr = new Locale("pt", "BR");
		return NumberFormat.getCurrencyInstance(ptBr).format(valor);
	}

	/**
	 * Verifica se eh menu
	 * 
	 * @param operacao
	 * @return
	 */
	public boolean menu(String operacao) {
		return OperacaoEnum.menu.name().equalsIgnoreCase(operacao);
	}

}
