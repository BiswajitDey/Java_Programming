package pcsaalgo;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.zip.CRC32;

import javax.swing.SwingUtilities;

import inputparser.InputParser;
import jfreechart.XYLineChartExample;

public class PCSAAlgo {
	final static int m = 32;
	final static int arraysize = 32;
	static Integer[][] bitSketch = new Integer[m][arraysize];
	static final double phi = 0.77351;
	static double sumZS = 0;
	static double result = 0;
	static int flag = 0;
	private static final String InputFilePath = "C:\\FlowTraffic.txt";
	private static HashMap<Double, Double> resultGraph = new HashMap<Double, Double>();

	private int offlineFMS() {
		int countZzz = 0;
		int k = getEmptyrows();
		double mulconst = 0.3;
		if ((double) k > (mulconst * (double) m)) {
			// System.out.println("Small Values");
			int result = (int) (((double) (2 * m)) * (Math.log((double) k / (double) m)));
			return (result * (-1));
		} else {
			// System.out.println("BigValues");
			// printBitArray();
			for (int i = 0; i < m; i++) {
				for (int j = bitSketch[i].length - 1; j >= 0; j--) {
					// System.out.println(bitSketch[i][j]);
					if (bitSketch[i][j] != 0)
						countZzz++;
					else {
						break;
					}
				}
				sumZS += countZzz;
				countZzz = 0;
			}
			// System.out.println(" sumZs = " + sumZS);
			double sumzsM = sumZS / m;
			result = m * (Math.pow(2, sumzsM)) / phi;
			int resultInt = (int) (result);
			// System.out.println(" N^ = " + resultInt);
			return resultInt;
		}
	}

	private int getEmptyrows() {
		int countZeroRows = 0;
		int flag = 0;
		for (int i = 0; i < m; i++) {
			for (int j = bitSketch[i].length - 1; j >= 0; j--) {
				if (bitSketch[i][j] != 0) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				countZeroRows++;
			}

		}
		return countZeroRows;
	}

	private void onlineFMS(List<String> destAddress) throws UnknownHostException, NoSuchAlgorithmException {

		flag = 0;
		for (String it : destAddress) {
			// int rowIndex = rowHash(it);
			int rowIndex = rowHashSHA512(it);
			int colIndex = geometricHash(it);
			colIndex = (bitSketch[rowIndex].length - 1) - colIndex;
			bitSketch[rowIndex][colIndex] = 1;
			// System.out.println("bitSketch[" + rowIndex + "][" + colIndex +
			// "]" + "=" + bitSketch[rowIndex][colIndex]);
		}

	}

	private void initBitSketch() {
		for (int i = 0; i < m; i++)
			for (int j = 0; j < arraysize; j++)
				bitSketch[i][j] = 0;

		sumZS = 0;
		result = 0;
	}

	static Random rn = new Random();

	private static int rowHash(String it) {
		long crc32 = 0;
		CRC32 crc = new CRC32();
		crc.update(it.getBytes());
		crc32 = crc.getValue();
		return (int) (crc32 % m);
	}

	private static int rowHashSHA512(String it) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(it.getBytes());
		byte[] byteData = md.digest();
		int res = getInt(byteData);

		if (res < 0)
			res = res * -1;

		return (int) (res % m);
	}

	private static int getInt(byte[] bytes) {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	private static int geometricHash(String address) throws UnknownHostException, NoSuchAlgorithmException {

		// debugging
		// String binaryAdd = Integer.toBinaryString(address.hashCode());
		// System.out.println(binaryAdd);

		int zeroCount = 0, check = 1;
		Boolean foundOne = false;

		while (!foundOne) {

			if ((address.hashCode() & check) == 0) {
				check = check << 1;
				zeroCount++;
			} else
				foundOne = true;
		}
		return zeroCount;
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		
		PCSAAlgo pcsaAlgo = new PCSAAlgo();
		Map<String, List<String>> inputMap = new HashMap<String, List<String>>();
		List<String> currentFlow;
		int i = 0, currentFlowSize = 0, res = 0;

		inputMap = InputParser.parseInputData(InputFilePath);

		// Processing for all flows
		for (Entry<String, List<String>> ee : inputMap.entrySet()) {
			currentFlow = ee.getValue();
			// System.out.println(ee.getKey() +" : "+ee.getValue() );
			currentFlowSize = currentFlow.size();

			// Processing for each flow
			i = 0;
			pcsaAlgo.initBitSketch();
			while (i < currentFlowSize) {
				try {
					pcsaAlgo.onlineFMS(currentFlow);
				} catch (UnknownHostException | NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}

			res = pcsaAlgo.offlineFMS();
			//if (currentFlowSize < 1000)
				resultGraph.put((double)currentFlowSize,(double) res);

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
}
