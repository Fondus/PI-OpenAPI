package tw.fondus.fews.openapi.transformation.util;

import nl.wldelft.util.timeseries.TimeSeriesArray;

import java.util.stream.IntStream;

/**
 * The tools of the transformation.
 *
 * @author Brad Chen
 *
 */
//@Slf4j
@SuppressWarnings( "rawtypes" )
public class TransformationUtils {
	public static final int THRESHOLD_LOW = 30;

	private TransformationUtils(){}

	/**
	 * Filter the noise.
	 *
	 * @param input input
	 * @param output output
	 * @param noise noise value
	 */
	public static void filterNoise( TimeSeriesArray input, TimeSeriesArray output, float noise ){
		IntStream.range( 0, output.size() ).forEach( i -> {
			float now = input.getValue( i );
			if ( Float.isNaN( now ) ){
				now = 0.0f;
			}

			if ( now >= noise ){
				int previousIndex = i - 1;
				int nextIndex = i + 1;

				if ( previousIndex < 0 ){
					previousIndex = 0;
				}

				if ( nextIndex > (output.size() - 1) ){
					nextIndex = i;
				}

				float previous = input.getValue( previousIndex );
				float next = input.getValue( nextIndex );

				if ( previousIndex == 0 && next < THRESHOLD_LOW ){
					now = next;
				} else if ( nextIndex == i && previous < THRESHOLD_LOW ){
					now = previous;
				} else if ( (previous < THRESHOLD_LOW && next < THRESHOLD_LOW) || (now - previous) > THRESHOLD_LOW && (now - next) > THRESHOLD_LOW ){
					now = ( previous + next ) / 2;

					//log.info( "Input value: {} exceed Noise: {}, so programs fill value with Linear Interpolation.", now, noise );
				}
			}

			output.setValue( i, now );
		});
	}

	/**
	 * Filter the noise when it repeat.
	 *
	 * @param input input
	 * @param output output
	 * @param noise noise value
	 */
	public static void filterNoiseRepeat( TimeSeriesArray input, TimeSeriesArray output, float noise ){
		int limit = output.size() - 1;
		IntStream.range( 1, limit ).forEach( i -> {
			Float now = input.getValue( i );

			if ( now >= noise ){
				int previousIndex = i - 1;
				int nextIndex = i + 1;

				float previous = input.getValue( previousIndex );
				Float next = input.getValue( nextIndex );

				if ( previous < THRESHOLD_LOW && now.equals( next ) ){
					output.setValue( i, previous );
					output.setValue( nextIndex, previous );
				}
			}
		} );
	}
}
