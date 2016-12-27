package hyperloglog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import inputparser.InputParser;
import jfreechart.XYLineChartExample;

public class HyperLogLog {

	final static double b = 6;
	static int b_2Power = (int) Math.pow(2, b);
	static Integer[] mReg = new Integer[b_2Power];
	static int IntegerSize = 32;
	static double alphaValue = 0.709;
	private static final String InputFilePath = "C:\\FlowTraffic.txt";

	private static HashMap<Double, Double> resultGraph = new HashMap<Double, Double>();

	private int estimator() {
		double z = 0;
		int V = 0;
		double estimate_value;
		for (int i = 0; i < b_2Power; i++) {
			z = z + Math.pow(0.5, mReg[i]);
		}
		double m_square = Math.pow(b_2Power, 2);
		estimate_value = (m_square * alphaValue) / z;
		//handling smaller values
		if (estimate_value <= ((double) 5 / (double) 2) * (double) b_2Power) {
			for (int i = 0; i < b_2Power; i++) {
				if (mReg[i] == 0) {
					V++;
				}
			}
			if (V != 0) {
				estimate_value = ((double) b_2Power) * Math.log((double) b_2Power / (double) V);
			}
		}
		if (estimate_value > ((double) 1 / (double) 30) * ((double) Math.pow(2, 32))) {
			double temp = (1 - (estimate_value / ((double) Math.pow(2, 32))));
			estimate_value = -Math.pow(2, 32) * Math.log(temp);
		}
		return (int) estimate_value;
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		HyperLogLog hll = new HyperLogLog();
		Map<String, List<String>> inputMap = new HashMap<String, List<String>>();
		List<String> currentFlow;
		int currentFlowSize = 0;
		try {
			inputMap = InputParser.parseInputData(InputFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (Entry<String, List<String>> ee : inputMap.entrySet()) {
			currentFlow = ee.getValue();
			currentFlowSize = currentFlow.size();
			hll.init();
			if (currentFlowSize > 0) {
				for (String it : currentFlow) {
					long hashCode = rowHashSHA512(it); // hashvalue
					int j = hll.lsbNDigits(hashCode);  //extract LSB
					int rho = hll.NLessbitArray(hashCode); // store rest of the number
					mReg[j] = Math.max(mReg[j], rho);  //reg should have the largest value
				}
				int estimate_value = hll.estimator(); //call the estimator
				//if (estimate_value < 1000)
					resultGraph.put((double)currentFlowSize, (double)estimate_value);
			}
		}
		// Plotting Graph
		final XYLineChartExample graphPlotter = new XYLineChartExample(resultGraph);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				graphPlotter.setVisible(true);
			}
		});

	}

	/************** Utility Functions *********************/

	private static Long[] decToBinary(long numericalValue) {
		Long[] bitarrayrev = new Long[IntegerSize];
		Long[] bitarray = new Long[IntegerSize];

		for (int i = 0; i < IntegerSize; i++) {
			bitarrayrev[i] = numericalValue & 0x1;
			numericalValue = numericalValue >> 1;
		}
		for (int i = 0; i < IntegerSize; i++) {
			bitarray[i] = bitarrayrev[(IntegerSize - 1) - i];
		}
		return bitarray;
	}

	private static int runningZeros(Long[] newArray) {
		int count = 0;
		int lastPos = IntegerSize - 1;
		for (int i = lastPos; i >= 0; i--) {
			if (newArray[i] == 0) {
				count = count + 1;
			} else {
				break;
			}
		}
		return count;
	}

	private static int rowHashSHA512(String it) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(it.getBytes());
		byte[] byteData = md.digest();
		int res = getInt(byteData);
		return (int) Math.abs(res);
	}

	private static int getInt(byte[] bytes) {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	private void init() {
		int i = 0;
		while (i < b_2Power) {
			mReg[i++] = 0;
		}
	}

	private int lsbNDigits(long hCode) {
		int a = (int) (hCode & 0x3F);
		return a;
	}

	private int NLessbitArray(long hCode) throws NoSuchAlgorithmException {
		long a = hCode >> (int) b;
		Long[] newArray = HyperLogLog.decToBinary(a);
		int x = HyperLogLog.runningZeros(newArray);
		return x + 1;
	}

}
