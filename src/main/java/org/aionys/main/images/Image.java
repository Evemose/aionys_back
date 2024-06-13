package org.aionys.main.images;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    @NotEmpty
    private byte @NonNull [] image;

    public Image(@NotEmpty byte @NonNull [] image) {
        this.image = image;
    }
}
