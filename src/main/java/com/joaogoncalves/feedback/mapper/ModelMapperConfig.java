package com.joaogoncalves.feedback.mapper;

import com.joaogoncalves.feedback.entity.Feedback;
import com.joaogoncalves.feedback.model.FeedbackRead;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Feedback, FeedbackRead>() {
            @Override
            protected void configure() {
                map(source.getUser().getUsername(), destination.getUser());
            }
        });
        Condition<Object, Object> skipNullAndEmpty = new Condition<Object, Object>() {
            @Override
            public boolean applies(MappingContext<Object, Object> context) {
                return context.getSource() != null && !"".equals(context.getSource());
            }
        };
        modelMapper.getConfiguration().setPropertyCondition(skipNullAndEmpty);
        return modelMapper;
    }
}