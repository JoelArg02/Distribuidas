package com.allpasoft.sync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ServerTimeDto {
    private long serverTime;
    private Map<String, Long> diferences;
}
