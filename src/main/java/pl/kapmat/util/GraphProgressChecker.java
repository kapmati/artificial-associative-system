package pl.kapmat.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Checks creating/extending graph progress
 *
 * @author Mateusz Kaproń
 */
public class GraphProgressChecker implements Runnable {

	public static int index = 0;
	private double size = 0;

	public GraphProgressChecker(int size) {
		this.size = size;
	}

	@Override
	public void run() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		if (size > 0) {
			try {
				while ((index/size)*100 <= 98) {
					Thread.sleep(10000);
					System.out.println("[" + LocalDateTime.now().format(formatter) + "]" + " Graph progress: " + MathUtil.roundDouble((index/size) * 100, 3) + "%");
				}
				System.out.println("Graph progress: 100%");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			index = 0;
		}
	}
}
