package com.monexus.finance.user.event;

import com.monexus.finance.user.entity.User;

public record UserDeletedEvent(User user) {
}
