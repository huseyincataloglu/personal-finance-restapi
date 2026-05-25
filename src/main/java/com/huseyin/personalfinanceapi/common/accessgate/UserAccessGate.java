package com.huseyin.personalfinanceapi.common.accessgate;

import com.huseyin.personalfinanceapi.auth.exception.UserAccountDisabledException;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.exception.MissingBaseCurrencyException;
import com.huseyin.personalfinanceapi.user.entity.User;
import com.huseyin.personalfinanceapi.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserAccessGate {
    private final UserRepository userRepository;
    public UserAccessGate(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Query to database, finds user
     * @param userId
     * @return
     */
    public User requireExistence(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
    }

    /**
     * Query to database, finds user and checks if the user is still active
     * @param userId
     * @return User
     */
    public User requireActive(Long userId){

        User user = requireExistence(userId);

        if(!user.isEnabled()){
            throw new UserAccountDisabledException("User is disabled");
        }
        return user;

    }

    /**
     * Query to database, finds user and checks if the user is still active and base currency is configured
     * @param userId
     * @return User
     */
    public User requireFinancialAccess(Long userId){
        User user = requireActive(userId);

        if(!user.isOnBoardingCompleted()){
            throw new BusinessRuleViolationException("User must complete the onboarding in order to operate some in some part of the system.");
        }
        return user;
    }
}
