package com.github.kattlo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClientConfig;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Option;

@Slf4j
public class Shared {

    private static Properties kafkaConfigurationValues;
    private static File kafkaConfiguration;
    private static String bootstrapServers;

    @Option(
        names = {
            "--bootstrap-servers"
        },
        description = "host/port pairs to connect the Apache Kafka®",
        required = false
    )
    public static void setBootstrapServers(String bootstrapServers) {
        Shared.bootstrapServers = bootstrapServers;
    }
    public static String getBootstrapServers() {
        return bootstrapServers;
    }

    @Option(
        names = {
            "--kafka-config-file"
        },
        description = "Properties file for Apache Kafka® clients",
        required = true,
        defaultValue = "kafka.properties"
    )
    public static void setKafkaConfiguration(File kafkaConfiguration) {
        Shared.kafkaConfiguration = Objects.requireNonNull(kafkaConfiguration);
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
               // throw new CommandLine
               //     .ParameterException(spec.commandLine(),
               //         kafkaConfiguration.getAbsolutePath() + " can't be read");
            }
        }
        return kafkaConfigurationValues;
    }

    public static void validateOptions() {
        // .kattlo.yaml now is optional

        if(!kafkaConfiguration.exists()){
            throw new IllegalStateException(
                kafkaConfiguration.getAbsolutePath() + " not found");
            //throw new CommandLine.
            //    ParameterException(spec.commandLine(),
            //        kafkaConfiguration.getAbsolutePath() + " not found");
        }
    }
}
