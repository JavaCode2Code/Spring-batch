package org.example.springbatch001.config;

import lombok.AllArgsConstructor;
import org.example.springbatch001.entity.Customer;
import org.example.springbatch001.partitioning.ColumnRangePartitioner;
import org.example.springbatch001.repository.CustomerRepository;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerWriter customerWriter;

    @Bean
    public FlatFileItemReader<Customer> reader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("csvReader")
                .resource(new FileSystemResource("src/main/resources/customers.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .build();
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }


    @Bean
    public ColumnRangePartitioner partitioner() {
        return new ColumnRangePartitioner();
    }
    
@Bean
public PartitionHandler partitionHandler(JobRepository jobRepository) {
    TaskExecutorPartitionHandler taskExecutorPartitionHandler=new TaskExecutorPartitionHandler();
    taskExecutorPartitionHandler.setGridSize(4);
    taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
    taskExecutorPartitionHandler.setStep(slaveStep(jobRepository));
    return taskExecutorPartitionHandler;
}
    @Bean
    public Step slaveStep(JobRepository jobRepository) {
        return new StepBuilder("slaveStep", jobRepository).<Customer, Customer>chunk(250)
                .reader(reader())
                .processor(processor()).
               writer(customerWriter)
                .build();
    }

    @Bean
    public Step masterStep(JobRepository jobRepository,
                           PartitionHandler partitionHandler,
                           ColumnRangePartitioner partitioner) {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner("slaveStep", partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, Step masterStep) {
        return new JobBuilder("importCustomers", jobRepository)
                .start(masterStep)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setQueueCapacity(4);
        return taskExecutor;
    }
}
