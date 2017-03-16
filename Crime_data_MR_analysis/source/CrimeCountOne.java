import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CrimeCountOne {

	public static class CsvMapper
	extends Mapper<Object, Text, Text, IntWritable>{

		private final static IntWritable one = new IntWritable(1);
		private Text crimeKey = new Text();
		
		public boolean isNumeric(String s) {  
		    return s.matches("[-+]?\\d*\\.?\\d+");  
		}  

		public void map(Object key, Text value, Context context
				) throws IOException, InterruptedException {
			String[] crimeFields = value.toString().split(",", -1);

			if(!crimeFields[4].isEmpty() && !crimeFields[5].isEmpty() && !crimeFields[7].isEmpty() && isNumeric(crimeFields[4]) && isNumeric(crimeFields[5]))
			{
				String easting = crimeFields[4].substring(0,1);
				String northing = crimeFields[5].substring(0,1);
				String crimeType = crimeFields[7];
				crimeKey.set(easting+"_"+northing+"_"+crimeType);
				context.write(crimeKey, one);
			}
		}
	}

	public static class IntSumReducer
	extends Reducer<Text,IntWritable,Text,IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context
				) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "crime count");
		job.setJarByClass(CrimeCountOne.class);
		job.setMapperClass(CsvMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
