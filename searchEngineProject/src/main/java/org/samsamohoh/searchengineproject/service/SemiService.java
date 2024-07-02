package org.samsamohoh.searchengineproject.service;

import org.samsamohoh.searchengineproject.aggregate.AWSBlog;

import java.util.List;

public interface SemiService {
    List<AWSBlog> findTest(String words);
}
