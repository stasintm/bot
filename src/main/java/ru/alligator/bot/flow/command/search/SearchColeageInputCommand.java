package ru.alligator.bot.flow.command.search;

import static ru.alligator.bot.flow.command.CommandConstants.USER_INPUT_COMMAND_NAME;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.alligator.bot.dto.EmployeeTo;
import ru.alligator.bot.exception.TgbotAccessDeniedException;
import ru.alligator.bot.exception.TgbotBadRequestException;
import ru.alligator.bot.feign.EmployeeFeignClient;
import ru.alligator.bot.flow.TgUtils;
import ru.alligator.bot.flow.command.Command;
import ru.alligator.bot.flow.stage.Stage;
import ru.alligator.bot.storage.ChatState;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SearchColeageInputCommand implements Command {

    private final EmployeeFeignClient employeeFeignClient;

    @Override
    public String getName() {
        return USER_INPUT_COMMAND_NAME;
    }

    @Override
    public String getLabel() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isApplicable(String commandName, ChatState chatState) {
        return getName().equals(commandName) && Stage.ENTER_SEARCH_STRING.equals(chatState.getCurrentStage());
    }

    private List<EmployeeTo> getEmployees(String searchStr) {
        return employeeFeignClient.findEmployees(searchStr);
    }

    @Override
    public void acceptMessage(String searchStr, ChatState chatState, TelegramLongPollingBot sender) {
        if (searchStr != null) {
            try {
                if (!chatState.isApproved()) {
                    throw new TgbotAccessDeniedException();
                }
                var employees = getEmployees(searchStr);
                employees.forEach(employee ->
                    TgUtils.sendMessage(chatState.getChatId().toString(),
                        String.format("%s %s %s\n%s", employee.getLastName(), employee.getFirstName(),
                            Optional.ofNullable(employee.getPatronymic()).orElse(""), employee.getPhone()),
                        sender));
                if (employees.isEmpty()) {
                    TgUtils.sendMessage(chatState.getChatId().toString(),
                        "По вашему запросу не найден ни один сотрудник", sender);
                }
            } catch (TgbotBadRequestException ex) {
                TgUtils.sendMessage(chatState.getChatId().toString(),
                    "Введен некорректный запрос!", sender);
                Command.enterStage(Stage.ENTER_SEARCH_STRING, chatState, sender);
                return;
            } catch (TgbotAccessDeniedException ex) {
                TgUtils.sendMessage(chatState.getChatId().toString(),
                        "Неавторизованный пользователь!", sender);
                Command.enterStage(Stage.NOT_AUTHORIZED, chatState, sender);
                return;
            }
        }
        Command.enterStage(Stage.AUTH_MAIN_MENU, chatState, sender);
    }
}
