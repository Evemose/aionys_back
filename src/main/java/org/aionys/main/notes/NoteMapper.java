package org.aionys.main.notes;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NoteMapper {

    GetNoteDTO toDto(Note entity);

    Note toEntity(PostNoteDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void mapNonNullIntoEntity(PostNoteDTO dto, @MappingTarget Note base);
}
