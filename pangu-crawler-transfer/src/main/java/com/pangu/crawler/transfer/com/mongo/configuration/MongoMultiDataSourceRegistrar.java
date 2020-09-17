package com.pangu.crawler.transfer.com.mongo.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.HashMap;
import java.util.Map;
/**
 * Mongo多数据源注册器
 * @Author sheng.ding
 * @Date 2020/9/5 13:36
 * @Version 1.0
 **/

@Slf4j
public class MongoMultiDataSourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {


    public static Map<String, SimpleMongoDbFactory> mongoDbFactoryMap = new HashMap<>();

    /**
     * 注册MongoTemplate
     *
     * 目前实现注册出现问题，先使用静态map实现，后续试着改为注册Spring容器
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        int count = 0;
        for (Map.Entry<String, SimpleMongoDbFactory> entry : mongoDbFactoryMap.entrySet()) {
            SimpleMongoDbFactory value = entry.getValue();

            GenericBeanDefinition definition = new GenericBeanDefinition();
            // 设置bean类型
            definition.setBeanClass(MongoTemplate.class);
            // 设置为自定义bean
            definition.setSynthetic(true);
            // 设置不初始化
            definition.setEnforceInitMethod(false);
            if (count == 0) {
                // 第一个设置为primary
                definition.setPrimary(true);
            }
            // 构造函数参数
            ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
            argumentValues.addIndexedArgumentValue(0, value);
            // 设置构造函数参数
            definition.setConstructorArgumentValues(argumentValues);
            // 注册
            registry.registerBeanDefinition(entry.getKey(),definition);
            count++;
        }
        log.info("MongoDB多数据源装载完毕，共:{} 个", count);
    }

    /**
     * 根据配置属性获取mongoTemplate
     *
     * 根据配置文件获取属性还需完善
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {

        PropertySource<?> propertySource = ((StandardServletEnvironment) environment)
                .getPropertySources().get("applicationConfig: [classpath:/application.yml]");
        Map<String, String> appMap = (Map<String, String>) propertySource.getSource();

        /**
         * 不能用sit作为后缀，不能修改配置文件名称，只能使用bootstrap开头
         */
        if (environment.getActiveProfiles().length > 0) {
            PropertySource activeSource = ((StandardServletEnvironment) environment).getPropertySources()
                    .get("applicationConfig: [classpath:/application-" + environment.getActiveProfiles()[0] +
                            ".yml]");
            Map<String, String> activeAppMap = (Map<String, String>) activeSource.getSource();
            appMap.putAll(activeAppMap);
        }

        // key:数据源属性 vaue：对象
        Map<String, MongoConfig> configMap = new HashMap<>();
        for (Map.Entry<String, String> entry : appMap.entrySet()) {
            if (entry.getKey().contains("spring.data.mongodb")) {
                // 分离每一个属性
                String beanName = getValue(entry.getKey(),3);
                String property = getValue(entry.getKey(), 4);
                if (!configMap.containsKey(beanName)) {
                    configMap.put(beanName, new MongoConfig());
                }
                setValue(configMap.get(beanName),property,entry.getValue());
            }
        }

        log.info("开始装载MongoDB多数据源");
        for (Map.Entry<String, MongoConfig> entry : configMap.entrySet()) {
            MongoConfig value = entry.getValue();
            SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(new MongoClientURI(value.getUri()));
//            ServerAddress host = new ServerAddress(value.getHost(), value.getPort());
//            MongoClient mongoClient = new MongoClient(host);
//            SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient,value.getDatabase());
            mongoDbFactoryMap.put(entry.getKey(), simpleMongoDbFactory);
            log.info("数据源名称:{}", entry.getKey());
        }

    }


    /**
     * 获取key值
     * index：3---》bean名称
     * index：4---》属性
     *
     * @param value
     * @param index
     * @return
     */
    public String getValue(String value,int index) {
        return value.split("\\.")[index];
    }

    private void setValue(MongoConfig config,String key,Object value) {
        if ("host".equalsIgnoreCase(key)) {
            config.setHost((String) value);
        } else if ("port".equalsIgnoreCase(key)) {
            config.setPort((Integer) value);
        } else if ("database".equalsIgnoreCase(key)) {
            config.setDatabase((String) value);
        } else if ("uri".equalsIgnoreCase(key)) {
            config.setUri(String.valueOf(value) );
        }else if ("enabled".equalsIgnoreCase(key)) {
            config.setEnabled(Boolean.valueOf(String.valueOf(value)));
        }else {
            throw new IllegalArgumentException("配置信息错误");
        }
    }
}


