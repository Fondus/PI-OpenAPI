package tw.fondus.fews.openapi.importer.manysplendid;

import nl.wldelft.fews.pi.PiTimeSeriesSerializer;
import nl.wldelft.fews.pi.PiVersion;
import nl.wldelft.util.Floats;
import nl.wldelft.util.Properties;
import nl.wldelft.util.coverage.Coverage;
import nl.wldelft.util.coverage.Geometry;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import nl.wldelft.util.timeseries.TimeSeriesUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tw.fondus.commons.util.file.PathUtils;
import tw.fondus.commons.util.file.io.PathReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

/**
 * The unit test of PCRasterWgs1984ForecastTextGridParser.
 *
 * @author Brad Chen
 *
 */
@SuppressWarnings( { "rawtypes" } )
public class PCRasterWgs1984ForecastTextGridParserTest {
	@Test
	public void test() throws IOException {
		Path path = Paths.get( "src/test/resources/manysplendid/pcraster_forecast_textgrid_0000.001" );
		Assertions.assertTrue( PathUtils.isExists( path ) );

		Properties properties = new Properties.Builder()
				.addString( "forecastName", "氣象局RWRF模式降雨預報：" )
				.build();

		PCRasterWgs1984ForecastTextGridParser parser = new PCRasterWgs1984ForecastTextGridParser();
		parser.setProperties( properties );

		TimeSeriesArrays timeSeriesArrays = TimeSeriesUtils.read( path.toFile(), parser, TimeSeriesArray.Type.COVERAGE );
		TimeSeriesArrays summary = TimeSeriesUtils.summarizeCoverageArrays( timeSeriesArrays );

		String actual = summary.toString( new PiTimeSeriesSerializer( PiVersion.getLatestVersion() ) );
		String expected = PathReader.readString( "src/test/resources/manysplendid/pcraster_forecast_textgrid_0000.001.xml" );
		Assertions.assertEquals( expected, actual );
	}

	private void debug( TimeSeriesArrays timeSeriesArrays ){
		TimeSeriesArray array = timeSeriesArrays.get( 0 );
		Coverage coverage = array.getCoverage( 0 );
		Geometry geometry = coverage.getGeometry();
		Floats values = coverage.getValues();
		values.setInstantCompressionEnabled( false );
		System.out.println( values.size() );
		System.out.println( geometry.size() );

		IntStream.range( 0, geometry.size() ).forEach( i -> {
			System.out.println( "X: " + geometry.getX( i ) + " Y: " + geometry.getY( i ) + " V: " + values.getValue( i ) );
		} );
	}
}
