package org.jono.medicalmodelsservice.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyResourceCount {
    private LocalDate date;
    private long newResources;
    private long runningTotal;
}
