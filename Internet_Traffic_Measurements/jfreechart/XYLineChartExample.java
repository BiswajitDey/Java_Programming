package jfreechart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This program demonstrates how to draw XY line chart with XYDataset using
 * JFreechart library.
 * 
 * @author www.codejava.net
 *
 */
public class XYLineChartExample extends JFrame {

	private static final long serialVersionUID = 1L;


	public XYLineChartExample(HashMap<Double, Double> data, String algoName) {
		super("FPCA Algorithm Evaluation");

		JPanel chartPanel = createChartPanel(data, algoName);
		add(chartPanel, BorderLayout.CENTER);

		setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private JPanel createChartPanel(HashMap<Double, Double> data, String algoName) {
		String chartTitle = algoName;
		String xAxisLabel = "Sampling Rate %";
		String yAxisLabel = "Space Optmized %";
		boolean rootPaneCheckingEnabled = false;
		XYDataset dataset = createDataset(data);

		//JFreeChart chart = ChartFactory.createScatterPlot(chartTitle, xAxisLabel, yAxisLabel, dataset,
			//	PlotOrientation.VERTICAL, rootPaneCheckingEnabled, rootPaneCheckingEnabled, rootPaneCheckingEnabled);

		
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset,
				PlotOrientation.VERTICAL, rootPaneCheckingEnabled, rootPaneCheckingEnabled, rootPaneCheckingEnabled);
		
		// boolean showLegend = false;
		// boolean createURL = false;
		// boolean createTooltip = false;
		//
		// JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
		// xAxisLabel, yAxisLabel, dataset,
		// PlotOrientation.HORIZONTAL, showLegend, createTooltip, createURL);

		// customizeChart(chart);

		// saves the chart as an image files
		File imageFile = new File(algoName + ".png");
		int width = 640;
		int height = 480;

		try {
			ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
		} catch (IOException ex) {
			System.err.println(ex);
		}

		return new ChartPanel(chart);
	}

	private XYDataset createDataset(HashMap<Double, Double> data) {

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("ITM Project");
		XYSeries series2 = new XYSeries("RefGraph");

		for (Map.Entry<Double, Double> entry : data.entrySet()) {
			series1.add((double) entry.getKey(), (double) entry.getValue());
		}

/*		int i = 0;
		while (i < 9000) {
			series2.add(i, i);
			i=i+1000;
		}
*/
	//	dataset.addSeries(series2);
		dataset.addSeries(series1);

		return dataset;
	}

	
	private void customizeChart(JFreeChart chart) {
		XYPlot plot = chart.getXYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// sets paint color for each series
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.YELLOW);

		// sets thickness for series (using strokes)
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		renderer.setSeriesStroke(2, new BasicStroke(2.0f));

		// sets paint color for plot outlines
		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		// sets renderer for lines
		plot.setRenderer(renderer);

		// sets plot background
		plot.setBackgroundPaint(Color.DARK_GRAY);

		// sets paint color for the grid lines
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

	}
}
