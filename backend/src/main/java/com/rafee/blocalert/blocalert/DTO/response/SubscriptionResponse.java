package com.rafee.blocalert.blocalert.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SubscriptionResponse {

    boolean isPaymentCheckedOut;
    boolean isSessionValid;
    boolean isSubscriptionSuccess;
    double amount;
    LocalDate subscriptionStart;
    LocalDate subscriptionEnd;
    String customerEmail;

}
