package com.company.plsql;

import org.junit.Test;
import org.sonar.plsqlopen.checks.verifier.PlSqlCheckVerifier;

public class FileMustBeCloseTestInException {

    @Test
    public void test() {
        PlSqlCheckVerifier.verify("src/test/resources/filemustbecloseinexception.sql", new FileMustBeCloseInExceptHanding());
    }
    
}
