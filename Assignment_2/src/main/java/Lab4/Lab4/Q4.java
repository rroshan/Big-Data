package Lab4.Lab4;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.*;


public class Q4 {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: TopN <in> <out>");
			System.exit(2);
		}
		Job job = Job.getInstance(conf);
		job.setJobName("Top N");
		job.setJarByClass(Q4.class);
		job.setMapperClass(TopNMapper.class);
		//job.setCombinerClass(TopNReducer.class);
		job.setReducerClass(TopNReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(MeanPairWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	/**
	 * The mapper reads one line at the time, splits it into an array of single words and emits every
	 * word to the reducers with the value of 1.
	 */
	public static class TopNMapper extends Mapper<Object, Text, Text, MeanPairWritable> {

		HashMap<String, MeanPairWritable> map = new HashMap<String, MeanPairWritable>();
		
		//im-mapper combining design pattern
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException
		{
			//from business
			String delims = "^";
			String[] reviewData = StringUtils.split(value.toString(),delims);

			if (reviewData.length == 4)
			{
				if(map.containsKey(reviewData[2]))
				{
					MeanPairWritable meanPairWritable = map.get(reviewData[2]);
					
					float sum = meanPairWritable.getSum().get();
					int count = meanPairWritable.getCount().get();
					
					sum = sum + Float.parseFloat(reviewData[3]);
					count++;
					
					meanPairWritable.set(sum, count);
					
					map.put(reviewData[2], meanPairWritable);
				}
				else
				{
					MeanPairWritable meanPairWritable = new MeanPairWritable();
					meanPairWritable.set(Float.parseFloat(reviewData[3]), 1);
					map.put(reviewData[2], meanPairWritable);
				}
			}
		}
		
		protected void cleanup(Context context) throws IOException, InterruptedException
		{
		    Iterator it = map.entrySet().iterator();
		    while (it.hasNext())
		    {
		        Map.Entry pair = (Map.Entry)it.next();
		        String key = (String) pair.getKey();
		        MeanPairWritable mpw = (MeanPairWritable) pair.getValue();
		        
		        context.write(new Text(key), mpw);
		    }
		}
	}

	/**
	 * The reducer retrieves every word and puts it into a Map: if the word already exists in the
	 * map, increments its value, otherwise sets it to 1.
	 */
	public static class TopNReducer extends Reducer<Text, MeanPairWritable, Text, FloatWritable> {

		private Map<Text, FloatWritable> countMap = new HashMap<Text, FloatWritable>();

		@Override
		public void reduce(Text key, Iterable<MeanPairWritable> values, Context context) throws IOException, InterruptedException {

			// computes the number of occurrences of a single word
			float sum = 0;
			int count = 0;
			
			for (MeanPairWritable mpw : values)
			{
				sum = sum + mpw.getSum().get();
				count = count + mpw.getCount().get();
			}

			float avg = sum / count;
			// puts the number of occurrences of this word into the map.
			// We need to create another Text object because the Text instance
			// we receive is the same for all the words
			countMap.put(new Text(key), new FloatWritable(avg));
		}

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {

			Map<Text, FloatWritable> sortedMap = sortByValues(countMap);

			int counter = 0;
			for (Text key : sortedMap.keySet()) {
				if (counter++ == 10) {
					break;
				}
				context.write(key, sortedMap.get(key));
			}
		}
	}

	/*
	 * sorts the map by values. Taken from:
	 * http://javarevisited.blogspot.it/2012/12/how-to-sort-hashmap-java-by-key-and-value.html
	 */
	private static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

}