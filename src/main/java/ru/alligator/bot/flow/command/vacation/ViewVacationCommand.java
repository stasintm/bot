package ru.alligator.bot.flow.command.vacation;

import static ru.alligator.bot.flow.TgUtils.DATE_TIME_FORMATTER;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.alligator.bot.dto.VacationTo;
import ru.alligator.bot.feign.VacationFeignClient;
import ru.alligator.bot.flow.TgUtils;
import ru.alligator.bot.flow.command.Command;
import ru.alligator.bot.flow.command.CommandConstants;
import ru.alligator.bot.flow.stage.Stage;
import ru.alligator.bot.storage.ChatState;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ViewVacationCommand implements Command {

    private final VacationFeignClient vacationFeignClient;

    @Override
    public String getName() {
        return CommandConstants.VIEW_VACATION_COMMAND_NAME;
    }

    @Override
    public String getLabel() {
        return CommandConstants.VIEW_VACATION_COMMAND_TITLE;
    }

    @Override
    public List<Stage> getKnownStages() {
        return List.of(
            Stage.AUTH_MAIN_MENU
        );
    }

    @SneakyThrows
    @Override
    public void acceptMessage(String searchStr, ChatState chatState, TelegramLongPollingBot sender) {
        List<VacationTo> vacations = vacationFeignClient.findMyVacation(chatState.getEmployeeId());
        if (vacations.isEmpty()) {
            TgUtils.sendMessage(chatState.getChatId().toString(),
                "У вас пока нет запланированных утвержденных отпусков",
                sender);
        } else {
            StringBuilder msg = new StringBuilder();
            vacations.forEach(vacation ->
                msg.append(String.format("С %s по %s\n",
                    DATE_TIME_FORMATTER.format(vacation.getStartDate()),
                    DATE_TIME_FORMATTER.format(vacation.getEndDate()))));

            TgUtils.sendMessage(chatState.getChatId().toString(), msg.toString(), sender);
        }
        Command.enterStage(Stage.AUTH_MAIN_MENU, chatState, sender);
    }
}
