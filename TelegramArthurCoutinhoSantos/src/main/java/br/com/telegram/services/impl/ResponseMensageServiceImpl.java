package br.com.telegram.services.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.telegram.services.ResponseMensageService;

public class ResponseMensageServiceImpl implements ResponseMensageService {

	private static OperacaoServiceImpl operacaoService = new OperacaoServiceImpl();

	@Override
	public SendResponse gerarRespostaParaChat(Update up, TelegramBot bot) {

		String mensagem = operacaoService.executarOperacao(up.message().text());

		return bot.execute(new SendMessage(up.message().chat().id(), mensagem));

	}

}
