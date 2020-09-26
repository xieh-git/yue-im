package com.yueya.im.node;

import com.yueya.im.node.listener.AppListener;
import com.yueya.im.server.ClusterNodeApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = {"com.yueya"},exclude = {DataSourceAutoConfiguration.class})
public class ImApplication {
    private static Logger logger = LoggerFactory.getLogger(ClusterNodeApp.class);
    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder().sources(ImApplication.class)
                .web(WebApplicationType.NONE)
                .build();
        application.addListeners(new AppListener());
        application.run(args);
        /*ImSession session = new ImSession("0001","aassssss","asdd");
        ImActorSystem.createUserActor(session);*/
    }

}
