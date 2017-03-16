package Assignment1b.PositiveNegativeWords;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class PositiveNegativeWords
{
	public static class PositiveNegativeWordsMapper extends Mapper<Object, Text, Text, IntWritable>
	{
		private final static IntWritable one = new IntWritable(1);

		private Set<String> goodWords = new HashSet<String>();
		private Set<String> badWords = new HashSet<String>();

		private Configuration conf;

		public final static Text POSITIVE = new Text("POSITIVE");
		public final static Text NEGATIVE = new Text("NEGATIVE");

		@Override
		public void setup(Context context) throws IOException, InterruptedException
		{
			//not done using file cache...Getting error for that. This method is inefficient.
			conf = context.getConfiguration();
			
			String goodWordsFilePath = conf.get("goodWordsFile");
			parseGoodWordsFile(goodWordsFilePath);
			
			
			String badWordsFilePath = conf.get("badWordsFile");
			parseBadWordsFile(badWordsFilePath);
			
		}

		private void parseGoodWordsFile(String fileUri)
		{
			BufferedReader br;

			try
			{
				FileSystem fs = FileSystem.get(URI.create(fileUri), conf);
				Path path = new Path(fileUri);
				
				br = new BufferedReader(new InputStreamReader(fs.open(path)));
				String pattern = null;
				while ((pattern = br.readLine()) != null)
				{
					if(pattern.matches("^[a-z].*$"))
					{
						goodWords.add(pattern);
					}
				}
			}
			catch (IOException ioe)
			{
				System.err.println("Caught exception while parsing the file '" + StringUtils.stringifyException(ioe));
			}
		}

		private void parseBadWordsFile(String fileUri)
		{
			BufferedReader br;

			try
			{
				FileSystem fs = FileSystem.get(URI.create(fileUri), conf);
				Path path = new Path(fileUri);
				
				br = new BufferedReader(new InputStreamReader(fs.open(path)));
				String pattern = null;
				while ((pattern = br.readLine()) != null)
				{
					if(pattern.matches("^[a-z].*$"))
					{
						badWords.add(pattern);
					}
				}
			}
			catch (IOException ioe)
			{
				System.err.println("Caught exception while parsing the file '" + StringUtils.stringifyException(ioe));
			}
		}

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException
		{
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line);

			while (itr.hasMoreTokens())
			{
				String token = itr.nextToken();

				if(goodWords.contains(token))
				{
					context.write(POSITIVE, one);
				}
				else if(badWords.contains(token))
				{
					context.write(NEGATIVE, one);
				}
			}
		}
	}

	public static class PositiveNegativeWordsReducer extends Reducer<Text,IntWritable,Text,IntWritable>
	{
		private IntWritable result = new IntWritable();

		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
		{
			int sum = 0;
			
			for (IntWritable val : values)
			{
				sum = sum + val.get();
			}
			
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();

		GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
		String[] remainingArgs = optionParser.getRemainingArgs();
		if (remainingArgs.length != 6)
		{
			System.err.println("Usage: PositiveNegativeWords <in> <out> -good goodWordsFile -bad badWordsFile");
			System.exit(2);
		}
		
		List<String> otherArgs = new ArrayList<String>();

		for (int i = 0; i < remainingArgs.length; i++)
		{
			if("-good".equals(remainingArgs[i]))
			{
				conf.set("goodWordsFile", remainingArgs[++i]);
			}
			else if("-bad".equals(remainingArgs[i]))
			{
				conf.set("badWordsFile", remainingArgs[++i]);
			}
			else
			{
				otherArgs.add(remainingArgs[i]);
			}
		}

		Job job = Job.getInstance(conf, "PositiveNegativeWords");
		job.setJarByClass(PositiveNegativeWords.class);
		job.setMapperClass(PositiveNegativeWordsMapper.class);
		job.setCombinerClass(PositiveNegativeWordsReducer.class);
		job.setReducerClass(PositiveNegativeWordsReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(otherArgs.get(0)));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs.get(1)));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
