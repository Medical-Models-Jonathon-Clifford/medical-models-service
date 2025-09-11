package org.jono.medicalmodelsservice.model;

import java.util.List;

public record TotalResourceMetrics(long total, List<DailyResourceCount> dailyCounts) {}
