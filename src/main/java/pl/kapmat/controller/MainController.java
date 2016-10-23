package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.kapmat.algorithm.AasGraph;
import pl.kapmat.dao.SentenceDAO;

/**
 * Main controller
 *
 * Created by Kapmat on 2016-10-23.
 */
@Controller
public class MainController {

	@Autowired
	private SentenceDAO sentenceDAO;

	@RequestMapping("/")
	@ResponseBody
	public String index() {
		AasGraph aasGraph = new AasGraph();
		aasGraph.run();

		return "Main page - Artificial associative system";
	}
}
