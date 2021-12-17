package tw.fondus.fews.openapi.importer.manysplendid;

import lombok.Builder;
import lombok.Data;
import nl.wldelft.util.Properties;
import nl.wldelft.util.PropertiesConsumer;
import nl.wldelft.util.coverage.PointsGeometry;
import nl.wldelft.util.geodatum.Wgs1984Point;
import nl.wldelft.util.io.LineReader;
import nl.wldelft.util.io.TextParser;
import nl.wldelft.util.timeseries.DefaultTimeSeriesHeader;
import nl.wldelft.util.timeseries.TimeSeriesContentHandler;
import org.joda.time.DateTime;
import strman.Strman;
import tw.fondus.commons.util.string.StringFormatter;
import tw.fondus.commons.util.string.Strings;
import tw.fondus.commons.util.time.JodaTimeUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The import parser of manysplendid company standard pc-raster WGS1984 forecast text grid.
 *
 * @author Brad Chen
 *
 */
//@Slf4j
public class PCRasterWgs1984ForecastTextGridParser implements TextParser<TimeSeriesContentHandler>, PropertiesConsumer {
	private String forecastName;
	private String timeFormat;

	@Override
	public void setProperties( Properties properties ) {
		this.forecastName = properties.getString( "forecastName", "" );
		this.timeFormat = properties.getString( "timeFormat", "yyyyMMddHH" );
	}

	@Override
	public void parse( LineReader reader, String virtualFileName, TimeSeriesContentHandler handler )
			throws Exception {
		reader.setSkipEmptyLines(true);
		//log.info( "PCRasterTextGridParser: Try to parse grid data with file: {}.", virtualFileName );
		List<String> lines = reader.lines().collect( Collectors.toList() );
		if ( lines.size() == 0 ) {
			throw new IllegalStateException( "PCRasterTextGridParser: File content is empty." );
		} else if ( lines.size() < 6 ){
			throw new IllegalStateException( "PCRasterTextGridParser: File content not contain header." );
		} else {
			DefaultTimeSeriesHeader header = new DefaultTimeSeriesHeader();

			// Parse Header
			this.parseHeaderTimes( handler, header, lines );
			this.parseHeaderParameter( header, lines );

			// Parse Grid
			List<GeometryPoint> data = this.parseGridGeometryData( lines );
			if ( data.isEmpty() ){
				//log.warn( "PCRasterTextGridParser: File grid content is empty." );
			} else {
				//log.info( "PCRasterTextGridParser: Success to parse grid data with {} points, try to mapping with FEWS data model.", data.size() );

				Wgs1984Point[] points = new Wgs1984Point[data.size()];
				float[] gridValues = new float[data.size()];
				for ( int i = 0; i < data.size(); i++ ){
					points[i] = data.get( i ).geometry;
					gridValues[i] = data.get( i ).value;
				}

				handler.setTimeSeriesHeader( header );
				handler.setGeometry( new PointsGeometry( points ) );
				handler.setCoverageValues( gridValues );
				handler.applyCurrentFields();
			}
		}
	}

	/**
	 * Parse grid data from file.
	 *
	 * @param lines file lines
	 * @return grid data
	 */
	private List<GeometryPoint> parseGridGeometryData( List<String> lines ){
		return lines.stream()
				.skip( 5 )
				.map( line -> line.split( Strings.SPLIT_SPACE_MULTIPLE ) )
				.map( temps -> GeometryPoint.builder()
						.geometry( new Wgs1984Point( Double.parseDouble( temps[1] ), Double.parseDouble( temps[0] ) ) )
						.value( Float.parseFloat( temps[2] ) )
						.build() )
				.collect( Collectors.toList() );
	}

	/**
	 * Parse parameter information from file header.
	 *
	 * @param header header
	 * @param lines file lines
	 */
	private void parseHeaderParameter( DefaultTimeSeriesHeader header, List<String> lines ){
		String line = lines.get( 4 );
		String parameter = line.substring( 0, Strman.lastIndexOf( line, "(" ) );
		String unit = Strman.between( line, "(", "\\)" )[0];
		header.setParameterId( parameter );
		header.setUnit( unit );
		//log.info( "PCRasterTextGridParser: Header Information Parameter Id: {}, Unit: {}.", parameter, unit );
	}

	/**
	 * Parse times information from file header.
	 *
	 * @param handler timeseries content header.
	 * @param header header
	 * @param lines file lines
	 */
	private void parseHeaderTimes( TimeSeriesContentHandler handler,
			DefaultTimeSeriesHeader header, List<String> lines ){
		String line = lines.get( 0 );
		if ( line.startsWith( this.forecastName ) ){
			String[] times = line.substring( this.forecastName.length() )
					.split( Strings.SPLIT_SPACE )[0]
					.split( "預報" );

			DateTime currentTime = JodaTimeUtils.toDateTime( times[1], this.timeFormat, JodaTimeUtils.UTC8 );
			DateTime forecastTime = JodaTimeUtils.toDateTime( times[0], this.timeFormat, JodaTimeUtils.UTC8 );

			//log.info( "PCRasterTextGridParser: Header Information Forecast Time: {}, Current Time: {}.", forecastTime, currentTime );

			handler.setTime( currentTime.getMillis() );
			header.setForecastTime( forecastTime.getMillis() );
		} else {
			//log.error( "PCRasterTextGridParser: File forecast name not equals {}.", this.forecastName );
			throw new IllegalStateException( StringFormatter.format( "PCRasterTextGridParser: File forecast name not equals {}.", this.forecastName ) );
		}
	}

	@Data
	@Builder
	private static class GeometryPoint {
		private Wgs1984Point geometry;
		private float value;
	}
}
