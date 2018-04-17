package com.example.pagination.pagination.model;

import com.example.pagination.pagination.PageCursorDelegate;

import java.util.List;

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

public class ImageDataDto implements PageCursorDelegate.CursorData {
    private int id;
    private List<ImageItemDto> items;
    private String prevCursor;
    private String nextCursor;

    @Override
    public String provideNextCursor() {
        return nextCursor;
    }
}
