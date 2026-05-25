package com.huseyin.personalfinanceapi.common;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Spring Data {@link Page}'i HTTP yanıt için sade bir DTO'ya çevirir.
 * Page'i doğrudan döndürmek Jackson sürüm farklılıklarına ve fazla alanlara
 * yol açabilir; bu sarmalayıcı stabil bir contract sağlar.
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <S, T> PagedResponse<T> of(Page<S> page, Function<S, T> mapper) {
        return new PagedResponse<>(
                page.map(mapper).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
