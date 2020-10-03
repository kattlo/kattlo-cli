package com.github.kattlo;

import picocli.CommandLine.IExitCodeExceptionMapper;

/**
 * @author fabiojose
 */
public class ExceptionToExitCode
    implements IExitCodeExceptionMapper {

    @Override
    public int getExitCode(Throwable arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

}
