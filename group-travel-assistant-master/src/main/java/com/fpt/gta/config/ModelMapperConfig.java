package com.fpt.gta.config;

import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSourceNamingConvention(NamingConventions.JAVABEANS_MUTATOR)
                .setSkipNullEnabled(true)
                .setPropertyCondition(new Condition<Object, Object>() {
                    public boolean applies(MappingContext<Object, Object> context) {
                        if (context.getSource() instanceof PersistentCollection) {
                            return ((PersistentCollection) context.getSource()).wasInitialized();
                        } else {
                            return true;
                        }
                    }
                });
        return modelMapper;
    }


}
