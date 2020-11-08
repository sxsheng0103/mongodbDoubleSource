package utils;

import org.springframework.context.annotation.Configuration;

/**
 * @author liuhx
 * @desc TODO
 * @date 2020/02/19
 **/
@Configuration
public class CorsConfig1 {

   /* @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addExposedHeader("Authorization");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }*/

}
