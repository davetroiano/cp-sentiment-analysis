# Intro

This is a standalone Confluent Platform on Docker demo that ingests Tweets for sentiment analysis via ksqlDB UDF.

# Prerequisites

- [Create](https://developer.twitter.com/en) a Twitter developer account
- Sign in and create a Standalone app
- You'll need a few keys: the API key and Secret under "Consumer Keys", and the Access Token and Secret under "Authentication Tokens".
- Update filter.keywords in connector to customize

# Running demo
## Start services
Start CP:
```
docker-compose up -d
```

Fill in your Twitter consumer key / secret and access token & secret in the [connector config](connector/twitter-source.json).

## Ingest Tweets
Add Twitter connector:

```
curl -X POST -H "Content-Type: application/json" -d @connector/twitter-source.json http://localhost:8083/connectors
```

Validate that the connector is running, and that you're getting messages on the `tweets` topic.

## ksqlDB

```
CREATE STREAM tweets (payload STRUCT<Text VARCHAR, User STRUCT<Location VARCHAR>>)
WITH (KAFKA_TOPIC='tweets', VALUE_FORMAT='JSON');

SELECT payload->Text, payload->User->Location
FROM tweets
WHERE payload->User->Location IS NOT NULL
EMIT CHANGES;

SELECT payload->Text, sentiment(payload->Text) AS sentiment
FROM tweets
EMIT CHANGES;
```


# Development

## Images
If you need to build / push a new image (to upgrade, add a connector, modify the inference service):

### Connect image
```
cd images/kafka-connect-twitter
docker build -t <your org>/kafka-connect-twitter:<corresponding CP version>
docker push <your org>/kafka-connect-twitter:<corresponding CP version>
```

### ksqlDB server image
```
cd ksql-udf
gradle clean shadowJar
cd ../images/cp-ksqldb-server-sentiment-udf/
cp ../../ksql-udf/extensions/nlp-udfs-*.jar .
docker build -t <your org>/cp-ksqldb-server-sentiment-udf:<corresponding CP version>
docker push <your org>/cp-ksqldb-server-sentiment-udf:<corresponding CP version>```
rm nlp-udfs-*.jar
```

### Model serving image
```
cd images/nlp-model-serving
docker build -t <your org>/nlp-model-serving:<version>
docker push <your org>/nlp-model-serving:<version>
```
