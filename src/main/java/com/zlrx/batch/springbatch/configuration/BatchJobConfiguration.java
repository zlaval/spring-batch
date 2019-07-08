package com.zlrx.batch.springbatch.configuration;

import com.zlrx.batch.springbatch.domain.Patient;
import com.zlrx.batch.springbatch.mapper.PatientMapper;
import com.zlrx.batch.springbatch.model.PatientModel;
import com.zlrx.batch.springbatch.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

@Slf4j
@Configuration
public class BatchJobConfiguration {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory
                .get(Const.JOB_NAME)
                .validator(validator())
                .start(step)
                .build();
    }

    @Bean
    public Step step(ItemReader<PatientModel> itemReader,
                     Function<PatientModel, Patient> processor,
                     JpaItemWriter<Patient> writer
    ) {
        return stepBuilderFactory
                .get(Const.STEP_NAME)
                .<PatientModel, Patient>chunk(100)
                .reader(itemReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public JobParametersValidator validator() {
        return parameters -> {
            String fileName = parameters.getString(Const.JOB_FILE_NAME);
            if (StringUtils.isEmpty(fileName)) {
                throw new JobParametersInvalidException("File path not present");
            } else {
                Path path = Paths.get(applicationProperties.getBatch().getInputPath() + File.separator + fileName);
                if (Files.notExists(path)) {
                    throw new JobParametersInvalidException("File does not exists");
                }
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<PatientModel> reader(@Value("#{jobParameters['" + Const.JOB_FILE_NAME + "']}") String fileName) {
        Path path = Paths.get(applicationProperties.getBatch().getInputPath() + File.separator + fileName);
        return new FlatFileItemReaderBuilder<PatientModel>()
                .name(Const.ITEM_READER_NAME)
                .resource(new FileSystemResource(path))
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .build();
    }

    @Bean
    public LineMapper<PatientModel> lineMapper() {
        DefaultLineMapper<PatientModel> mapper = new DefaultLineMapper<>();
        mapper.setFieldSetMapper(fieldSet ->
                PatientModel.builder()
                        .sourceId(fieldSet.readString(0))
                        .firstName(fieldSet.readString(1))
                        .middleInitial(fieldSet.readString(2))
                        .lastName(fieldSet.readString(3))
                        .emailAddress(fieldSet.readString(4))
                        .phoneNumber(fieldSet.readString(5))
                        .street(fieldSet.readString(6))
                        .city(fieldSet.readString(7))
                        .state(fieldSet.readString(8))
                        .zip(fieldSet.readString(9))
                        .birthDate(fieldSet.readString(10))
                        .action(fieldSet.readString(11))
                        .ssn(fieldSet.readString(12))
                        .build()
        );
        mapper.setLineTokenizer(new DelimitedLineTokenizer());
        return mapper;
    }

//    @Bean
//    @StepScope
//    public PassThroughItemProcessor<PatientModel> processor() {
//        return new PassThroughItemProcessor<>();
//    }


    @Bean
    @StepScope
    public Function<PatientModel, Patient> processor() {
        return model -> patientMapper.map(model);
    }

//    @Bean
//    @StepScope
//    public ItemWriter<Patient> writer() {
//        return items -> items.stream().forEach(item -> log.info(item.toString()));
//    }

    @Bean
    @StepScope
    public JpaItemWriter<Patient> writer() {
        JpaItemWriter<Patient> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }


}
