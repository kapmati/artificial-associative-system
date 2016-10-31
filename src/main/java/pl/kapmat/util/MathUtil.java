package pl.kapmat.util;

/**
 * Math util class
 *
 * @author Mateusz Kaproń
 */
public class MathUtil {

	public static double roundDouble(double dValue, int precision) {
		dValue = dValue * Math.pow(10, precision);
		int intValue = (int) dValue;
		return intValue / Math.pow(10, precision);
	}

}
