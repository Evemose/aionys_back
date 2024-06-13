package org.aionys.main.images;

import java.util.Optional;

public interface ImageService {
    Optional<Image> findById(Long id);
}
