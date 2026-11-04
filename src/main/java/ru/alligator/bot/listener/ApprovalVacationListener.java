package ru.alligator.bot.listener;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.alligator.bot.dto.VacationTo;
import ru.alligator.bot.flow.EntryBot;
import ru.alligator.bot.flow.TgUtils;
import ru.alligator.bot.storage.ChatStateRepository;

@RequiredArgsConstructor
@Component
public class ApprovalVacationListener {
    private final ChatStateRepository repository;
    private final EntryBot bot;

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(VacationTo to) {
        repository.findByEmployeeId(to.getEmployeeId()).forEach(state -> {
            TgUtils.sendMessage(state.getChatId().toString(),
                    String.format("Ваш отпуск с %s по %s согласован",
                            TgUtils.DATE_TIME_FORMATTER.format(to.getStartDate()),
                            TgUtils.DATE_TIME_FORMATTER.format(to.getEndDate())), bot);
        });
    }
}
