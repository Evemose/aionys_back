package org.aionys.notes.dto.notes;

import org.aionys.notes.persistence.model.entity.Note;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {

    GetNoteDTO toDto(Note entity);

    Note toEntity(PostNoteDTO dto);

    /**
     * Maps the given note to a GetNoteDTO. Ignores null values in dto.
     *
     * @param dto  the dto
     * @param base the base note
     */
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void toEntity(PostNoteDTO dto, @MappingTarget Note base);
}
