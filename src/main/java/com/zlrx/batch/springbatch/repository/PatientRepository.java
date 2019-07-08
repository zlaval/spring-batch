package com.zlrx.batch.springbatch.repository;

import com.zlrx.batch.springbatch.domain.Patient;
import org.springframework.data.repository.CrudRepository;

public interface PatientRepository extends CrudRepository<Patient, Long> {
}
