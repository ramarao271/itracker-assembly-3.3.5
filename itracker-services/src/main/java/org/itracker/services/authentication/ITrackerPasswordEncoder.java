package org.itracker.services.authentication;

import org.itracker.PasswordException;
import org.itracker.model.util.UserUtilities;
import org.springframework.security.crypto.password.PasswordEncoder;


public class ITrackerPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return UserUtilities.encryptPassword(String.valueOf(rawPassword));
        } catch (PasswordException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (null == rawPassword || rawPassword.length() < 1 || null == encodedPassword) {
            return false;
        }
        String pw = encode(rawPassword);

        return pw.equals(encodedPassword);
    }
}
