package com.ken.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

// We add fields we want to expose to the outside
// in DTOs
@Getter
@AllArgsConstructor
public class UserDto {
    // @JsonIgnore // Don't convert it into JSON
    private Long id;

    // @JsonProperty("user_name") // Rename this field in JSON
    private String name;
    private String email;

    /*
     * @JsonInclude(JsonInclude.Include.NON_NULL) // exclude null values private String phoneNumber;
     * // It will return null by default, // because phoneNumber is not included in Entity or
     * database
     * 
     * @JsonFormat(pattern = "YYYY-MM-DD HH:mm:ss") private LocalDateTime createdAt; // Although
     * createdAt is also not included in Entity or database // we assigned current time to it in
     * UserMapper: // @Mapping(target = "createdAt", expression = //
     * "java(java.time.LocalDateTime.now())")
     */
}
