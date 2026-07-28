package com.monexus.finance.user.event;

import com.monexus.finance.user.entity.User;

public record UserRegisteredEvent(User user) {
}
