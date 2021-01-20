package com.github.kattlo.core.report;

import java.nio.file.Path;
import java.util.stream.Stream;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.Resource;
import com.github.kattlo.core.backend.ResourceType;

/**
 * @author fabiojose
 */
public interface Reporter {

    void report(Migration migration);
    void report(Migration migration, boolean isImport);
    void report(Throwable cause);

    void current(Resource resource, ReportFormat format);
    void history(Stream<Migration> migrations, ReportFormat format);

    void generated(Path path);
}
