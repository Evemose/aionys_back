package org.aionys.main.security.users;

import org.aionys.main.images.Image;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class UserMapper {

    abstract GetUserDTO toDTO(User user);

    abstract User toEntity(PostUserDTO userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract void mapNonNullIntoEntity(PostUserDTO dto, @MappingTarget User base);

    @ObjectFactory
    protected User createEntity(PostUserDTO dto) {
        return new User(dto.username(), dto.password());
    }

    protected Image map(String value) {
        return value == null ? null : new Image(value.getBytes());
    }

    protected String map(Image value) {
        return value == null ? null : new String(value.getImage());
    }
}
