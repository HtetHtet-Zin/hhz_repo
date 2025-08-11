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
        registry.addResourceHandler("/photo/birthdayphoto/**")
                .addResourceLocations("file:" + Paths.get("photo/birthdayphoto").toAbsolutePath()+"/");

        registry.addResourceHandler("/photo/eventphoto/**")
                .addResourceLocations("file:" + Paths.get("photo/eventphoto").toAbsolutePath()+"/");
    }
}
