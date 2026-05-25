package com.huseyin.personalfinanceapi.account.listener;

import com.huseyin.personalfinanceapi.account.service.AccountService;
import com.huseyin.personalfinanceapi.user.event.UserCurrencySetEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserRegisteredEvent geldiğinde varsayılan TRY cinsinden bir "Nakit Cüzdan"
 * hesabı oluşturur. {@link AccountService#createDefaultCashWallet} idempotenttır,
 * dolayısıyla çift yayınlamada zarar yoktur.
 *
 * <p>Yeni transaction (REQUIRES_NEW) açılır — eğer kullanıcı kaydı işlemi
 * herhangi bir nedenle rollback olursa cüzdan oluşturma da o roll-back'in
 * bir parçası olur (varsayılan REQUIRED ile sarmal). REQUIRES_NEW kullanmadık
 * çünkü yeni kullanıcının cüzdanı kaydının başarısı, kullanıcının kaydının
 * başarısına bağlı olmalıdır.
 */
@Component
public class DefaultWalletInitializer {

    private final AccountService accountService;

    public DefaultWalletInitializer(AccountService accountService) {
        this.accountService = accountService;
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRED)
    public void onUserRegistered(UserCurrencySetEvent event) {
        accountService.createDefaultCashWallet(event.user());
    }
}
