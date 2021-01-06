package com.github.kattlo.core.report;

import com.github.kattlo.core.backend.Migration;

/**
 * @author fabiojose
 */
public interface Reporter {

    void report(Migration migration);
    void report(Migration migration, boolean isImport);
    void report(Throwable cause);
}
