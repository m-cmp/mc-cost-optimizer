package com.mcmp.collector.model.cur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurProcessModel {
    private String csp;
    private String payer_account;
    private String collect_date;
    private String certifed_fixed_yn;
    private LocalDateTime certifed_fixed_date;
    private String object_key;
}
