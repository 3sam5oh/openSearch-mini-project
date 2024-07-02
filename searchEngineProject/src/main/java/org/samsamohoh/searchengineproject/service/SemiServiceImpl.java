package org.samsamohoh.searchengineproject.service;

import org.samsamohoh.searchengineproject.aggregate.AWSBlog;
import org.samsamohoh.searchengineproject.repository.SemiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemiServiceImpl implements SemiService{

    private final SemiRepo semiRepo;

    @Autowired
    public SemiServiceImpl(SemiRepo semiRepo) {
        this.semiRepo = semiRepo;
    }

    @Override
    public List<AWSBlog> findTest(String words) {

        return semiRepo.searchAll(words);
    }
}
