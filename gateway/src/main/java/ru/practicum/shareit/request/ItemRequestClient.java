package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestParams;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(ItemRequestDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getOwnRequests(long userId) {
        return get("", userId);

    }

    public ResponseEntity<Object> getAllRequest(ItemRequestParams params) {
        Map<String, Object> paramsMap = Map.of(
                "from", params.getFrom(),
                "size", params.getSize()
        );
        return get("/all?from={from}&size={size}", params.getUserId(), paramsMap);
    }

    public ResponseEntity<Object> getItemRequestById(Long requestId, long userId) {
        return get("/" + requestId, userId);
    }
}