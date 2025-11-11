package com.group5final.roomieradar.mappers;

import com.group5final.roomieradar.dtos.response.UserDto;
import com.group5final.roomieradar.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}

