package virtualhyperloglog;

import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.SwingUtilities;

import inputparser.InputParser;
import jfreechart.XYLineChartExample;

public class VirtualHyperLoglog {
	private static final String InputFilePath = "C:\\FlowTraffic_1.txt";
	private static HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

	private static int nSketches = 256;
	private static int totSketches = 2000000;
	private double nEst;
	private int R[];

	private static int M[][] = new int[totSketches][1];

	private static void initM() {
		for (int i = 0; i < totSketches; i++) {
			M[i][0] = 0;
		}
	}

	private static int getInt(byte[] bytes) {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	private static int getSHA512(String input) throws NoSuchAlgorithmException {
		int result;
		byte[] byteArr;
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.reset();
		md.update(input.getBytes());
		byteArr = md.digest();
		result = getInt(byteArr);
		return result;
	}

	private double alphaS(int s) {
		switch (s) {
		case 16:
			return 0.673;
		case 32:
			return 0.697;
		case 64:
			return 0.709;
		default:
			return (0.7213) / (1 + (1.079 / s));
		}
	}

	private int calcNs(String source) throws NoSuchAlgorithmException {
		double sum = 0.0;
		int countZero = 0;

		for (int k = 0; k < nSketches; k++) {
			Integer i = getSHA512(source) ^ R[k];
			int fp = getSHA512(i.toString());
			fp = fp % totSketches;
			fp = fp < 0 ? fp + totSketches : fp;

			int val = M[fp][0];

			if (val == 0)
				countZero++;
			sum += Math.pow(2.0, -val);
		}
		double nS = alphaS(nSketches) * nSketches * nSketches / sum;
		nS = lowValCorrections(nS, countZero, nSketches);
		double nF;
		double m = totSketches;
		double s = nSketches;
		nF = (m * s / (m - s)) * Math.abs(nS / s - nEst / m);
		return (int) nF;

	}

	public double lowValCorrections(double estVal, int countZero, int nSk) {
		if (estVal <= 2.5 * nSk) {
			if (countZero != 0) {
				estVal = nSk * Math.log(nSk / (double) countZero);
			}
		} else if (estVal > Math.pow(2, 32) / 30) {
			estVal = -Math.pow(2, 32) * (1 - estVal / Math.pow(2, 32));
		}
		return estVal;
	}

	public void calcOverallN() {
		double nEst = 0.0;
		double sum = 0.0;
		int countZero = 0;
		for (int i = 0; i < totSketches; i++) {
			int val = M[i][0];
			sum += Math.pow(2.0, -val);
			if (val == 0)
				countZero++;
		}
		nEst = alphaS(totSketches) * (double) totSketches * (double) totSketches / sum;
		nEst = lowValCorrections(nEst, countZero, totSketches);
		this.nEst = nEst;
	}

	private static Integer[] decToBinary(int numericalValue) {
		Integer[] bitarrayrev = new Integer[32];
		Integer[] bitarray = new Integer[32];

		for (int i = 0; i < 32; i++) {
			bitarrayrev[i] = numericalValue & 0x1;
			numericalValue = numericalValue >> 1;
		}
		for (int i = 0; i < 32; i++) {
			bitarray[i] = bitarrayrev[(32 - 1) - i];
		}
		return bitarray;
	}

	private static int countConZeroes(String it) throws NoSuchAlgorithmException {
		int a = getSHA512(it) >> (int) 8;

		Integer[] newArray = decToBinary(a);

		int x = countZeroes(newArray);
		return x + 1;
	}

	private static int countZeroes(Integer[] newArray) {
		int pho = 0;
		int lastPos = 32 - 1;
		for (int i = lastPos; i >= 0; i--) {
			if (newArray[i] == 0) {
				pho = pho + 1;
			} else {
				break;
			}
		}
		return pho;
	}

	private static int extractNDigits(String it) {
		int a = it.hashCode() & 0xFF;
		return a;
	}

	private void processOnlineData(String source, String destination) throws NoSuchAlgorithmException {

		int p = extractNDigits(destination);

		p = p % nSketches;
		p = p < 0 ? p + nSketches : p;

		int q = countConZeroes(destination);

		Integer ip = getSHA512(source) ^ R[p];
		int f_p = getSHA512(ip.toString());

		f_p = f_p % totSketches;
		f_p = f_p < 0 ? f_p + totSketches : f_p;

		M[f_p][0] = Math.max(M[f_p][0], q);
	}

	private void initR() {
		R = new int[nSketches];
		Random r = new Random();
		for (int i = 0; i < nSketches; i++) {
			R[i] = r.nextInt();
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		Map<String, List<String>> inputMap = new HashMap<String, List<String>>();
		int i = 0, currentFlowSize = 0;
		String currentFlowId = "";
		List<String> currentFlow;
		initM();
		int actualOverallCardinality = 0;

		VirtualHyperLoglog virtualHyperLogLog = new VirtualHyperLoglog();

		virtualHyperLogLog.initR();

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

			if (currentFlowSize <= 1000) {
				// Processing for each flow
				i = 0;
				while (i < currentFlowSize) {
					virtualHyperLogLog.processOnlineData(currentFlowId, currentFlow.get(i));
					i++;
				}

				actualOverallCardinality += currentFlowSize;
			}
		}

		virtualHyperLogLog.calcOverallN();
		System.out.println("Overall cardinality estimation : " + (int) virtualHyperLogLog.nEst + " Actual :"
				+ actualOverallCardinality);

		for (Entry<String, List<String>> ee : inputMap.entrySet()) {
			currentFlowId = ee.getKey();
			currentFlow = ee.getValue();
			currentFlowSize = currentFlow.size();

			if (currentFlowSize <= 1000) {

				int n_estimate = virtualHyperLogLog.calcNs(currentFlowId);

				result.put(currentFlowSize, n_estimate);
			}
		}

		final XYLineChartExample graphPlotter = new XYLineChartExample(result, "Virtual Hyperloglog");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				graphPlotter.setVisible(true);
			}
		});

	}

}
