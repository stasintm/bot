package ru.alligator.bot.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.alligator.bot.exception.TgbotBadRequestException;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class TgbotErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (methodKey.contains("findEmployees")) {
            var body = getBody(response);
            if (body != null && body.contains("Internal Server Error")) {
                return new TgbotBadRequestException();
            }
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }

    @SneakyThrows
    private String getBody(Response response) {
        if (response.body() == null) {
            return null;
        }
        return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
