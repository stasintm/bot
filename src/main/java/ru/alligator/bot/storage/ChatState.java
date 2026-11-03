package ru.alligator.bot.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import ru.alligator.bot.flow.stage.Stage;

import java.util.UUID;


@RedisHash("chatState")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatState {
    @Id
    private Long chatId;
    private String phone;
    private Stage currentStage;

    @Indexed
    private UUID employeeId;

    public boolean isApproved() {
        return employeeId != null;
    }
}
