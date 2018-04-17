package com.example.pagination.pagination.datasource;

import com.example.pagination.pagination.model.ImageDataDto;

import io.reactivex.Flowable;

/**
 * Developed by Magora Team (magora-systems.com). 2017.
 *
 * @author mihaylov
 */
public interface ImagesDataSource {
    Flowable<ImageDataDto> getImagesData(String nextCursor,int pageSize);
}
