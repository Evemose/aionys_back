package org.aionys.main.security.users;

import org.aionys.main.notes.Note;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class UserMapper {
    abstract GetUserDTO toDTO(User user);

    abstract User toEntity(PostUserDTO userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    abstract void mapNonNullIntoEntity(PostUserDTO dto, @MappingTarget Note base);

    @ObjectFactory
    protected User createEntity(PostUserDTO dto) {
        return new User(dto.username(), dto.password());
    }
}
