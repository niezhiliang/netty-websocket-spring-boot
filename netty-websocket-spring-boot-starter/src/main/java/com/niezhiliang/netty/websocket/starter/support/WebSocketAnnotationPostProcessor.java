package com.niezhiliang.netty.websocket.starter.support;

import com.niezhiliang.netty.websocket.starter.WebsocketProperties;
import com.niezhiliang.netty.websocket.starter.annotations.WsServerEndpoint;
import com.niezhiliang.netty.websocket.starter.netty.NettyWebsocketServer;
import com.niezhiliang.netty.websocket.starter.netty.WebsocketActionDispatch;
import lombok.SneakyThrows;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;


/**
 * @Author : niezhiliang
 * @Date : 2023/6/23
 */
public class WebSocketAnnotationPostProcessor implements SmartInitializingSingleton {
    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private WebsocketProperties websocketProperties;

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanNamesForAnnotation = beanFactory.getBeanNamesForAnnotation(SpringBootApplication.class);
        String applicationStartBean = beanNamesForAnnotation[0];
        Object bean = beanFactory.getBean(applicationStartBean);
        String basePackage = ClassUtils.getPackageName(bean.getClass());
        scanWebsocketServiceBeans(basePackage,beanFactory);
        registerServerEndpoints();
    }

    @SneakyThrows
    private void registerServerEndpoints() {
        String[] beanNamesForAnnotation = beanFactory.getBeanNamesForAnnotation(WsServerEndpoint.class);
        WebsocketActionDispatch actionDispatch = new WebsocketActionDispatch();
        for (String beanName : beanNamesForAnnotation) {
            Class<?> beanType = beanFactory.getType(beanName);
            Class<?> targetClass = getTargetClass(beanType);

            WsServerEndpoint wsServerEndpoint = targetClass.getAnnotation(WsServerEndpoint.class);
            WebsocketServerEndpoint websocketServerEndpoint = new WebsocketServerEndpoint(targetClass
                    ,beanFactory.getBean(targetClass),wsServerEndpoint.value());
            actionDispatch.addWebsocketServerEndpoint(websocketServerEndpoint);
        }
        NettyWebsocketServer websocketServer = new NettyWebsocketServer(actionDispatch,websocketProperties);
        // 启动websocket
        websocketServer.start();
    }


    /**
     * 扫描WsServerEndpoint的Bean
     * @param packagesToScan 扫描包路径
     * @param registry
     */
    private void scanWebsocketServiceBeans(String packagesToScan, BeanDefinitionRegistry registry) {

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        // 扫描 @WsServerEndpoint标注的类
        scanner.addIncludeFilter(new AnnotationTypeFilter(WsServerEndpoint.class));
        scanner.scan(packagesToScan);
    }

    /**
     * 获取类型的目标类型
     * @param clazz
     * @return
     */
    public Class<?> getTargetClass(Class<?> clazz) {
        if (AopUtils.isCglibProxy(clazz)) {
            return clazz.getSuperclass();
        }
        return clazz;
    }
}
