package com.geo.bridge.emitter.simulator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.test.context.ActiveProfiles;

import com.geo.bridge.domain.emitter.SimulatorService;
import com.geo.bridge.domain.emitter.integration.client.EmitterClient;
import com.geo.bridge.domain.emitter.integration.model.EmitterType;
import com.geo.bridge.domain.emitter.model.CreateSimulatorClientDTO;

import io.github.sctf.annotation.TargetComponent;
import io.github.sctf.annotation.TargetComponentTest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

// @SpringBootTest
@Slf4j
// @TargetComponent({SimulatorService.class})
// @TargetComponentTest(basePackage = "com.geo.bridge", stubSecurityInfrastructure = true)
@SpringBootTest
@ActiveProfiles("local")
class EmitterSimulatorTests {

	@Autowired
	private SimulatorService simulatorService;

	@Autowired
    private ApplicationContext context;

	static long startTime;

    @BeforeAll
    static void start() {
        startTime = System.currentTimeMillis();
    }

    @AfterAll
    static void end() {
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("⏱️ 전체 테스트 소요 시간: " + duration + "ms");
    }

	@Test
    void 빈_개수_측정() {
        log.info("🚀 현재 컨텍스트에 등록된 총 빈의 개수: {}", context.getBeanDefinitionCount());
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.stream(beanNames)
            .sorted()
            .forEach(System.out::println);
        System.out.println("총 빈 수: " + beanNames.length);
    }

	@Test
	void test() throws Exception{
		CreateSimulatorClientDTO createSimulatorClientDTO = new CreateSimulatorClientDTO();
		createSimulatorClientDTO.setName("테스트");
		createSimulatorClientDTO.setHost("tcp://52.78.183.35:12900");
		createSimulatorClientDTO.setTopic("track.smatii");
		createSimulatorClientDTO.setUsername("palnet");
		createSimulatorClientDTO.setPassword("palnet!234");
		createSimulatorClientDTO.setType(EmitterType.MQTT);
		
		Map<String, String> parameters = new HashMap<>();
		parameters.put("fbctnNo", "DJI");
		
		createSimulatorClientDTO.setParameter(parameters);

		Mono<EmitterClient> mono = simulatorService.createSimulatorClient(createSimulatorClientDTO);
		mono
			.doOnSuccess(client -> {
				log.info("Client :: {}", client);
			})
			.subscribe();
	}

	@Test
	void createSimulatorCoordinates() {

		List<Coordinate> path = new ArrayList<>();
		path.add(new Coordinate(126.9780, 37.5665)); // A지점
		path.add(new Coordinate(126.9784, 37.5670)); // B지점

		// Mono<List<Coordinate>> rs = simulatorService.createSimulatorCoordinates(path, 10D, 3);
		// rs
		// 	.doOnNext(coordinates -> log.info("DATA SIZE :: {}", coordinates.size()))
		// 	.flatMapIterable(coordinates -> coordinates) // FLUX를 MONO로
		// 	.delayElements(Duration.ofSeconds(1L)) // 1초마다
		// 	.flatMap(coordinate -> { // send
				
		// 		return null;
		// 	})
		// 	.log("SIMULATOR")
		// 	.blockLast(); // 실제코드에서는 subscribe로 변경 
	}

}
