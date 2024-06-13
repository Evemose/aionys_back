package org.aionys.main.security.users;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.aionys.main.images.Image;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity(name = "users") // postgresql does not allow table name 'user'
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NonNull
    @Column(unique = true)
    private String username;

    // password is already hashed using bcrypt here, so @Password annotation does not make sense
    @NotBlank
    @NonNull
    private String password;

    @Nullable
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private Image profilePicture;

    private boolean enabled = true;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    @Enumerated(EnumType.STRING)
    @NotNull
    @NonNull
    private Role role;

    @CreatedDate
    @NotNull
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @NotNull
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastModifiedAt;

    public User(@NonNull String username, @NonNull String password) {
        this(username, password, Role.USER);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
