package com.example.pagination.pagination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Developed by Magora Team (magora-systems.com). 2017.
 *
 * @author mihaylov
 */
@Data
@Builder
@AllArgsConstructor
public class ImageItemDto {
    String name;
    String url;
}
