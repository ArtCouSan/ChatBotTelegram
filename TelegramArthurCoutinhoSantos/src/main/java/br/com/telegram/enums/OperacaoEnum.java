package br.com.telegram.enums;

public enum OperacaoEnum {

	naoEntendi("Não entendi essa operacao", 0), 
	login("Acessar a conta \nPor favor, digite seu CPF: ", 1),
	menu("Como posso te ajudar? ", 2);

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

}
