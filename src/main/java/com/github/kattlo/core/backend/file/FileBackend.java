package com.github.kattlo.core.backend.file;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import com.github.kattlo.core.backend.Backend;
import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.MigrationToApply;
import com.github.kattlo.core.backend.ResourceType;

/**
 * Manages the state using a file within current directory.
 *
 * @author fabiojose
 */
public class FileBackend implements Backend {

    private final Path workdir;

    public FileBackend(Path workdir){
        this.workdir = Objects.requireNonNull(workdir);
    }

    @Override
    public Optional<Migration> latest(ResourceType type, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Migration commit(MigrationToApply migration) {
        // TODO Auto-generated method stub
        return null;
    }

}
