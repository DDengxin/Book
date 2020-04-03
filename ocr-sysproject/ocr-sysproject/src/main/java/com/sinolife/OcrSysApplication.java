package com.sinolife;

//import com.sinolife.sf.rpc.RpcFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OcrSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcrSysApplication.class, args);
    }

//    @Bean
//    public RpcFilter rpcFilter() {
//        return new RpcFilter();
//    }

}
