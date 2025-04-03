package com.example.doan.config;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("product-photo", registry);
        exposeDirectory("data", registry);
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
<<<<<<< HEAD
        
        registry.addResourceHandler("/product-photo/**")
                .addResourceLocations("file:" + uploadPath + "/");
        registry.addResourceHandler("/data/**")
=======
        registry.addResourceHandler("/" + dirName + "/**") 
>>>>>>> a53bc9b (file anh)
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
