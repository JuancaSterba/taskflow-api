package com.juancasterba.taskflow_api.config;

import com.juancasterba.taskflow_api.exception.ForbiddenAccessException;
import com.juancasterba.taskflow_api.model.Project;
import com.juancasterba.taskflow_api.security.model.User;
import com.juancasterba.taskflow_api.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Utility component to handle common security-related operations.
 * This class centralizes logic for retrieving the current authenticated user
 * and performing authorization checks.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * Retrieves the currently authenticated user from the Spring Security context.
     *
     * @return The authenticated {@link User} entity.
     * @throws UsernameNotFoundException if no user is found for the authentication context.
     */
    public User getCurrentAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Verifies if the current user is the owner of the project or has an ADMIN role.
     * Throws a {@link ForbiddenAccessException} if the access check fails.
     *
     * @param project The project to check ownership against.
     * @param currentUser The currently authenticated user.
     */
    public void checkOwnershipOrAdmin(Project project, User currentUser) {
        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenAccessException("You do not have permission to access this resource.");
        }
    }
}