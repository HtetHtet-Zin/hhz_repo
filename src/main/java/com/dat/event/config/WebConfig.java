/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.config;
import org.springframework.context.annotation.Configuration;
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
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /// Map /captures/** to the actual file system directory
        registry.addResourceHandler("/staffPhoto/**")
                .addResourceLocations("file:" + Paths.get("staffPhoto").toAbsolutePath() + "/");
    }
}
