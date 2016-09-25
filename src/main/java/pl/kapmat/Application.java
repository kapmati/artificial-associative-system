package pl.kapmat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.algorithm.AasGraph;
import pl.kapmat.algorithm.Node;
import pl.kapmat.util.HibernateUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Main class
 *
 * Created by Kapmat on 2016-09-21.
 */

public class Application {

	private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {

		//ADD NEW SENTENCES
//		InsertSentenceWorker.insertSentences();

		AasGraph aasGraph = new AasGraph();
		aasGraph.run();

		//Probably there is a bug in a Hibernate lib 4.x and it's necessary to close session at the end of main method
		HibernateUtil.getSessionFactory().close();
	}
}
