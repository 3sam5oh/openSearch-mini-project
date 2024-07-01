package org.samsamohoh.searchengineproject.aggregate;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@Data
@Document(indexName = "aws-blog-stemming")
@Mapping(mappingPath = "mapping.json")
@Setting(settingPath = "setting.json")
public class AWSBlog {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Text)
    private String body;

    @Field(type = FieldType.Text)
    private String category;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private String date;

    @Field(type = FieldType.Keyword)
    private String url;
}
