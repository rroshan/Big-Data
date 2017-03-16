package Lab4.Lab4;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

public class MeanPairWritable implements WritableComparable<MeanPairWritable>
{
	FloatWritable sum;
	IntWritable count;

	public MeanPairWritable()
	{
		sum = new FloatWritable(0);
		count = new IntWritable(0);
	}

	public MeanPairWritable(FloatWritable sum,
			IntWritable count)
	{
		this.sum = sum;
		this.count = count;
	}

	public void set(float sum,
			int count)
	{
		this.sum = new FloatWritable(sum);
		this.count = new IntWritable(count);
	}

	public void write(DataOutput out) throws IOException
	{
		sum.write(out);
		count.write(out);
	}

	public void readFields(DataInput in) throws IOException
	{
		sum.readFields(in);
		count.readFields(in);
	}

	public FloatWritable getSum() {
		return sum;
	}

	public IntWritable getCount() {
		return count;
	}

	public int compareTo(MeanPairWritable o) {
		return 0;
	}
}
