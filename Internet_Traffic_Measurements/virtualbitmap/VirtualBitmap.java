package virtualbitmap;

import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import inputparser.InputParser;
import jfreechart.XYLineChartExample;

public class VirtualBitmap {

	private static final String InputFilePath = "C:\\FlowTraffic_1.txt";
	private static final int MaxBitArray = 8000000;
	private static final int MaxVirtualBitmapSize = 2500;

	private static int B[] = new int[MaxBitArray];
	private static int R[] = new int[MaxVirtualBitmapSize];

	private static Random rand = new Random();

	private static HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

	private static int getInt(byte[] bytes) {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	private static int getMD5(String input) throws NoSuchAlgorithmException {
		int result;
		byte[] byteArr;
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(input.getBytes());
		byteArr = md.digest();
		result = getInt(byteArr);
		return result;
	}

	private int getCountZeroesVs(String flowId) throws NoSuchAlgorithmException {
		int i = 0;
		int count = 0;
		int bitB = 0;
		int t;

		Integer input;

		for (i = 0; i < MaxVirtualBitmapSize; i++) {

			t = getMD5(flowId);

			input = (int) (t ^ R[i]);

			String str = input.toString();

			t = getMD5(str);

			bitB = (int) (t % MaxBitArray);
			/*
			 * if (bitB < 0) { System.out.println("less than zero"); bitB = 0; }
			 */

			if (B[Math.abs(bitB)] == 0) {
				count++;
			}

		}

		// System.out.println("CountzeroVs : " + count);
		return count;
	}

	private int countZeroesB() {
		int i = 0;
		int count = 0;
		for (i = 0; i < MaxBitArray; i++) {
			if (B[i] == 0) {
				count++;
			}
		}
		// System.out.println("CountzeroB : " + count);
		return count;

	}

	private void setBitArray(String flowId, String element) throws NoSuchAlgorithmException {
		int bitB = 0;
		int bitVs = 0;
		int t;

		t = getMD5(element);

		bitVs = (int) (t % MaxVirtualBitmapSize);

		t = getMD5(flowId);

		Integer input = (int) (t ^ R[Math.abs(bitVs)]);

		String str = input.toString();

		t = getMD5(str);

		bitB = (int) (t % MaxBitArray);

		B[Math.abs(bitB)] = 1;
	}

	private void initR() {
		int temprand;
		for (int i = 0; i < MaxVirtualBitmapSize; i++) {
			temprand = rand.nextInt();
			R[i] = temprand;
		}
	}

	private void initB() {
		for (int i = 0; i < MaxBitArray; i++) {
			B[i] = 0;
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		Map<String, List<String>> inputMap = new HashMap<String, List<String>>();
		int i = 0, currentFlowSize = 0, countZeroesVs = 0, nEstimate = 0, countZeroesVm = 0;
		double Vs = 0, Vm = 0;
		String currentFlowId = "";
		List<String> currentFlow;
		VirtualBitmap virtualBitMap = new VirtualBitmap();

		virtualBitMap.initR();
		virtualBitMap.initB();

		try {
			inputMap = InputParser.parseInputData(InputFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Processing for all flows
		for (Entry<String, List<String>> ee : inputMap.entrySet()) {
			currentFlowId = ee.getKey();
			currentFlow = ee.getValue();
			currentFlowSize = currentFlow.size();

			// Processing for each flow
			i = 0;
			while (i < currentFlowSize) {
				virtualBitMap.setBitArray(currentFlowId, currentFlow.get(i));
				i++;
			}
			// virtualBitMap.resetVs();

		}

		// Overall count of zeroes in B
		countZeroesVm = virtualBitMap.countZeroesB();
		Vm = (double) countZeroesVm / MaxBitArray;

		// System.out.println("Zeroes : " + zeroCount);

		for (Entry<String, List<String>> ee : inputMap.entrySet()) {
			currentFlowId = ee.getKey();
			currentFlow = ee.getValue();
			currentFlowSize = currentFlow.size();

			// virtualBitMap.countZeroesVs(currentFlowId);
			countZeroesVs = virtualBitMap.getCountZeroesVs(currentFlowId);

			Vs = (double) countZeroesVs / MaxVirtualBitmapSize;

			nEstimate = (int) ((-1) * MaxVirtualBitmapSize * java.lang.Math.log(Vs)
					+ MaxVirtualBitmapSize * java.lang.Math.log(Vm));

			result.put(currentFlowSize, Math.abs(nEstimate));

		}

		final XYLineChartExample graphPlotter = new XYLineChartExample(result, "Virtual Bitmap");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				graphPlotter.setVisible(true);
			}
		});

	}
}