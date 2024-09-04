package com.mcmp.costbe.common.service;

import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

@Service
public class ExceptionService {

    public boolean isTableNotFound(BadSqlGrammarException ex){
        String msg = ex.getCause().getMessage();

        return msg != null && msg.contains("Table") && (msg.contains("doesn't exist") || msg.contains("not found") ||
                msg.contains("does not exist") || msg.contains("ORA-00942"));
    }
}
