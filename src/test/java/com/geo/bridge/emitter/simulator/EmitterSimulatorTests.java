package com.geo.bridge.emitter.simulator;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.geo.bridge.domain.emitter.EmitterSimulatorService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class EmitterSimulatorTests {

	@Autowired
	private EmitterSimulatorService simulatorService;

	@Test
	void createSimulatorCoordinates() {

		List<Coordinate> path = new ArrayList<>();
		path.add(new Coordinate(126.9780, 37.5665)); // A지점
		path.add(new Coordinate(126.9784, 37.5670)); // B지점

		List<Coordinate> rs = simulatorService.createSimulatorCoordinates(path, 10D, 3);

		log.info("RS :: {}", rs);
	}

}
