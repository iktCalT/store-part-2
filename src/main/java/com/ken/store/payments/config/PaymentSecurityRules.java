package com.ken.store.payments.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;
import com.ken.store.common.security.SecurityRules;

@Component
public class PaymentSecurityRules implements SecurityRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        // stripe|paypal will not login as a user, we have to make this endpoint public 
        registry.requestMatchers(HttpMethod.POST, "/checkout/webhook").permitAll()
                .requestMatchers(HttpMethod.GET, "/payment-result/**").permitAll(); 
    }

}
