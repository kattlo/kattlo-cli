package com.github.kattlo.core.backend;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author fabiojose
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Migration {

    @NonNull private MigrationToApply applied;
    @NonNull private LocalDateTime timestamp;
    @NonNull private MigrationStatus status;

}
