package br.com.telegram.services.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.telegram.db.OperacoesDb;
import br.com.telegram.enums.OperacaoEnum;
import br.com.telegram.services.OperacaoService;

public class OperacaoServiceImpl implements OperacaoService {

	private static MensagensServiceImpl mensagensService = new MensagensServiceImpl();
	private static OperacoesDb operacaoDb = new OperacoesDb();
	private static Integer operacaoId = 0;
	private static Boolean logado = false;
	private static Boolean primeiraVez = true;
	private static BigDecimal saldo = new BigDecimal(0);

	/**
	 * Realiza operações principais
	 */
	@Override
	public String executarOperacao(String mensagem) {

		try {

			List<String> mensagens = mensagensService.listarMensagensRecebidas(mensagem);
			List<String> palavras = mensagensService.listarPalavrasRecebidas(mensagens);
			List<String> operacoes = mensagensService.extrairOperacoes(palavras);

			// Verifica se esta logado
			if (logado) {

				// Verifica se pedido operação
				if (!operacoes.isEmpty()) {

					return operacaoComComando(operacoes);

				} else {

					// Verifica mensagem ou executa operacao
					return operacaoSemComando(mensagem, palavras);

				}

			} else {

				return login(mensagem);

			}

		} catch (Exception e) {
			
			return "Não entendi";

		}

	}

	/**
	 * Se usuario não usa um comando ou quer executar uma operacao
	 * 
	 * @param mensagem
	 * @return mensagem
	 */
	private String operacaoSemComando(String mensagem, List<String> palavras) {

		// Verifica se executa operacao
		if (operacaoId.compareTo(OperacaoEnum.menu.getOperacao()) != 0) {

			return executarTransacao(operacaoId, mensagem);

		} else {

			return verificarOqueUsuarioQuerPelaMensagem(palavras);

		}

	}

	/**
	 * Se usuario use um comando
	 * 
	 * @param operacoes
	 * @return
	 */
	private String operacaoComComando(List<String> operacoes) {

		// Verifica se tem operação em aberto e apresenta menu
		if (operacaoId.compareTo(OperacaoEnum.menu.getOperacao()) != 0 && !(ehMenu(operacoes.get(0)))) {

			return OperacaoEnum.operacaoEmAberto.getOperacaoMensagem()
					.concat(OperacaoEnum.valueOf(operacaoId).getOperacaoMensagem());

		} else {

			return menu(operacoes.get(0));

		}

	}

	/**
	 * Verifica o que a mensagem quer pelas palavras
	 * @param palavras
	 * @return
	 */
	public String verificarOqueUsuarioQuerPelaMensagem(List<String> palavras) {

		Integer querDepositar = 0;
		Integer querSacar = 0;
		Integer querTransferir = 0;
		Integer querSaldo = 0;

		for (String palavra : palavras) {

			querDepositar = existeSinonimo(palavra, querDepositar, operacaoDb.depositar);
			querSacar = existeSinonimo(palavra, querSacar, operacaoDb.sacar);
			querTransferir = existeSinonimo(palavra, querTransferir, operacaoDb.transferir);
			querSaldo = existeSinonimo(palavra, querSaldo, operacaoDb.saldo);

		}

		TreeMap<Integer, String> opcoes = new TreeMap<Integer, String>();
		opcoes.put(querDepositar, OperacaoEnum.depositar.name());
		opcoes.put(querSacar, OperacaoEnum.sacar.name());
		opcoes.put(querTransferir, OperacaoEnum.transferir_para.name());
		opcoes.put(querSaldo, OperacaoEnum.saldo.name());

		if (opcoes.size() == 1) {

			return OperacaoEnum.naoEntendi.getOperacaoMensagem();

		} else {

			return menu(opcoes.get(opcoes.lastKey()));

		}

	}

	/**
	 * Verifica se a palavra contem sinonimo
	 * 
	 * @param palavra
	 * @param querOque
	 * @param sinonimos
	 * @return se tem o que quer
	 */
	public Integer existeSinonimo(String palavra, Integer querOque, List<String> sinonimos) {

		Boolean existe = sinonimos.contains(palavra);

		if (existe) {
			querOque++;
		}

		return querOque;

	}

