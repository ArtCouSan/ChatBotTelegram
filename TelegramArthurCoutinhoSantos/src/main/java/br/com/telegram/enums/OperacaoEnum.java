package br.com.telegram.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperacaoEnum {

	operacaoEmAberto("Termine a operação anterior ou encerre com o comando: /menu\n", -1),
	naoEntendi("Não entendi, se deseja voltar para o menu digite: /menu", 0),
	errologin("Não entendi\nPor favor, digite seu CPF corretamente, com pontos ou sem: ", 1),
	bemVindo("Bem vindo\nComo posso te ajudar? \nSe deseja ver as opções digite: /menu ", 2),
	menu("Digite uma opção:\n /depositar\n /sacar \n /saldo \n /transferirPara", 3),
	depositar("Digite o valor que deseja depositar: ", 4),
	sacar("Digite o valor que deseja sacar: ", 5),
	saldo("Saldo em conta: ", 6),
	transferir("Digite o valor que deseja transferir: ", 7),
	transferirPara("Digite o cpf para que deseja transferir: ", 8);
	
	private static Map map = new HashMap<>();
	private String operacaoMensagem;
	private Integer operacao;

	OperacaoEnum(String operacaoMensagem, Integer operacao) {
		this.operacaoMensagem = operacaoMensagem;
		this.operacao = operacao;
	}

	public String getOperacaoMensagem() {
		return operacaoMensagem;
	}

	public Integer getOperacao() {
		return operacao;
	}

	static {
		for (OperacaoEnum o : OperacaoEnum.values()) {
			map.put(o.operacao, o);
		}
	}

	public static OperacaoEnum valueOf(int o) {
		return (OperacaoEnum) map.get(o);
	}

}
