package org.itracker.web.ptos;

import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;

import java.util.Date;

public class UserPTO {
    private User user;
    private Date lastAccess;

    public UserPTO(User user, Date lastAccess) {
        this.user = user;
        this.lastAccess = lastAccess;
    }

    public User getUser() {
        return user;
    }

    public boolean isStatusLocked() {
        return (user.getStatus() == UserUtilities.STATUS_LOCKED);
    }

    public boolean isRegisrationTypeSelf() {
        return (user.getRegistrationType() == UserUtilities.REGISTRATION_TYPE_SELF);
    }

    public Date getLastAccess() {
        return lastAccess;
    }
}