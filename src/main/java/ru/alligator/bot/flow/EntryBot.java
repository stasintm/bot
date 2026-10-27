package ru.alligator.bot.flow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alligator.bot.flow.command.Command;
import ru.alligator.bot.flow.stage.Stage;
import ru.alligator.bot.storage.ChatState;
import ru.alligator.bot.storage.ChatStateService;

import java.util.List;

import static ru.alligator.bot.flow.command.CommandConstants.USER_INPUT_COMMAND_NAME;

@Slf4j
@Component
public class EntryBot extends TelegramLongPollingBot {

    private final String botName;

    private final List<Command> commands;

    private final ChatStateService chatStateService;


    public EntryBot(@Value("${bot.name}") String botName,
                    @Value("${bot.token}") String token,
                    List<Command> commands,
                    ChatStateService chatStateService) {
        super(token);
        this.botName = botName;
        this.commands = commands;
        this.chatStateService = chatStateService;
    }


    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = null;
        String msg;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().getContact() != null) {
                msg = update.getMessage().getContact().getPhoneNumber();
            } else {
                msg = update.getMessage().getText();
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            msg = update.getCallbackQuery().getData();
            TgUtils.clearButton(chatId.toString(), update.getCallbackQuery().getMessage().getMessageId(), this);
        } else  {
            throw new IllegalArgumentException("Unknown update type");
        }
        final ChatState chatState = chatStateService.getChatState(chatId);
        final String cmdName = msg.startsWith(Command.COMMAND_PREFIX) ? msg : USER_INPUT_COMMAND_NAME;
        var command = commands.stream()
            .filter(c -> c.isApplicable(cmdName, chatState))
            .findFirst();
        if (command.isPresent()) {
            command.get().acceptMessage(msg, chatState, this);
        } else {
            Command.enterStage(chatState.isApproved() ? Stage.AUTH_MAIN_MENU : Stage.NOT_AUTHORIZED, chatState, this);
        }
        chatStateService.save(chatState);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
