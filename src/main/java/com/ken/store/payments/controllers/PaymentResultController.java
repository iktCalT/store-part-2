package com.ken.store.payments.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/payment-result")
public class PaymentResultController {

    @GetMapping("/checkout-success")
    public String paymentSucceeded(@RequestParam("orderId") String orderId, Model model) {
        return "succeeded";
    }

    @GetMapping("/cancel")
    public String paymentCancelled(Model model) {
        return "cancelled";
    }
}
