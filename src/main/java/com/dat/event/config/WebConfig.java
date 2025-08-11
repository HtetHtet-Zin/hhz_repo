/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * WebConfig Class.
 * <p>
 * </p>
 *
 * @author
 */


@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/photo/birthdayPhoto/**")
                .addResourceLocations("file:" + Paths.get("photo/birthdayPhoto").toAbsolutePath()+"/");

        registry.addResourceHandler("/photo/eventPhoto/**")
                .addResourceLocations("file:" + Paths.get("photo/eventPhoto").toAbsolutePath()+"/");
    }
}
