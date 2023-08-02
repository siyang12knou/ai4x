package com.kailoslab.ai4x.commons.data.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResultMessageDto {
    private boolean result = true;
    @Nonnull
    private String message = "데이터 처리에 성공하였습니다.";
    private Object data;

    public ResultMessageDto(Object data) {
        this.data = data;
    }

    public ResultMessageDto(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
