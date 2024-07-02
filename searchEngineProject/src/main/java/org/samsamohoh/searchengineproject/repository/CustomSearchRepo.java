package org.samsamohoh.searchengineproject.repository;

import org.samsamohoh.searchengineproject.aggregate.AWSBlog;

import java.util.List;

public interface CustomSearchRepo {
    List<AWSBlog> searchAll(String word);
}
