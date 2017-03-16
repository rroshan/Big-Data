package Lab4.Lab4;


import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;



public class Q2
{

	public static class BusinessMap extends Mapper<LongWritable, Text, Text, Text>{

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			//from business
			String delims = "^";
			String[] businessData = StringUtils.split(value.toString(),delims);
			String concat;

			if (businessData.length == 3) {
				if(businessData[1].contains(" NY") && businessData[2].contains("Restaurants"))
				{
					concat = businessData[0] + "|" + businessData[1];
					context.write(new Text(concat), new Text(""));
				}
			}		
		}

		@Override
		protected void setup(Context context)
				throws IOException, InterruptedException {
		}
	}




	// Driver program
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();		// get all args
		if (otherArgs.length != 2) {
			System.err.println("Usage: CountYelpBusiness <in> <out>");
			System.exit(2);
		}

		Job job = Job.getInstance(conf, "CountYelp");
		job.setJarByClass(Q1.class);

		job.setMapperClass(BusinessMap.class);
		//uncomment the following line to add the Combiner
		//job.setCombinerClass(Reduce.class);

		// set output key type 

		job.setOutputKeyClass(Text.class);


		// set output value type
		job.setMapOutputValueClass(Text.class);
		job.setOutputValueClass(Text.class);


		job.setNumReduceTasks(0);
		
		//set the HDFS path of the input data
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		// set the HDFS path for the output 
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		//Wait till job completion
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}



