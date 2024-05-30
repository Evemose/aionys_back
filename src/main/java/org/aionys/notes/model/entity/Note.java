package org.aionys.notes.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
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
    private LocalDateTime createdAt;

    @NonNull
    @ManyToOne
    @CreatedBy
    private User owner;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
}
