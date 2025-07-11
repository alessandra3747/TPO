package pjatk.tpo.tpo7_fa_s30395;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class MessageConsumer {

    public KafkaConsumer<String, String> kafkaConsumer;

    public MessageConsumer(String topic, String groupId) {
        kafkaConsumer = new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
                ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"
        ));
        kafkaConsumer.subscribe(Collections.singletonList(topic));


        kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS)).forEach(cr -> {});
    }

}