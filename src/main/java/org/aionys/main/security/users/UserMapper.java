package org.aionys.main.security.users;

import org.aionys.main.notes.Note;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {
    GetUserDTO toDTO(User user);

    User toEntity(PostUserDTO userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void mapNonNullIntoEntity(PostUserDTO dto, @MappingTarget Note base);
}