	/**
	 * Realiza login
	 * 
	 * @param mensagem
	 * @return
	 */
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

	/**
	 * Valida o cpf
	 * 
	 * @param cpf
	 * @return
	 */
	public boolean validaCPF(String cpf) {

		CPFValidator validador = new CPFValidator();
		cpf = cpf.replaceAll("[.-]", "");

		if (validador.isEligible(cpf)) {

			return true;

		}

		return false;

	}

	/**
	 * Menu de operações
	 * 
	 * @param mensagem
	 * @return a operação
	 */
	public String menu(String mensagem) {

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
		case saldo:

			return OperacaoEnum.saldo.getOperacaoMensagem().concat(tranformarMoeda(saldo));
		case transferir_para:

			operacaoId = OperacaoEnum.transferir_para.getOperacao();
			return OperacaoEnum.transferir_para.getOperacaoMensagem();
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
	public String executarTransacao(Integer idOperacao, String valorString) {

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

					mensagem = depositar(valor, mensagem);

					break;
				case sacar:

					mensagem = sacar(valor, mensagem);

					break;
				case saldo:

					mensagem = saldo();

					break;
				case transferir:

					mensagem = transferir(valor, mensagem);

					break;
				case transferir_para:

					mensagem = transferirPara(valorString);

					break;
				default:

					break;
				}

				return mensagem;

			} else {

				return "Valor negativo, se deseja voltar para o menu digite /menu";

			}

		} catch (Exception e) {

			return "Valor incorreto, se deseja voltar para o menu digite /menu";

		}

	}

	/**
	 * Pegar saldo
	 * 
	 * @return saldo
	 */
	private String saldo() {

		return "Saldo em conta ".concat(tranformarMoeda(saldo));

	}

	/**
	 * Verifica usuario que recebe tranferencia
	 * 
	 * @param valorString
	 * @return
	 */
	private String transferirPara(String valorString) {

		String mensagem;

		if (validaCPF(valorString)) {

			operacaoId = OperacaoEnum.transferir.getOperacao();
			mensagem = OperacaoEnum.transferir.getOperacaoMensagem();

		} else {

			mensagem = "CPF inválido, se deseja voltar para o menu digite /menu";

		}

		return mensagem;

	}

	/**
	 * Transferir
	 * 
	 * @param valor
	 * @param mensagem
	 * @return
	 */
	private String transferir(BigDecimal valor, String mensagem) {

		if (valorEmCaixa(valor)) {

			saldo = saldo.subtract(valor);
			mensagem = mensagem.concat(" tranferido, retornando ao menu... \n")
					.concat(OperacaoEnum.menu.getOperacaoMensagem());
			operacaoId = OperacaoEnum.menu.getOperacao();

		} else {

			mensagem = "Valor em conta inferior, se deseja voltar para o menu digite /menu";

		}

		return mensagem;

	}

	/**
	 * Sacar
	 * 
	 * @param valor
	 * @param mensagem
	 * @return
	 */
	private String sacar(BigDecimal valor, String mensagem) {

		if (valorEmCaixa(valor)) {

			saldo = saldo.subtract(valor);
			mensagem = mensagem.concat(" sacado, retornando ao menu... \n")
					.concat(OperacaoEnum.menu.getOperacaoMensagem());
			operacaoId = OperacaoEnum.menu.getOperacao();

		} else {

			mensagem = "Valor em conta inferior, se deseja voltar para o menu digite /menu";

		}

		return mensagem;

	}

	/**
	 * Depositar
	 * 
	 * @param valor
	 * @param mensagem
	 * @return
	 */
	private String depositar(BigDecimal valor, String mensagem) {

		saldo = saldo.add(valor);

		mensagem = mensagem.concat(" depositado, retornando ao menu... \n")
				.concat(OperacaoEnum.menu.getOperacaoMensagem());

		operacaoId = OperacaoEnum.menu.getOperacao();

		return mensagem;

	}

	/**
	 * Verifica se valor eh maior que o em conta
	 * 
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
	public boolean ehMenu(String operacao) {
		return OperacaoEnum.menu.name().equalsIgnoreCase(operacao);
	}

}
