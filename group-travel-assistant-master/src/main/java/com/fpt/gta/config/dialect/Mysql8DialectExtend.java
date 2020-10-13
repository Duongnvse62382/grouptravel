package com.fpt.gta.config.dialect;

import org.hibernate.dialect.MySQL8Dialect;

public class Mysql8DialectExtend extends MySQL8Dialect {
    public Mysql8DialectExtend() {
        super();
        registerKeyword("group");
        registerKeyword("member");
    }
}
