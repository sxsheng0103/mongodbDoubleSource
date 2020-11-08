package utils;

import org.springframework.context.annotation.Configuration;

/**
 * @author liuhx
 * @desc TODO
 * @date 2020/02/19
 **/
@Configuration
public class CorsConfig2 {

    /*@Bean
    public FilterRegistrationBean corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addExposedHeader("Authorization");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //3.返回新的CorsFilter.
        //return new CorsFilter(configSource);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(configSource));
        bean.setOrder(0);
        return bean;
    }*/

}
