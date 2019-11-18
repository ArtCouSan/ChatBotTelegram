package br.com.telegram.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;

public interface ResponseMensageService {

	public SendResponse gerarRespostaParaChat(Update up, TelegramBot bot);	
}
