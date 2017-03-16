package Assignment1b.PartOfSpeech;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class POSOutputFormat extends FileOutputFormat<IntWritable, POSWritable>
{
	@Override
	public RecordWriter<IntWritable, POSWritable> getRecordWriter(TaskAttemptContext arg0) throws IOException, InterruptedException
	{
		Path path = FileOutputFormat.getOutputPath(arg0);
		Path fullPath = new Path(path, "result.txt");
		FileSystem fs = path.getFileSystem(arg0.getConfiguration());
		FSDataOutputStream fileOut = fs.create(fullPath, arg0);
		
		return new MyCustomPOSWriter(fileOut);
	}

	public class MyCustomPOSWriter extends RecordWriter<IntWritable, POSWritable>
	{
		private DataOutputStream out;
		
		public MyCustomPOSWriter(DataOutputStream stream)
		{
	        out = stream;
		}
		
		@Override
		public void close(TaskAttemptContext arg0) throws IOException, InterruptedException
		{
			out.close();
		}

		@Override
		public void write(IntWritable arg0, POSWritable arg1) throws IOException, InterruptedException
		{
			out.writeBytes("Length:" + arg0.toString() + "\n");
			
			out.writeBytes("Count of Words: " + arg1.getCountOfWords() + "\n");
			
			out.writeBytes("Distribution of POS: {");
			
			out.writeBytes("");
			if(arg1.getNounCount().get() > 0)
				out.writeBytes("noun: " + arg1.getNounCount() + "; ");
			if(arg1.getPluralCount().get() > 0)
				out.writeBytes("plural: " + arg1.getPluralCount() + "; ");
			if(arg1.getNounPhraseCount().get() > 0)
				out.writeBytes("noun_phrase: " + arg1.getNounPhraseCount() + "; ");
			if(arg1.getVerbParticipleCount().get() > 0)
				out.writeBytes("verb_participle: " + arg1.getVerbParticipleCount() + "; ");
			if(arg1.getVerbTransitiveCount().get() > 0)
				out.writeBytes("verb_transitive: " + arg1.getVerbTransitiveCount() + "; ");
			if(arg1.getVerbIntransitiveCount().get() > 0)
				out.writeBytes("verb_intransitive: " + arg1.getVerbIntransitiveCount() + "; ");
			if(arg1.getAdjectiveCount().get() > 0)
				out.writeBytes("adjective: " + arg1.getAdjectiveCount() + "; ");
			if(arg1.getAdverbCount().get() > 0)
				out.writeBytes("adverb: " + arg1.getAdverbCount() + "; ");
			if(arg1.getConjunctionCount().get() > 0)
				out.writeBytes("conjunction: " + arg1.getConjunctionCount() + "; ");
			if(arg1.getPrepositionCount().get() > 0)
				out.writeBytes("preposition: " + arg1.getPrepositionCount() + "; ");
			if(arg1.getInterjectionCount().get() > 0)
				out.writeBytes("interjection: " + arg1.getInterjectionCount() + "; ");
			if(arg1.getPronounCount().get() > 0)
				out.writeBytes("pronoun: " + arg1.getPronounCount() + "; ");
			if(arg1.getDefiniteArticle().get() > 0)
				out.writeBytes("definite_article: " + arg1.getDefiniteArticle() + "; ");
			if(arg1.getIndefiniteArticle().get() > 0)
				out.writeBytes("indefinite_article: " + arg1.getIndefiniteArticle() + "; ");
			if(arg1.getNominativeCount().get() > 0)
				out.writeBytes("nominative: " + arg1.getNominativeCount());
			
			out.writeBytes("}\n");
			
			out.writeBytes("Number of Palindromes: " + arg1.getPalindromeCount() + "\n");
			out.writeBytes("\r\n");
		}
		
	}
}
