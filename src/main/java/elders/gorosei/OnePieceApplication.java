package elders.gorosei;

import elders.gorosei.common.filters.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@CrossOrigin
public class OnePieceApplication implements WebMvcConfigurer {
	private final Filter filter;

	@Autowired
	public OnePieceApplication(Filter filter) {
		this.filter = filter;
	}

	public static void main(String[] args) {
		SpringApplication.run(OnePieceApplication.class, args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(filter);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/ws/**")
				.allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTION")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}
