package com.huseyin.personalfinanceapi.category.controller.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Bir kategoride yalnızca {@code name} güncellenebilir. Tip değişikliğine
 * (EXPENSE/INCOME) izin verilmez — talimattaki "atanmış kategoriler üzerinde
 * sadece isim gibi customization işlemlerine izin verilmeli, kategorinin
 * tipi değiştirilememeli" kuralına uymak için.
 */
public record UpdateCategoryRequest(
        @NotBlank String name
) {
}
