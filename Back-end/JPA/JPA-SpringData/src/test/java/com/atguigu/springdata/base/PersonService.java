package com.atguigu.springdata.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonRepository mPersonRepository;

    @Transactional
    public void savePersons(List<Person> persons) {
        mPersonRepository.saveAll(persons);
    }

    @Transactional
    public void updatePersonEmail(String email, Integer id) {
        mPersonRepository.updatePersonEmail(id, email);
    }
}
