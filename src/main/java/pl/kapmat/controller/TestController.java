package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.kapmat.algorithm.AasGraph;

/**
 * Controller created for manual tests
 *
 * @author Mateusz Kaproń
 */
@Controller
@RequestMapping("/test")
public class TestController {

	@Autowired
	private AasGraph aasGraph;

	@RequestMapping("/readTest")
	@ResponseBody
	public String readTest() {
		return "Nodes: " + aasGraph.readTest();
	}
}
