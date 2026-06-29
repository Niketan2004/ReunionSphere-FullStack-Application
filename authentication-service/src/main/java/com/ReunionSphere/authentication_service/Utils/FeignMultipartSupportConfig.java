package com.ReunionSphere.authentication_service.Utils;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@Configuration
public class FeignMultipartSupportConfig {

     @Bean
     public Encoder feignFormEncoder(ObjectProvider<FeignHttpMessageConverters> converters) {
          return new SpringFormEncoder(new SpringEncoder(converters));
     }
}
