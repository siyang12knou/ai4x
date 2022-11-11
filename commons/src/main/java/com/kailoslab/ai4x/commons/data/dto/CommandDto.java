package com.kailoslab.ai4x.commons.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CommandDto {
    private String command;
    private Map<String, Object> options;
}
