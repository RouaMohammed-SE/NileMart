package org.route.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 50, message = "Name must be 2 to 50 characters.")
    private String name;

    @NotBlank(message = "Slug is required.")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "Slug may contain only lower-case letters, digits and hyphens.")
    private String slug;

    @Builder.Default
    private boolean active = true;
}
