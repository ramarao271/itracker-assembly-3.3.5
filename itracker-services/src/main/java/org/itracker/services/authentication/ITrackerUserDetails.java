package org.itracker.services.authentication;

import org.apache.commons.lang.StringUtils;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


final public class ITrackerUserDetails implements UserDetails {
    private final Set<GrantedAuthority> authorities = new HashSet<>();
    private final boolean enabled;
    private final String password;
    private final String username;
    private final boolean credentialsNonExpired;
    private final boolean accountNonLocked;
    private final boolean accountNonExpired;
   private final String displayName;

   public ITrackerUserDetails(User model, Collection<Permission> permissions) {
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        for (Permission p : permissions) {
            if (null == p.getProject()) {
                authorities.add(new SimpleGrantedAuthority(p.getPermissionType().name()));
            } else {
                if (p.getPermissionType() == PermissionType.PRODUCT_ADMIN) {
                    authorities.add(new SimpleGrantedAuthority(p.getPermissionType().name()));
                }
                else if (p.getPermissionType() == PermissionType.ISSUE_VIEW_ALL) {
                    authorities.add(new SimpleGrantedAuthority(p.getPermissionType().name()));
                }
                authorities.add(new SimpleGrantedAuthority(p.getPermissionType().name(p.getProject())));
            }
        }

        username = model.getLogin();
        password = model.getPassword();
        displayName = model.getFullName();
        credentialsNonExpired = StringUtils.isNotEmpty(model.getPassword());
        accountNonLocked = model.getStatus() != UserUtilities.STATUS_LOCKED;
        accountNonExpired = model.getStatus() == UserUtilities.STATUS_ACTIVE;
        enabled = isAccountNonExpired() && !model.isNew();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
      return displayName;
    }

   @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
