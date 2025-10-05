package org.jono.medicalmodelsservice.model;

import java.time.LocalDate;

public record DailyResourceCount(LocalDate date, long newResources, long runningTotal) {}
