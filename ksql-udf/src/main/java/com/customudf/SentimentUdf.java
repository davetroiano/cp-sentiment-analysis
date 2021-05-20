package com.customudf;

import com.google.gson.Gson;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.common.Configurable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@UdfDescription(name = "sentiment",
        author = "Dave Troiano",
        version = "0.0.1",
        description = "KSQL scalar UDF for sentiment analysis")
public class SentimentUdf implements Configurable {

    private String url;

    @Override
    public void configure(final Map<String, ?> map) {
        url = (String) map.get("ksql.functions.sentiment.url");
    }

    @Udf(description = "KSQL scalar UDF for sentiment analysis")
    public String sentiment(@UdfParameter String text) {
        String result = "";
        HttpPost post = new HttpPost(url + "/sentiment/");

        Map<String, String> requestMap = new HashMap();
        requestMap.put("text", text);
        try {
            post.setEntity(new StringEntity(new Gson().toJson(requestMap)));
        } catch (UnsupportedEncodingException e) {
            return "parse_error";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return "request_error";
        }
        Map<String, String> resultMap = new Gson().fromJson(result, Map.class);
        return resultMap.get("sentiment");
    }
}
