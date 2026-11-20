package org.example.springbatch001.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/jobs")
@Tag(name = "Batch Jobs", description = "CSV customer import operations")
public class JobController {

    private final Job job;
    private final JobOperator jobOperator;

    public JobController(Job job, JobOperator jobOperator) {
        this.job = job;
        this.jobOperator = jobOperator;
    }

    @Operation(summary = "Import data from a CSV file", description = "Starts a batch job to import data from the provided CSV file.")
    @PostMapping(path = "/importData", consumes = "multipart/form-data")
    public ResponseEntity<?> startBatch(@Parameter(description = "CSV file containing customer records", required = true)
                                       @RequestPart("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("The uploaded file is empty");
        }

        Path fileToImport = null;
        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String safeFileName = originalFileName == null
                    ? "customers.csv"
                    : Paths.get(originalFileName).getFileName().toString();

            Path temporaryDirectory = Files.createDirectories(
                    Paths.get(System.getProperty("java.io.tmpdir"), "spring-batch-uploads"));
            fileToImport = Files.createTempFile(temporaryDirectory, "upload-", "-" + safeFileName);
            multipartFile.transferTo(fileToImport);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", fileToImport.toAbsolutePath().toString())
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobOperator.start(job, jobParameters);
            return ResponseEntity.ok(execution);
        } catch (IOException | JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | InvalidJobParametersException exception) {
            return ResponseEntity.internalServerError()
                    .body("Could not run the batch job: " + exception.getMessage());
        } finally {
            if (fileToImport != null) {
                try {
                    Files.deleteIfExists(fileToImport);
                } catch (IOException ignored) {
                    // The OS will clean temporary files if cleanup is unavailable.
                }
            }
        }
    }
}