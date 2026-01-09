package com.ken.store.payments.services;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WebhookRequest {
    private Map<String, String> header;
    private String payload;
}
