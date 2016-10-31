package pl.kapmat.algorithm;

import pl.kapmat.util.MathUtil;

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
		if (size > 0) {
			try {
				while ((index/size)*100 <= 98) {
					Thread.sleep(10000);
					System.out.println("Graph building: " + MathUtil.roundDouble((index/size) * 100, 3) + "%");
				}
				System.out.println("Graph building: 100%");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			index = 0;
		}
	}
}
