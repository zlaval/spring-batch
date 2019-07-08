package com.zlrx.batch.springbatch.mapper;

import com.zlrx.batch.springbatch.domain.Patient;
import com.zlrx.batch.springbatch.model.PatientModel;


//@Mapper(componentModel = "spring")
public interface PatientMapper {

    // @Mapping(target = "id", ignore = true)
    Patient map(PatientModel model);

}
