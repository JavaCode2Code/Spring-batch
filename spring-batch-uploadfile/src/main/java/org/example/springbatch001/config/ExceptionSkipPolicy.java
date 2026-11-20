package org.example.springbatch001.config;

import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class ExceptionSkipPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(@NonNull Throwable throwable, long skipCount) throws SkipLimitExceededException {
        return throwable instanceof NumberFormatException;
    }
}
