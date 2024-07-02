package org.samsamohoh.searchengineproject.repository;

import org.samsamohoh.searchengineproject.aggregate.AWSBlog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemiRepo extends ElasticsearchRepository<AWSBlog, String> {

    List<AWSBlog> findByTitle(String words);
}
