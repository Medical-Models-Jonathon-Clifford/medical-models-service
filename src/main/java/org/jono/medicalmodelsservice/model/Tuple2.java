package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple2<S, T> {
    private final S t1;
    private final T t2;
}
