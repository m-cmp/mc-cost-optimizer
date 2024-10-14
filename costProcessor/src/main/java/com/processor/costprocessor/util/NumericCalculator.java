package com.processor.costprocessor.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
@Slf4j
public class NumericCalculator {

    public String parseExponentialFormat(double num){
        String numStr = Double.toString(num);

        if(numStr.contains("E")){
            DecimalFormat df = new DecimalFormat("0.00E0");
            String formatted = df.format(num);

            formatted = formatted.replace("E", "(10^") + ")";

            return formatted;
        } else {
            return String.format("%.2f", num);
        }

    }
}
