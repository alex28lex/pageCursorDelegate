package com.example.pagination.pagination.datasource;

import com.example.pagination.pagination.model.ImageDataDto;
import com.example.pagination.pagination.model.ImageItemDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Developed by Magora Team (magora-systems.com). 2017.
 *
 * @author mihaylov
 */
public class ImagesRestMockDataSource implements ImagesDataSource {
    @Override
    public Flowable<ImageDataDto> getImagesData(final String nextCursor, final int pageSize) {
        final List<ImageDataDto> imageDataDtos = generateImageDataList(pageSize, 5);
        return Flowable.create(new FlowableOnSubscribe<ImageDataDto>() {
            @Override
            public void subscribe(FlowableEmitter<ImageDataDto> e) throws Exception {
                ImageDataDto imageDataDto = getPage(nextCursor, imageDataDtos);
                if (imageDataDto != null) {
                    e.onNext(imageDataDto);
                    e.onComplete();
                } else {
                    e.onError(new Throwable("Illegal cursor"));
                }
            }
        }, BackpressureStrategy.DROP).delay(2000L, TimeUnit.MILLISECONDS);
    }


    protected List<ImageDataDto> generateImageDataList(int pageSize, int pageCount) {
        List<ImageDataDto> imageDataDtos = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            List<ImageItemDto> imageItemDtos = new ArrayList<>();
            for (int j = 0; j < pageSize; j++) {
                imageItemDtos.add(new ImageItemDto("page " + i + "image " + j, "https://s3.eu-west-1.amazonaws.com/greetingcards-dev/formats/360/18c48b44-94e7-4d1d-af66-d4691fa127ee"));
            }
            imageDataDtos.add(new ImageDataDto(i, imageItemDtos, i == 0 ? null : String.valueOf(i - 1), i == pageCount - 1 ? null : String.valueOf(i + 1)));
        }
        return imageDataDtos;
    }

    protected ImageDataDto getPage(String nextCursor, List<ImageDataDto> imageDataDtos) {
        if (nextCursor == null)
            return imageDataDtos.get(0);
        for (ImageDataDto imageDataDto : imageDataDtos) {
            if (String.valueOf(imageDataDto.getId()).equals(nextCursor))
                return imageDataDto;
        }
        return null;
    }
}
