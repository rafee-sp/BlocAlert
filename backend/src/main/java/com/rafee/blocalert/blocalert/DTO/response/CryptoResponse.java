package com.rafee.blocalert.blocalert.DTO.response;

import com.rafee.blocalert.blocalert.DTO.Pagination;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;

import java.util.List;

public record CryptoResponse(
        List<CryptoMarketLite> cryptoList,
        Pagination pagination
        ) {

}
