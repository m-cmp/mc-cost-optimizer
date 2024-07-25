package com.mcmp.collector.batch;

import com.mchange.v2.lang.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Getter
public class UnusedJobParameter {

    private LocalDateTime startDt;

    private LocalDateTime endDt;

    public UnusedJobParameter(String startDt, String endDt){
        if(!startDt.isEmpty() && !endDt.isEmpty()){
            this.startDt = LocalDateTime.parse(startDt);
            this.endDt = LocalDateTime.parse(endDt);
        }
    }
}
