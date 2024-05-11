package elders.gorosei;

import elders.gorosei.common.filters.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
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
}
