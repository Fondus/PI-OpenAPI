package tw.fondus.fews.openapi.export.pi.json;

import nl.wldelft.util.FileUtils;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tw.fondus.commons.fews.pi.util.timeseries.TimeSeriesUtils;
import tw.fondus.commons.json.util.JSONUtils;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The unit test of PiJsonSeriesSerializer.
 *
 * @author Brad Chen
 *
 */
public class PiJsonSeriesSerializerTest {
	private Path path;

	@Before
	public void setUp(){
		this.path = Paths.get( "src/test/resources/Input.xml" );
		Assert.assertTrue( Files.exists( this.path ) );
	}

	@Test
	public void test() throws IOException, OperationNotSupportedException {
		TimeSeriesArrays timeSeriesArrays = TimeSeriesUtils.readPiTimeSeries( this.path );
		File outputFile = Paths.get( "src/test/resources/Output.json" ).toFile();

		timeSeriesArrays.write( outputFile, new PiJsonSeriesSerializer() );

		Assert.assertTrue( Files.exists( outputFile.toPath() ) );
		Assert.assertTrue( JSONUtils.isJSONValid( FileUtils.readText( outputFile ) ) );
	}
}
