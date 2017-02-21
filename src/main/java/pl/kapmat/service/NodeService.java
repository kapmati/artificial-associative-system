package pl.kapmat.service;

import org.springframework.stereotype.Service;
import pl.kapmat.algorithm.Node;

import java.io.*;
import java.util.Set;

/**
 * Node service
 *
 * @author Mateusz Kaproń
 */
@Service
public class NodeService {

	private static String SERIALIZE_DATA_FILE_NAME = "setOfNodes.ser";

	public Node getNodeFromSet(Set<Node> nodeSet, String word) {
			return nodeSet.stream().filter(node -> node.getWord().equals(word.toUpperCase())).findFirst().get();
	}

	public void serializeSetOfNodes(Set<Node> nodeSet) {
		try (OutputStream file = new FileOutputStream(SERIALIZE_DATA_FILE_NAME);
			 OutputStream buffer = new BufferedOutputStream(file);
			 ObjectOutput output = new ObjectOutputStream(buffer);
		) {
			output.writeObject(nodeSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Set<Node> deserializeSetOfNodes() {
		Set<Node> setOfNodes = null;
		try (InputStream file = new FileInputStream(SERIALIZE_DATA_FILE_NAME);
			 InputStream buffer = new BufferedInputStream(file);
			 ObjectInput input = new ObjectInputStream (buffer);
		) {
			setOfNodes = (Set<Node>) input.readObject();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return setOfNodes;
	}

}
