package com.github.kattlo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClientConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
public class SharedOptionValues {

    private static Properties kafkaConfigurationValues;
    private static File kafkaConfiguration;
    private static String bootstrapServers;

    static void setBootstrapServers(String bootstrapServers) {
        SharedOptionValues.bootstrapServers = Objects.requireNonNull(bootstrapServers);
    }
    public static String getBootstrapServers() {
        return bootstrapServers;
    }

    static void setKafkaConfiguration(File kafkaConfiguration) {
        SharedOptionValues.kafkaConfiguration = Objects.requireNonNull(kafkaConfiguration);
    }
    public static Properties getKafkaConfiguration() {
        if(null== kafkaConfigurationValues){
            kafkaConfigurationValues = new Properties();

            try{
                kafkaConfigurationValues
                    .load(new FileReader(kafkaConfiguration));

                if(Objects.nonNull(getBootstrapServers())){
                    var oldBootstrapServers =
                      kafkaConfigurationValues
                        .put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                            getBootstrapServers());

                    log.debug("bootstrap.servers overwritten by {}", getBootstrapServers());
                    log.debug("Old bootstrap.servers {}", oldBootstrapServers);
                }
            }catch(IOException e){
                throw new IllegalStateException(
                    kafkaConfiguration.getAbsolutePath() + " can't be read");
            }
        }
        return kafkaConfigurationValues;
    }

    public static void validateOptions() {
        // .kattlo.yaml now is optional

        if(!kafkaConfiguration.exists()){
            throw new IllegalStateException(
                kafkaConfiguration.getAbsolutePath() + " not found");
        }
    }
}
