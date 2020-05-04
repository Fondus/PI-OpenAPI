package tw.fondus.fews.openapi.export.pi.json;

import lombok.extern.slf4j.Slf4j;
import nl.wldelft.util.Properties;
import nl.wldelft.util.PropertiesConsumer;
import nl.wldelft.util.io.LineWriter;
import nl.wldelft.util.io.TextSerializer;
import nl.wldelft.util.timeseries.SimpleTimeSeriesContentHandler;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import nl.wldelft.util.timeseries.TimeSeriesContent;
import nl.wldelft.util.timeseries.TimeSeriesHeader;
import tw.fondus.commons.fews.pi.util.json.PiJSONBuilder;
import tw.fondus.commons.fews.pi.util.timeseries.TimeSeriesUtils;

import java.util.stream.IntStream;

/**
 * The export serializer used to convert the time series arrays into string of PI-JSON time series arrays.
 *
 * @author Brad Chen
 *
 */
@Slf4j
public class PiJsonSeriesSerializer implements TextSerializer<TimeSeriesContent>, PropertiesConsumer {
	private int timeZero;

	@Override
	public void serialize( TimeSeriesContent timeSeriesContent, LineWriter lineWriter, String s )
			throws Exception {
		if ( timeSeriesContent.getTimeSeriesCount() <= 0 ) {
			log.error( "PiJsonSeriesSerializer: No TimeSeries can be Written." );
		}

		SimpleTimeSeriesContentHandler handler = new SimpleTimeSeriesContentHandler();
		IntStream.range( 0, timeSeriesContent.getTimeSeriesCount() ).forEach( i -> {
			timeSeriesContent.setTimeSeriesIndex( i );

			// Header
			TimeSeriesHeader header = timeSeriesContent.getTimeSeriesHeader();
			handler.setNewTimeSeriesHeader( header );

			// Time Value
			IntStream.range( 0, timeSeriesContent.getContentTimeCount() )
					.forEach( timeIndex -> {
				timeSeriesContent.setContentTimeIndex( timeIndex );

				if ( timeSeriesContent.isTimeAvailable() ){
					long time = timeSeriesContent.getTime();
					float value = timeSeriesContent.getValue();

					TimeSeriesUtils.addPiTimeSeriesValue( handler, time, value );
				}
			} );
		} );

		TimeSeriesArrays timeSeriesArrays = handler.getTimeSeriesArrays();
		lineWriter.writeLine( PiJSONBuilder.toTimeSeriesJSON( timeSeriesArrays, this.timeZero ).toString() );
	}

	@Override
	public void setProperties( Properties properties ) {
		this.timeZero = properties.getInt( "TimeZeroIndex", 0 );
		log.debug( "PiJsonSeriesSerializer: The time zero index is {}.", this.timeZero );
	}
}
