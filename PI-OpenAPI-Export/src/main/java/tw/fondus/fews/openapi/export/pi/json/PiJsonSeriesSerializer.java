package tw.fondus.fews.openapi.export.pi.json;

import nl.wldelft.util.Properties;
import nl.wldelft.util.PropertiesConsumer;
import nl.wldelft.util.io.LineWriter;
import nl.wldelft.util.io.TextSerializer;
import nl.wldelft.util.timeseries.SimpleTimeSeriesContentHandler;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import nl.wldelft.util.timeseries.TimeSeriesContent;
import nl.wldelft.util.timeseries.TimeSeriesHeader;
import tw.fondus.commons.fews.pi.util.timeseries.TimeSeriesUtils;
import tw.fondus.commons.json.util.gson.GsonMapperRuntime;
import tw.fondus.commons.rest.pi.json.model.timeseries.PiTimeSeriesCollection;
import tw.fondus.commons.rest.pi.json.util.timeseries.PiSeriesMapper;
import tw.fondus.commons.util.math.NumberUtils;

import java.util.stream.IntStream;

/**
 * The export serializer used to convert the time series arrays into string of PI-JSON time series arrays.
 *
 * @author Brad Chen
 *
 */
//@Slf4j
@SuppressWarnings( "rawtypes" )
public class PiJsonSeriesSerializer implements TextSerializer<TimeSeriesContent>, PropertiesConsumer {
	private int timeZero;

	@Override
	public void serialize( TimeSeriesContent timeSeriesContent, LineWriter lineWriter, String s )
			throws Exception {
		if ( timeSeriesContent.getTimeSeriesCount() <= 0 ) {
			//log.error( "PiJsonSeriesSerializer: No TimeSeries can be Written." );
		}

		SimpleTimeSeriesContentHandler handler = TimeSeriesUtils.seriesHandler();
		IntStream.range( 0, timeSeriesContent.getTimeSeriesCount() ).forEach( i -> {
			timeSeriesContent.setTimeSeriesIndex( i );

			// Header
			TimeSeriesHeader header = timeSeriesContent.getTimeSeriesHeader();
			TimeSeriesUtils.addHeader( handler, header );

			// Time Value
			IntStream.range( 0, timeSeriesContent.getContentTimeCount() )
					.forEach( timeIndex -> {
				timeSeriesContent.setContentTimeIndex( timeIndex );

				if ( timeSeriesContent.isTimeAvailable() ){
					long time = timeSeriesContent.getTime();
					TimeSeriesUtils.addValue( handler, time, NumberUtils.create( timeSeriesContent.getValue() ) );
				}
			} );
		} );

		TimeSeriesArrays timeSeriesArrays = handler.getTimeSeriesArrays();
		PiTimeSeriesCollection collection = PiSeriesMapper.toPiTimeSeriesCollection( timeSeriesArrays, this.timeZero );
		lineWriter.writeLine( GsonMapperRuntime.ISO8601.toString( collection ) );
	}

	@Override
	public void setProperties( Properties properties ) {
		this.timeZero = properties.getInt( "TimeZeroIndex", 0 );
		//log.debug( "PiJsonSeriesSerializer: The time zero index is {}.", this.timeZero );
	}
}
