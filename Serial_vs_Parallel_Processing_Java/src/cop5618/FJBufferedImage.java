package cop5618;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class FJBufferedImage extends BufferedImage {
	private static final int GetRGB = 1;
	private static final int SetRGB = 0;

	private class FJImplementer extends RecursiveAction {

		private static final long serialVersionUID = 1L;
		private int xStart;
		private int yStart;
		private int w;
		private int h;
		private int[] rgbArray;
		private int offset;
		private int scansize;
		private int[] getRes;
		private FJBufferedImage fjb;
		private int flag;

		// For given image keeping threshold as y/(2^3) y = 2988
		protected static final int threshold = 374;

		public FJImplementer(FJBufferedImage obj, int xStart, int yStart, int w, int h, int[] rgbArray, int offset,
				int scansize, int[] getResult, int flag) {
			fjb = obj;
			this.xStart = xStart;
			this.yStart = yStart;
			this.w = w;
			this.h = h;
			this.rgbArray = rgbArray;
			this.offset = offset;
			this.scansize = scansize;
			this.getRes = getResult;
			this.flag = flag;
		}

		@Override
		protected void compute() {

			if (h < threshold) {
				computeDirectly();
				return;
			}

			int split = h / 2;

			invokeAll(new FJImplementer(fjb, xStart, yStart, w, split, rgbArray, offset, scansize, getRes, flag),
					new FJImplementer(fjb, xStart, yStart + split, w, h - split, rgbArray, offset, scansize, getRes,
							flag));

		}

		protected void computeDirectly() {
			if (flag == SetRGB) {
				offset = (offset + ((yStart) * scansize) + (xStart));
				fjb.setRGBSuper(xStart, yStart, w, h, rgbArray, offset, scansize);
			} else {
				offset = (offset + ((yStart) * scansize) + (xStart));
				int[] rgb = fjb.getRGBSuper(xStart, yStart, w, h, rgbArray, offset, scansize);

				for (int i = offset; i < (offset + h * scansize); i++) {
					getRes[i] = rgb[i];
				}
			}

		}

	}

	/** Constructors */

	public FJBufferedImage(int width, int height, int imageType) {
		super(width, height, imageType);
	}

	public FJBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
		super(width, height, imageType, cm);
	}

	public FJBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied,
			Hashtable<?, ?> properties) {
		super(cm, raster, isRasterPremultiplied, properties);
	}

	/**
	 * Creates a new FJBufferedImage with the same fields as source.
	 * 
	 * @param source
	 * @return
	 */
	public static FJBufferedImage BufferedImageToFJBufferedImage(BufferedImage source) {
		Hashtable<String, Object> properties = null;
		String[] propertyNames = source.getPropertyNames();
		if (propertyNames != null) {
			properties = new Hashtable<String, Object>();
			for (String name : propertyNames) {
				properties.put(name, source.getProperty(name));
			}
		}
		return new FJBufferedImage(source.getColorModel(), source.getRaster(), source.isAlphaPremultiplied(),
				properties);
	}

	@Override
	public void setRGB(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize) {
		/**** IMPLEMENT THIS METHOD USING PARALLEL DIVIDE AND CONQUER *****/
		FJImplementer fjImpl = new FJImplementer(this, xStart, yStart, w, h, rgbArray, offset, scansize, null, SetRGB);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(fjImpl);
	}

	@Override
	public int[] getRGB(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize) {
		/**** IMPLEMENT THIS METHOD USING PARALLEL DIVIDE AND CONQUER *****/
		int[] getResult = new int[w * h];
		FJImplementer fjImpl = new FJImplementer(this, xStart, yStart, w, h, rgbArray, offset, scansize, getResult,
				GetRGB);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(fjImpl);
		return getResult;
	}

	public void setRGBSuper(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize) {
		super.setRGB(xStart, yStart, w, h, rgbArray, offset, scansize);
	}

	public int[] getRGBSuper(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize) {
		return super.getRGB(xStart, yStart, w, h, rgbArray, offset, scansize);
	}

}
