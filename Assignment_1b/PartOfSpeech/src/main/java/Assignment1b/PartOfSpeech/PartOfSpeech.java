package Assignment1b.PartOfSpeech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.StringUtils;


public class PartOfSpeech
{
	public static class PartOfSpeechMapper extends Mapper<Object, Text, IntWritable, Text>
	{
		private Configuration conf;

		HashMap<String, HashSet<Character>> posMap = new HashMap<String, HashSet<Character>>();

		public final static Text NOUN = new Text("noun");
		public final static Text PLURAL = new Text("plural");
		public final static Text NOUN_PHRASE = new Text("noun_phrase");
		public final static Text VERB_PARTICIPLE = new Text("verb_participle");
		public final static Text VERB_TRANSITIVE = new Text("verb_transitive");
		public final static Text VERB_INTRANSITIVE = new Text("verb_intransitive");
		public final static Text ADJECTIVE = new Text("adjective");
		public final static Text ADVERB = new Text("adverb");
		public final static Text CONJUNCTION = new Text("conjunction");
		public final static Text PREPOSITION = new Text("preposition");
		public final static Text INTERJECTION = new Text("interjection");
		public final static Text PRONOUN = new Text("pronoun");
		public final static Text DEFINITE_ARTICLE = new Text("definite_article");
		public final static Text INDEFINITE_ARTICLE = new Text("indefinite_article");
		public final static Text NOMINATIVE = new Text("nominative");
		public final static Text PALINDROME = new Text("palindrome");

		@Override
		public void setup(Context context) throws IOException, InterruptedException
		{
			//not done using file cache...Getting error for that. This method is inefficient.
			conf = context.getConfiguration();

			String posFilePath = conf.get("posfile");
			parsePosFile(posFilePath);
		}

		private void parsePosFile(String fileUri)
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
					String[] parts = pattern.split("\\|");

					String key = parts[0].toLowerCase().trim().replaceAll(" +", " ");
					
					if(!posMap.containsKey(key))
					{
						posMap.put(key, new HashSet<Character>());
					}

					HashSet<Character> set = posMap.get(key);

