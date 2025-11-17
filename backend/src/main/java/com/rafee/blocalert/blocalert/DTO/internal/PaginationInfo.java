package com.rafee.blocalert.blocalert.DTO.internal;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PaginationInfo {

    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;

    public PaginationInfo(Page<?> page){
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
    }

}
