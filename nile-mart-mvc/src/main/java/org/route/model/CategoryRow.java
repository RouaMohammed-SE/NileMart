package org.route.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CategoryRow {
    String name;
    String slug;
    String parentName;
    int displayOrder;
    boolean active;
}