					for(int i = 0; i < parts[1].length(); i++)
					{
						char c = parts[1].charAt(i);
						set.add(c);
					}
				}
			}
			catch (IOException ioe)
			{
				System.err.println("Caught exception while parsing the file '" + StringUtils.stringifyException(ioe));
			}
		}
		
		public boolean isPalindrome(String token)
		{
			token = token.toLowerCase().replaceAll("[^a-z0-9]", "");
			return new StringBuilder(token).reverse().toString().equals(token);
		}

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException
		{
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line);

			while (itr.hasMoreTokens())
			{
				String token = itr.nextToken();
				
				token = token.toLowerCase().trim().replaceAll(" +", " ");

				int length = token.length();

				if(length >= 5)
				{
					HashSet<Character> set = posMap.get(token);

					if(set != null)
					{
						for(char c : set)
						{
							switch(c)
							{
							case 'N':
								context.write(new IntWritable(length), NOUN);
								break;

							case 'p':
								context.write(new IntWritable(length), PLURAL);
								break;

							case 'h':
								context.write(new IntWritable(length), NOUN_PHRASE);
								break;

							case 'V':
								context.write(new IntWritable(length), VERB_PARTICIPLE);
								break;

							case 't':
								context.write(new IntWritable(length), VERB_TRANSITIVE);
								break;

							case 'i':
								context.write(new IntWritable(length), VERB_INTRANSITIVE);
								break;

							case 'A':
								context.write(new IntWritable(length), ADJECTIVE);
								break;

							case 'v':
								context.write(new IntWritable(length), ADVERB);
								break;

							case 'C':
								context.write(new IntWritable(length), CONJUNCTION);
								break;

							case 'P':
								context.write(new IntWritable(length), PREPOSITION);
								break;

							case '!':
								context.write(new IntWritable(length), INTERJECTION);
								break;

							case 'r':
								context.write(new IntWritable(length), PRONOUN);
								break;

							case 'D':
								context.write(new IntWritable(length), DEFINITE_ARTICLE);
								break;

							case 'I':
								context.write(new IntWritable(length), INDEFINITE_ARTICLE);
								break;

							case 'o':
								context.write(new IntWritable(length), NOMINATIVE);
								break;
							}
						}
						
						//check if palindrome
						if(isPalindrome(token))
						{
							context.write(new IntWritable(length), PALINDROME);
						}
					}
				}
			}
		}
	}
	
	public static class PartOfSpeechReducer extends Reducer<IntWritable, Text, IntWritable, POSWritable>
	{
		public final static Text NOUN = new Text("noun");
		public final static Text PLURAL = new Text("plural");
		public final static Text NOUN_PHRASE = new Text("noun_phrase");
		public final static Text VERB_PARTICIPLE = new Text("verb_participle");
		public final static Text VERB_TRANSITIVE = new Text("verb_transitive");
		public final static Text VERB_INTRANSITIVE = new Text("verb_intransitive");
		public final static Text ADJECTIVE = new Text("adjective");
		public final static Text ADVERB = new Text("adverb");
		public final static Text CONJUNCTION = new Text("conjunction");
		public final static Text PREPOSITION = new Text("preposition");
		public final static Text INTERJECTION = new Text("interjection");
		public final static Text PRONOUN = new Text("pronoun");
		public final static Text DEFINITE_ARTICLE = new Text("definite_article");
		public final static Text INDEFINITE_ARTICLE = new Text("indefinite_article");
		public final static Text NOMINATIVE = new Text("nominative");
		public final static Text PALINDROME = new Text("palindrome");
		
		@Override
		public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{
			int countOfWords = 0;
			int nounCount = 0;
			int pluralCount = 0;
			int nounPhraseCount = 0;
			int verbParticipleCount = 0;
			int verbTransitiveCount = 0;
			int verbIntransitiveCount = 0;
			int adjectiveCount = 0;
			int adverbCount = 0;
			int conjunctionCount = 0;
			int prepositionCount = 0;
			int interjectionCount = 0;
			int pronounCount = 0;
			int definiteArticle = 0;
			int indefiniteArticle = 0;
			int nominativeCount = 0;
			int palindromeCount = 0;
			
			for (Text val : values)
			{
				if(val.equals(NOUN)) {
					nounCount++;
				} else if(val.equals(PLURAL)) {
					pluralCount++;
				} else if(val.equals(NOUN_PHRASE)) {
					nounPhraseCount++;
				} else if(val.equals(VERB_PARTICIPLE)) {
					verbParticipleCount++;
				} else if(val.equals(VERB_TRANSITIVE)) {
					verbTransitiveCount++;
				} else if(val.equals(VERB_INTRANSITIVE)) {
					verbIntransitiveCount++;
				} else if(val.equals(ADJECTIVE)) {
					adjectiveCount++;
				} else if(val.equals(ADVERB)) {
					adverbCount++;
				} else if(val.equals(CONJUNCTION)) {
					conjunctionCount++;
				} else if(val.equals(PREPOSITION)) {
					prepositionCount++;
				} else if(val.equals(INTERJECTION)) {
					interjectionCount++;
				} else if(val.equals(PRONOUN)) {
					pronounCount++;
				} else if(val.equals(DEFINITE_ARTICLE)) {
					definiteArticle++;
				} else if(val.equals(INDEFINITE_ARTICLE)) {
					indefiniteArticle++;
				} else if(val.equals(NOMINATIVE)) {
					nominativeCount++;
				} else if(val.equals(PALINDROME)) {
					palindromeCount++;
				}
				
				countOfWords++;
			}
			
			POSWritable posWritable = new POSWritable();
			posWritable.set(countOfWords, nounCount, pluralCount, nounPhraseCount, verbParticipleCount, verbTransitiveCount, verbIntransitiveCount, adjectiveCount, adverbCount, conjunctionCount, prepositionCount, interjectionCount, pronounCount, definiteArticle, indefiniteArticle, nominativeCount, palindromeCount);
			context.write(key, posWritable);
		}
	}

	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();

		GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
		String[] remainingArgs = optionParser.getRemainingArgs();
		if (remainingArgs.length != 4)
		{
			System.err.println("Usage: PartOfSpeech <in> <out> -pos posfile");
			System.exit(2);
		}

		List<String> otherArgs = new ArrayList<String>();

		for (int i = 0; i < remainingArgs.length; i++)
		{
			if("-pos".equals(remainingArgs[i]))
			{
				conf.set("posfile", remainingArgs[++i]);
			}
			else
			{
				otherArgs.add(remainingArgs[i]);
			}
		}

		Job job = Job.getInstance(conf, "PartOfSpeech");
		job.setJarByClass(PartOfSpeech.class);
		job.setMapperClass(PartOfSpeechMapper.class);
		job.setReducerClass(PartOfSpeechReducer.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(POSWritable.class);
		
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(otherArgs.get(0)));
		
		job.setOutputFormatClass(POSOutputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(otherArgs.get(1)));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
