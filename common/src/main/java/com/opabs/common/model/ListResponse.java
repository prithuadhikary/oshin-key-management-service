package com.opabs.common.model;

import lombok.Data;

@Data
public class ListResponse<T> {

    private Iterable<T> content;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;

    private long totalElements;

}
