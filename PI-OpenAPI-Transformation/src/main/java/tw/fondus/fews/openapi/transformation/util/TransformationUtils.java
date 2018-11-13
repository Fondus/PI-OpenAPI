package tw.fondus.fews.openapi.transformation.util;

import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.wldelft.util.timeseries.TimeSeriesArray;

/**
 * The tools of the transformation.
 * 
 * @author Brad Chen
 *
 */
public class TransformationUtils {
	private static final Logger log = LoggerFactory.getLogger( TransformationUtils.class );
	public static final int THRESHOLD_LOW = 30;
	
	/**
	 * Filter the noise.
	 * 
	 * @param input
	 * @param output
	 * @param noise
	 */
	public static void filterNoise( TimeSeriesArray input, TimeSeriesArray output, float noise ){
		IntStream.range( 0, output.size() ).forEach( i -> {
			Float now = input.getValue( i );
			if ( Float.isNaN( now ) ){
				now = 0.0f;
			}
			
			if ( now >= noise ){
				int periousIndex = i - 1;
				int nextIndex = i + 1;
				
				if ( periousIndex < 0 ){
					periousIndex = 0;
				}
				
				if ( nextIndex > (output.size() - 1) ){
					nextIndex = i;
				}
				
				Float perious = input.getValue( periousIndex );
				Float next = input.getValue( nextIndex );
				
				if ( periousIndex == 0 && next < THRESHOLD_LOW ){
					now = next;
				} else if ( nextIndex == i && perious < THRESHOLD_LOW ){
					now = perious;
				} else if ( (perious < THRESHOLD_LOW && next < THRESHOLD_LOW) || (now - perious) > THRESHOLD_LOW && (now - next) > THRESHOLD_LOW ){
					now = ( perious + next ) / 2;
					
					log.info( "Input value: {} execeed Noise: {}, so programs fill value with Linear Interpolation.", now, noise );
				} 
			}
			
			output.setValue( i, now);
		});
	}
	
	/**
	 * Filter the noise when it repeat.
	 * 
	 * @param input
	 * @param output
	 * @param noise
	 */
	public static void filterNoiseRepeat( TimeSeriesArray input, TimeSeriesArray output, float noise ){
		int limit = output.size() - 1;
		IntStream.range( 1, limit ).forEach( i -> {
			Float now = input.getValue( i );
			
			if ( now >= noise ){
				int periousIndex = i - 1;
				int nextIndex = i + 1;
				
				Float perious = input.getValue( periousIndex );
				Float next = input.getValue( nextIndex );
				
				if ( perious < THRESHOLD_LOW && now.equals( next ) ){
					output.setValue( i, perious );
					output.setValue( nextIndex, perious );
				}
			}
		} );
	}
}
