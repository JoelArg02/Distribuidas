package com.allpasoft.sync.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientTimeDto {
    private String nodeName;
    private Long serverTime;

}
