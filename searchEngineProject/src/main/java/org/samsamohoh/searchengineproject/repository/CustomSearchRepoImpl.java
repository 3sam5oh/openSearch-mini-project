package org.samsamohoh.searchengineproject.repository;

import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.samsamohoh.searchengineproject.aggregate.AWSBlog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomSearchRepoImpl implements CustomSearchRepo {

    private OpenSearchRestTemplate openSearchRestTemplate;

    @Autowired
    public CustomSearchRepoImpl(OpenSearchRestTemplate openSearchRestTemplate) {
        this.openSearchRestTemplate = openSearchRestTemplate;
    }

    @Override
    public List<AWSBlog> searchAll(String words) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(words)
                        .field("title", 2.0f)
                        .field("body", 1.5f)
                        .field("category", 1.1f)
                        .field("author", 1.0f)
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
                .build();

        SearchHits<AWSBlog> searchHits = openSearchRestTemplate.search(searchQuery, AWSBlog.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}