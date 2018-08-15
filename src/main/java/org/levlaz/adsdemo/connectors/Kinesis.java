package org.levlaz.adsdemo.connectors;

import java.nio.ByteBuffer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.launchdarkly.eventsource.MessageEvent;

import org.levlaz.adsdemo.SimpleEventHandler;
import org.levlaz.adsdemo.utils.KinesisConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kinesis extends SimpleEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(Kinesis.class);
    private AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();
    private AmazonKinesis kinesisClient = null;
    private String region = null;
    private String streamName = null; 
    private BasicAWSCredentials awsCreds = null;
    private int counter = 0;

    public Kinesis(String region, String streamName, BasicAWSCredentials awsCreds) {
        this.streamName = streamName;
        this.region = region;
        this.awsCreds = awsCreds;

        clientBuilder.setClientConfiguration(KinesisConfigurationUtils.getClientConfig());
        clientBuilder.setCredentials(new AWSStaticCredentialsProvider(this.awsCreds));
        clientBuilder.setRegion(this.region);
        kinesisClient = clientBuilder.build();
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
        byte[] bytes = messageEvent.getData().getBytes("utf-8");

        if (bytes == null) {
            logger.warn("Could not get JSON bytes from event.");
            return;
        }

        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);
        putRecord.setPartitionKey("ld-ads-demo-partition");
        putRecord.setData(ByteBuffer.wrap(bytes));

        try {
            kinesisClient.putRecord(putRecord);
            counter += 1;
            logger.info("Sent " + counter + " record(s) to Kinesis");
        } catch (AmazonClientException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}