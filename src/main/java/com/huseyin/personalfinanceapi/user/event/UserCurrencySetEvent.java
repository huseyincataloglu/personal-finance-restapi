package com.huseyin.personalfinanceapi.user.event;

import com.huseyin.personalfinanceapi.user.entity.User;

/**
 * Kullanıcı baseCurrencysini set ettiğinde Account modülü bu
 * event'i dinleyerek varsayılan "Nakit Cüzdan" hesabını oluşturur.
 *
 * <p>Event publish gevşek bağlama (loose coupling) sağlar — AuthService'in
 * Account modülüne doğrudan bağımlılığı yoktur.
 */
public record UserCurrencySetEvent(User user) {

}
