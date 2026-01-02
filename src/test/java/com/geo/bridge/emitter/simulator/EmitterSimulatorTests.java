package com.geo.bridge.emitter.simulator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.geo.bridge.domain.emitter.SimulatorService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SpringBootTest
@Slf4j
class EmitterSimulatorTests {

	@Autowired
	private SimulatorService simulatorService;

	@Test
	void createSimulatorCoordinates() {

		List<Coordinate> path = new ArrayList<>();
		path.add(new Coordinate(126.9780, 37.5665)); // A지점
		path.add(new Coordinate(126.9784, 37.5670)); // B지점

		Mono<List<Coordinate>> rs = simulatorService.createSimulatorCoordinates(path, 10D, 3);
		rs
			.doOnNext(coordinates -> log.info("DATA SIZE :: {}", coordinates.size()))
			.flatMapIterable(coordinates -> coordinates) // FLUX를 MONO로
			.delayElements(Duration.ofSeconds(1L)) // 1초마다
			.flatMap(coordinate -> { // send
				
				return null;
			})
			.log("SIMULATOR")
			.blockLast(); // 실제코드에서는 subscribe로 변경 
	}

}
