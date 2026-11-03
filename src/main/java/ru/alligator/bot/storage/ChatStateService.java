package ru.alligator.bot.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatStateService {
    private final ChatStateRepository repository;

    public ChatState getChatState(Long chatId) {
        return repository.findById(chatId).orElseGet(() -> {
            ChatState state = new ChatState();
            state.setChatId(chatId);
            return state;
        });
    }

    public void save(ChatState state) {
        repository.save(state);
    }

}
