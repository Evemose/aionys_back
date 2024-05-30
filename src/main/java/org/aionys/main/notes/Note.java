package org.aionys.main.notes;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.aionys.main.security.users.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NonNull
    private String title;

    @NotBlank
    @NonNull
    private String content;

    @CreatedDate
    @Setter(AccessLevel.NONE)
    @Column(updatable = false) // even though setter is not generated, we need to prevent updates via reflection
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @CreatedBy
    @Setter(AccessLevel.NONE)
    private User owner;

    @LastModifiedDate
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastModifiedAt;
}
