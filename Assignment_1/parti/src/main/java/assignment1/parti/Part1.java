package assignment1.parti;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.util.Progressable;

public class Part1
{
	public static void downloadFiles(File file)
	{
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
			{
				String[] parts = line.split("/", -1);
				String fileName = parts[parts.length - 1];

				System.out.println("Downloading " + fileName);
				URL website = new URL(line);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				FileOutputStream fos = new FileOutputStream(fileName);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void uploadFiles(File file)
	{
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
			{
				String[] parts = line.split("/", -1);
				String fileName = parts[parts.length - 1];

				System.out.println("Copying " + fileName + " to hdfs");
				String localSrc = fileName;
				String dst = "/user/rxr151330/assignment1/" + fileName;

				InputStream in = new BufferedInputStream(new FileInputStream(localSrc));

				Configuration conf = new Configuration();
				conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/core-site.xml"));
				conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/hdfs-site.xml"));

				FileSystem fs = FileSystem.get(URI.create(dst), conf);
				OutputStream out = fs.create(new Path(dst), new Progressable() {

					public void progress() {
						System.out.print(".");
					}
				});

				IOUtils.copyBytes(in, out, 4096, true);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void cleanupLocal(File file)
	{
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
			{	
				String[] parts = line.split("/", -1);
				String fileName = parts[parts.length - 1];

				File fileToDelete = new File(fileName);
				boolean result = Files.deleteIfExists(fileToDelete.toPath());

				if(result)
				{
					System.out.println(fileName + " was successfully deleted");
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void decompress(File file)
	{
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
			{	
				String[] parts = line.split("/", -1);
				String fileName = parts[parts.length - 1];

				String uri = "/user/rxr151330/assignment1/" + fileName;

				Configuration conf = new Configuration();
				conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/core-site.xml"));
				conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/hdfs-site.xml"));

				FileSystem fs = FileSystem.get(URI.create(uri), conf);

				Path inputPath = new Path(uri);
				CompressionCodecFactory factory = new CompressionCodecFactory(conf);
				CompressionCodec codec = factory.getCodec(inputPath);
				if (codec == null) {
					System.err.println("No codec found for " + uri);
					System.exit(1);
				}

				String outputUri =
						CompressionCodecFactory.removeSuffix(uri, codec.getDefaultExtension());

				System.out.println("Decompressing " + fileName);
				InputStream in = null;
				OutputStream out = null;
				try {
					in = codec.createInputStream(fs.open(inputPath));
					out = fs.create(new Path(outputUri));
					IOUtils.copyBytes(in, out, conf);
				} finally {
					IOUtils.closeStream(in);
					IOUtils.closeStream(out);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void cleanupHDFS(File file)
	{
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
			{	
				String[] parts = line.split("/", -1);
				String fileName = parts[parts.length - 1];

				String uri = "/user/rxr151330/assignment1/" + fileName;

				Configuration conf = new Configuration();
				conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/core-site.xml"));
				conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/hdfs-site.xml"));

				FileSystem fs = FileSystem.get(URI.create(uri), conf);
				Path inputPath = new Path(uri);
				
				System.out.println("Deleting " + fileName + " from hdfs");
				fs.delete(inputPath, false);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		File file = new File("urls.txt");

		Part1.downloadFiles(file);
		Part1.uploadFiles(file);
		Part1.cleanupLocal(file);
		Part1.decompress(file);
		Part1.cleanupHDFS(file);
	}
}

