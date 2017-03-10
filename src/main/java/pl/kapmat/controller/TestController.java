package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.kapmat.algorithm.AasGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller created for manual tests
 *
 * @author Mateusz Kaproń
 */
@Controller
@RequestMapping("/test")
public class TestController {

	private AasGraph aasGraph;

	@Autowired
	public TestController(AasGraph aasGraph) {
		this.aasGraph = aasGraph;
	}

	@RequestMapping(value = "/readTest", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> readTest() {
		Map<String, Integer> testMap = new HashMap<>();
		testMap.put("Nodes", aasGraph.readTest());
		testMap.put("Test communication", 432);
		return new ResponseEntity<>(testMap, HttpStatus.OK);
	}
}
