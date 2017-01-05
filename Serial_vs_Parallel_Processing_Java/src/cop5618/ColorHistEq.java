package cop5618;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javax.swing.colorchooser.ColorSelectionModel;

import java.awt.Color;

public class ColorHistEq {

	// Use these labels to instantiate you timers. You will need 8 invocations
	// of now()
	static String[] labels = { "getRGB", "convert to HSB", "create brightness map", "parallel prefix",
			"probability array", "equalize pixels", "setRGB" };

	private static final int MaxBins = 256;

	static Timer colorHistEq_serial(BufferedImage image, BufferedImage newImage) {
		Timer times = new Timer(labels);
		/**
		 * IMPLEMENT SERIAL METHOD
		 */
		int w = image.getWidth();
		int h = image.getHeight();
		times.now();
		int[] srcPixelArray = image.getRGB(0, 0, w, h, new int[w * h], 0, w);

		times.now();

		float[][] hsbDetails = Arrays.stream(srcPixelArray).mapToObj(pixel -> convertRGBtoHSB(pixel))
				.toArray(float[][]::new);

		times.now();

		Double[] hsbBright = new Double[srcPixelArray.length];

		for (int i = 0; i < hsbDetails.length; i++) {
			hsbBright[i] = (double) hsbDetails[i][2];
		}

		ArrayList<Double> hsbBrightList = new ArrayList<Double>(Arrays.asList(hsbBright));

		Map<Double, Long> hsbBrightCol = hsbBrightList.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		times.now();

		Map<Integer, Long> binPackingMap = new HashMap<Integer, Long>(MaxBins + 1);

		// Initialize binPackingMap
		for (int i = 0; i <= MaxBins; i++) {
			binPackingMap.put(i, 0L);
		}

		// Make MaxBins + 1
		for (Map.Entry<Double, Long> entry : hsbBrightCol.entrySet()) {
			double key = entry.getKey();
			Long value = entry.getValue();

			int binNum = (int) Math.floor((key * (double) MaxBins));
			binPackingMap.put(binNum, binPackingMap.get(binNum) + value);
		}

		double cumulativeSum = 0;

		Long[] array = new Long[binPackingMap.size()];
		array = binPackingMap.values().toArray(array);

		BinaryOperator<Long> opt = (f1, f2) -> (f1 + f2);

		Arrays.parallelPrefix(array, opt);

		times.now();

		cumulativeSum = array[MaxBins];

		// Compute cumulative probability in a map
		Map<Integer, Double> cumulativeProbability = new HashMap<Integer, Double>(4);
		for (int i = 0; i < array.length; i++) {
			cumulativeProbability.put(i, ((double) array[i] / cumulativeSum));
		}

		times.now();

		for (int j = 0; j < hsbDetails.length; j++) {

			double val = hsbDetails[j][2];

			int binNum = (int) Math.floor((val * (double) MaxBins));
			hsbDetails[j][2] = cumulativeProbability.get(binNum).floatValue();

		}

		int[] destPixelArray = Arrays.stream(hsbDetails).mapToInt((pixel -> convertHSBtoRGB(pixel))).toArray();

		times.now();

		newImage.setRGB(0, 0, w, h, destPixelArray, 0, w);

		times.now();

		return times;
	}

	static Timer colorHistEq_parallel(FJBufferedImage image, FJBufferedImage newImage) {
		Timer times = new Timer(labels);
		/**
		 * IMPLEMENT PARALLEL METHOD
		 */
		int w = image.getWidth();
		int h = image.getHeight();
		times.now();
		int[] srcPixelArray = image.getRGB(0, 0, w, h, new int[w * h], 0, w);

		times.now();

		float[][] hsbDetails = Arrays.stream(srcPixelArray).parallel().mapToObj(pixel -> convertRGBtoHSB(pixel))
				.toArray(float[][]::new);

		times.now();

		Double[] hsbBright = new Double[srcPixelArray.length];

		for (int i = 0; i < hsbDetails.length; i++) {
			hsbBright[i] = (double) hsbDetails[i][2];
		}

		ArrayList<Double> hsbBrightList = new ArrayList<Double>(Arrays.asList(hsbBright));

		Map<Double, Long> hsbBrightCol = hsbBrightList.stream().parallel()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		times.now();

		Map<Integer, Long> binPackingMap = new HashMap<Integer, Long>(MaxBins + 1);

		// Initialize binPackingMap
		for (int i = 0; i <= MaxBins; i++) {
			binPackingMap.put(i, 0L);
		}

		// Make MaxBins + 1
		for (Map.Entry<Double, Long> entry : hsbBrightCol.entrySet()) {
			double key = entry.getKey();
			Long value = entry.getValue();

			int binNum = (int) Math.floor((key * (double) MaxBins));
			binPackingMap.put(binNum, binPackingMap.get(binNum) + value);
		}

		double cumulativeSum = 0;

		Long[] array = new Long[binPackingMap.size()];
		array = binPackingMap.values().toArray(array);

		BinaryOperator<Long> opt = (f1, f2) -> (f1 + f2);

		Arrays.parallelPrefix(array, opt);

		times.now();

		cumulativeSum = array[MaxBins];

		// Compute cumulative probability in a map
		Map<Integer, Double> cumulativeProbability = new HashMap<Integer, Double>(4);
		for (int i = 0; i < array.length; i++) {
			cumulativeProbability.put(i, ((double) array[i] / cumulativeSum));
		}

		times.now();

		for (int i = 0; i < hsbDetails.length; i++) {

			double val = hsbDetails[i][2];

			int binNum = (int) Math.floor((val * (double) MaxBins));
			hsbDetails[i][2] = cumulativeProbability.get(binNum).floatValue();

		}

		int[] destPixelArray = Arrays.stream(hsbDetails).parallel().mapToInt((pixel -> convertHSBtoRGB(pixel)))
				.toArray();

		times.now();

		newImage.setRGB(0, 0, w, h, destPixelArray, 0, w);

		times.now();

		return times;
	}

	private static Object convertRGBtoHSB(int pixel) {
		ColorModel colorModel = ColorModel.getRGBdefault();
		float[] hsb = new float[3];
		Color.RGBtoHSB((colorModel.getRed(pixel)), (colorModel.getGreen(pixel)), (colorModel.getBlue(pixel)), hsb);
		return hsb;
	}

	private static int convertHSBtoRGB(float[] pixel) {
		return Color.HSBtoRGB(pixel[0], pixel[1], pixel[2]);
	}

}