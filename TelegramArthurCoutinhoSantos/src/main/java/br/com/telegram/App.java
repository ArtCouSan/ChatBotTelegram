package br.com.telegram;

import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.telegram.services.ResponseMensageService;
import br.com.telegram.services.impl.ResponseMensageServiceImpl;

public class App {

	private static ResponseMensageServiceImpl responseService = new ResponseMensageServiceImpl();

	public static void main(String[] args) {

		// Criação do objeto bot com as informações de acesso
		@SuppressWarnings("deprecation")
		TelegramBot bot = TelegramBotAdapter.build("1065282499:AAHhFZgZuC3JsF9r1OIbbQZ22ip53jD49fM");

		GetUpdatesResponse updatesResponse;
		SendResponse sendResponse;
		BaseResponse baseResponse;

		int m = 0;

		while (true) {

			updatesResponse = bot.execute(new GetUpdates().limit(100).offset(m));

			// lista de mensagens
			List<Update> updates = updatesResponse.updates();

			// análise de cada ação da mensagem
			for (Update update : updates) {

				// atualização do off-set
				m = update.updateId() + 1;

				// envio de "Escrevendo" antes de enviar a resposta
				baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));

				// envio da mensagem de resposta
				sendResponse = responseService.gerarRespostaParaChat(update, bot);

			}

		}

	}

}