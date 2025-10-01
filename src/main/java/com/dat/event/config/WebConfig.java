/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.config;
import com.dat.event.common.constant.WebUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.processing.Generated;
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

    @Generated("application")
    @Value("${filepath.base-route}")
    private String BASE;

    @Generated("application")
    @Value("${filepath.event}")
    private String EVENT;

    @Generated("application")
    @Value("${filepath.signature}")
    private String SIGNATURE;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/".concat(BASE).concat("/").concat("birthdayPhoto").concat("/**"))
                .addResourceLocations("file:" + Paths.get("photo/birthdayPhoto").toAbsolutePath()+"/");

        registry.addResourceHandler("/".concat(BASE).concat("/").concat(EVENT).concat("/**"))
                .addResourceLocations("file:" + Paths.get(  BASE.concat("/").concat(EVENT)).toAbsolutePath()+"/");

        registry.addResourceHandler( "/".concat(BASE).concat("/").concat(SIGNATURE).concat("/**"))
                .addResourceLocations("file:" + Paths.get(BASE.concat("/").concat(SIGNATURE)).toAbsolutePath()+"/");
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", WebUrl.LOGIN_URL);
    }
}
