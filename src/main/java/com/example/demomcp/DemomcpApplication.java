package com.example.demomcp;


import com.example.demomcp.service.SupermercadoToolsService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemomcpApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemomcpApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider supermercadoTools(SupermercadoToolsService tools) {
		return MethodToolCallbackProvider.builder().toolObjects(tools).build();
	}
}