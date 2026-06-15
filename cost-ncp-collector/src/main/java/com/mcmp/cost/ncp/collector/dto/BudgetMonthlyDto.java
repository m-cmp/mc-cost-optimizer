package com.mcmp.cost.ncp.collector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetMonthlyDto {
    private Long id;
    private String csp;
    private Integer year;
    private Integer month;
    private BigDecimal budget;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public BudgetMonthlyDto(Long id, String csp, Integer year, Integer month, BigDecimal budget, String currency, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.csp = csp;
        this.year = year;
        this.month = month;
        this.budget = budget;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
