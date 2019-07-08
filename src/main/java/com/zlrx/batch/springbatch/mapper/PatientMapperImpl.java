package com.zlrx.batch.springbatch.mapper;

import com.zlrx.batch.springbatch.domain.Patient;
import com.zlrx.batch.springbatch.model.PatientModel;
import org.springframework.stereotype.Component;

@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public Patient map(PatientModel model) {
        if (model == null) {
            return null;
        }

        Patient patient = new Patient();

        patient.setSourceId(model.getSourceId());
        patient.setFirstName(model.getFirstName());
        patient.setMiddleInitial(model.getMiddleInitial());
        patient.setLastName(model.getLastName());
        patient.setEmailAddress(model.getEmailAddress());
        patient.setPhoneNumber(model.getPhoneNumber());
        patient.setStreet(model.getStreet());
        patient.setCity(model.getCity());
        patient.setState(model.getState());
        patient.setZip(model.getZip());
        patient.setBirthDate(model.getBirthDate());
        patient.setAction(model.getAction());
        patient.setSsn(model.getSsn());

        return patient;
    }
}

