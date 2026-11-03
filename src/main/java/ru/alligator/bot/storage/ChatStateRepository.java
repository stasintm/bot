package ru.alligator.bot.storage;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ChatStateRepository extends CrudRepository<ChatState, Long> {

    public List<ChatState> findByEmployeeId(UUID employeeId);
}
