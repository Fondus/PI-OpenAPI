package tw.fondus.fews.openapi.transformation;

import nl.wldelft.fews.openapi.transformationmodule.Calculation;
import nl.wldelft.fews.openapi.transformationmodule.Input;
import nl.wldelft.fews.openapi.transformationmodule.Output;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import tw.fondus.fews.openapi.transformation.util.TransformationUtils;

/**
 * The custom transformation of filter the rainfall noise.
 * 
 * @author Brad Chen
 *
 */
public class FilterRainfallNoiseTransformation implements Calculation {
	@Input
	private float noise;
	
	@Input
	private float threshold;

	@Input
	TimeSeriesArray input = null;

	@Output
	TimeSeriesArray output = null;
	
	@Override
	public void calculate() throws Exception {
		TransformationUtils.filterNoise( this.input, this.output, this.noise );
		TransformationUtils.filterNoise( this.input, this.output, this.threshold );
		TransformationUtils.filterNoiseRepeat( this.input, this.output, this.threshold );
	}
}
