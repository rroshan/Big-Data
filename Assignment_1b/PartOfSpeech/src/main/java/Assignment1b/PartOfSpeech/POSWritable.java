package Assignment1b.PartOfSpeech;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

public class POSWritable implements WritableComparable<POSWritable>
{
	IntWritable countOfWords;
	IntWritable nounCount;
	IntWritable pluralCount;
	IntWritable nounPhraseCount;
	IntWritable verbParticipleCount;
	IntWritable verbTransitiveCount;
	IntWritable verbIntransitiveCount;
	IntWritable adjectiveCount;
	IntWritable adverbCount;
	IntWritable conjunctionCount;
	IntWritable prepositionCount;
	IntWritable interjectionCount;
	IntWritable pronounCount;
	IntWritable definiteArticle;
	IntWritable indefiniteArticle;
	IntWritable nominativeCount;
	IntWritable palindromeCount;

	public POSWritable()
	{
		countOfWords = new IntWritable(0);

		nounCount = new IntWritable(0);
		pluralCount = new IntWritable(0);
		nounPhraseCount = new IntWritable(0);
		verbParticipleCount = new IntWritable(0);
		verbTransitiveCount = new IntWritable(0);
		verbIntransitiveCount = new IntWritable(0);
		adjectiveCount = new IntWritable(0);
		adverbCount = new IntWritable(0);
		conjunctionCount = new IntWritable(0);
		prepositionCount = new IntWritable(0);
		interjectionCount = new IntWritable(0);
		pronounCount = new IntWritable(0);
		definiteArticle = new IntWritable(0);
		indefiniteArticle = new IntWritable(0);
		nominativeCount = new IntWritable(0);
		palindromeCount = new IntWritable(0);
	}

	public POSWritable(IntWritable countOfWords,
			IntWritable nounCount,
			IntWritable pluralCount,
			IntWritable nounPhraseCount,
			IntWritable verbParticipleCount,
			IntWritable verbTransitiveCount,
			IntWritable verbIntransitiveCount,
			IntWritable adjectiveCount,
			IntWritable adverbCount,
			IntWritable conjunctionCount,
			IntWritable prepositionCount,
			IntWritable interjectionCount,
			IntWritable pronounCount,
			IntWritable definiteArticle,
			IntWritable indefiniteArticle,
			IntWritable nominativeCount,
			IntWritable palindromeCount)
	{
		this.countOfWords = countOfWords;
		this.nounCount = nounCount;
		this.pluralCount = pluralCount;
		this.nounPhraseCount = nounPhraseCount;
		this.verbParticipleCount = verbParticipleCount;
		this.verbTransitiveCount = verbTransitiveCount;
		this.verbIntransitiveCount = verbIntransitiveCount;
		this.adjectiveCount = adjectiveCount;
		this.adverbCount = adverbCount;
		this.conjunctionCount = conjunctionCount;
		this.prepositionCount = prepositionCount;
		this.interjectionCount = interjectionCount;
		this.pronounCount = pronounCount;
		this.definiteArticle = definiteArticle;
		this.indefiniteArticle = indefiniteArticle;
		this.nominativeCount = nominativeCount;
		this.palindromeCount = palindromeCount;
	}

	public void set(int countOfWords,
			int nounCount,
			int pluralCount,
			int nounPhraseCount,
			int verbParticipleCount,
			int verbTransitiveCount,
			int verbIntransitiveCount,
			int adjectiveCount,
			int adverbCount,
			int conjunctionCount,
			int prepositionCount,
			int interjectionCount,
			int pronounCount,
			int definiteArticle,
			int indefiniteArticle,
			int nominativeCount,
			int palindromeCount)
	{
		this.countOfWords = new IntWritable(countOfWords);
		this.nounCount = new IntWritable(nounCount);
		this.pluralCount = new IntWritable(pluralCount);
		this.nounPhraseCount = new IntWritable(nounPhraseCount);
		this.verbParticipleCount = new IntWritable(verbParticipleCount);
		this.verbTransitiveCount = new IntWritable(verbTransitiveCount);
		this.verbIntransitiveCount = new IntWritable(verbIntransitiveCount);
		this.adjectiveCount = new IntWritable(adjectiveCount);
		this.adverbCount = new IntWritable(adverbCount);
		this.conjunctionCount = new IntWritable(conjunctionCount);
		this.prepositionCount = new IntWritable(prepositionCount);
		this.interjectionCount = new IntWritable(interjectionCount);
		this.pronounCount = new IntWritable(pronounCount);
		this.definiteArticle = new IntWritable(definiteArticle);
		this.indefiniteArticle = new IntWritable(indefiniteArticle);
		this.nominativeCount = new IntWritable(nominativeCount);
		this.palindromeCount = new IntWritable(palindromeCount);
	}

	public void write(DataOutput out) throws IOException
	{
		countOfWords.write(out);
		nounCount.write(out);
		pluralCount.write(out);
		nounPhraseCount.write(out);
		verbParticipleCount.write(out);
		verbTransitiveCount.write(out);
		verbIntransitiveCount.write(out);
		adjectiveCount.write(out);
		adverbCount.write(out);
		conjunctionCount.write(out);
		prepositionCount.write(out);
		interjectionCount.write(out);
		pronounCount.write(out);
		definiteArticle.write(out);
		indefiniteArticle.write(out);
		nominativeCount.write(out);
		palindromeCount.write(out);
	}

	public void readFields(DataInput in) throws IOException
	{
		countOfWords.readFields(in);
		nounCount.readFields(in);
		pluralCount.readFields(in);
		nounPhraseCount.readFields(in);
		verbParticipleCount.readFields(in);
		verbTransitiveCount.readFields(in);
		verbIntransitiveCount.readFields(in);
		adjectiveCount.readFields(in);
		adverbCount.readFields(in);
		conjunctionCount.readFields(in);
		prepositionCount.readFields(in);
		interjectionCount.readFields(in);
		pronounCount.readFields(in);
		definiteArticle.readFields(in);
		indefiniteArticle.readFields(in);
		nominativeCount.readFields(in);
		palindromeCount.readFields(in);
	}

	public int compareTo(POSWritable o)
	{
		return 0;
	}

	public IntWritable getCountOfWords() {
		return countOfWords;
	}

	public IntWritable getNounCount() {
		return nounCount;
	}

	public IntWritable getPluralCount() {
		return pluralCount;
	}

	public IntWritable getNounPhraseCount() {
		return nounPhraseCount;
	}

	public IntWritable getVerbParticipleCount() {
		return verbParticipleCount;
	}

	public IntWritable getVerbTransitiveCount() {
		return verbTransitiveCount;
	}

	public IntWritable getVerbIntransitiveCount() {
		return verbIntransitiveCount;
	}

	public IntWritable getAdjectiveCount() {
		return adjectiveCount;
	}

	public IntWritable getAdverbCount() {
		return adverbCount;
	}

	public IntWritable getConjunctionCount() {
		return conjunctionCount;
	}

	public IntWritable getPrepositionCount() {
		return prepositionCount;
	}

	public IntWritable getInterjectionCount() {
		return interjectionCount;
	}

	public IntWritable getPronounCount() {
		return pronounCount;
	}

	public IntWritable getDefiniteArticle() {
		return definiteArticle;
	}

	public IntWritable getIndefiniteArticle() {
		return indefiniteArticle;
	}

	public IntWritable getNominativeCount() {
		return nominativeCount;
	}

	public IntWritable getPalindromeCount() {
		return palindromeCount;
	}
}
