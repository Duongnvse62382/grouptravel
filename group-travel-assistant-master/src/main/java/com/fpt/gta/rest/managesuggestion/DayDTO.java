package com.fpt.gta.rest.managesuggestion;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DayDTO {
    private int quantity;
    private boolean startStatus;
    private boolean endStatus;
    private boolean sameDay;
    private LocalDateTime startDate;

}
