package probabilisticounting;

import jfreechart.XYLineChartExample;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.CRC32;

import javax.swing.SwingUtilities;

import inputparser.InputParser;

public class ProbabilistiCounting {
	private static final String InputFilePath = "C:\\FlowTraffic.txt";
	private static final int MaxBitArraySize = 205;
	private static int B[] = new int[MaxBitArraySize];
	private static HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
	private static HashMap<Double, Double> result_bias = new HashMap<Double, Double>();

	
	private void resetBitArray() {
		int i = 0;
		for (i = 0; i < MaxBitArraySize; i++) {
			B[i] = 0;
		}
	}

	private int countZeroes() {
		int i = 0, count = 0;
		for (i = 0; i < MaxBitArraySize; i++) {
			if (B[i] == 0) {
				count++;
			}
		}
		return count;

	}

	private void setBitArray(String element) {
		long crc32 = 0;
		int bitB = 0;
		CRC32 crc = new CRC32();

		crc.update(element.getBytes());

		crc32 = crc.getValue();

		bitB = (int) (crc32 % MaxBitArraySize);

		B[Math.abs(bitB)] = 1;

	}

	public static void main(String[] args) {

		Map<String, List<String>> inputMap = new HashMap<String, List<String>>();

		int i = 0, currentFlowSize, countZeroesVs = 0, nEstimate = 0;
		double Vs = 0;
		String currentFlowId = "";
		List<String> currentFlow;
		ProbabilistiCounting probCounting = new ProbabilistiCounting();
		double errorRate = 0.0;
		int noFlows = 0;
		int no100 = 0, no500 = 0, no1000 = 0;
		double variance = 0.0;
		double bias = 0.0;
		double t = 0.0;
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
			probCounting.resetBitArray();

			if (currentFlowSize <= 1000) {
				// Processing for each flow
				i = 0;
				while (i < currentFlowSize) {
					probCounting.setBitArray(currentFlow.get(i));
					i++;

				}
				if (currentFlowSize > 100) {
					// System.out.println("current flowsize : " +
					// currentFlowSize + " count :" + flowCount);
				}
				countZeroesVs = probCounting.countZeroes();
				Vs = (double) countZeroesVs / MaxBitArraySize;

				// System.out.println("CountZeroes : " + countZeroesVs + "Vs : "
				// +
				// Vs);

				nEstimate = (int) (((-1) * MaxBitArraySize * java.lang.Math.log(Vs)));

				// nEstimate = (int) ( (-1) * MaxBitArraySize *
				// (java.lang.Math.log(Vs / (double) samplerate)));

				// nEstimate = (int) (((-1) * MaxBitArraySize *
				// java.lang.Math.log(Vs)));

				// System.out.println("FlowId : " + currentFlowId + " n = " +
				// currentFlowSize + " n^ = " + nEstimate);

				
				//System.out.println("errorrate = " + (nEstimate - currentFlowSize));
				
				errorRate += Math.abs((nEstimate - currentFlowSize));
				
				t = (double) (currentFlowSize) / (double) MaxBitArraySize;
				variance = ((double) (1 / (double) MaxBitArraySize))
						* ((double) Math.exp(-t) * (1 - ((1 + t) * ((double) Math.exp(-t)))));
				bias = ((double) Math.exp(t) - t - 1) / (double) (2 * currentFlowSize);

				result_bias.put((double)currentFlowSize, variance);
				
				
				noFlows++;
				
				if(currentFlowSize > 0 && currentFlowSize < 50) {
					no100++;
				} else if(currentFlowSize >= 50 && currentFlowSize < 500) {
					no500++;
				} else {
					no1000++;
				}
				
				result.put(currentFlowSize, nEstimate);
			}
			
			
		}

		System.out.println("Average error rate : " + errorRate /(double) noFlows +"%");
		//System.out.println("Noflows :" + noFlows + " no100: " +no100 + "no500: "+ no500 + "no1000: " + no1000);
		
		/*
		final XYLineChartExample graphPlotter = new XYLineChartExample(result, "PCA - 205 bits per flow");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				graphPlotter.setVisible(true);
			}
		});
		*/
		
		final XYLineChartExample graphPlotter = new XYLineChartExample(result_bias, "PCA - 205bits per flow");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				graphPlotter.setVisible(true);
			}
		});

	}

}
