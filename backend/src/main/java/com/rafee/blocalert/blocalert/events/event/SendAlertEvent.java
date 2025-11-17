package com.rafee.blocalert.blocalert.events.event;

import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;

import java.util.List;

public record SendAlertEvent(List<CryptoMarketLite> alertEvent){

}
