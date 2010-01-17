package m3x.xml;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * @author jgasseli
 */
public final class TypeConverter
{
    private TypeConverter()
    {
        
    }

    public static final ByteBuffer parseByteBuffer(String value)
    {
        throw new UnsupportedOperationException();
    }

    public static final byte[] parseUByteArray(String value)
    {
        //estimate that the stream length is about a quarter of the
        //string length
        ByteArrayOutputStream stream = new ByteArrayOutputStream(
                value.length() >> 2);
        final int length = value.length();
        int integer = 0;
        boolean inInterger = false;
        final int radix = 10;
        for (int i = 0; i <= length; ++i)
        {
            final char ch;
            if (i < length)
            {
                ch = value.charAt(i);
            }
            else
            {
                ch = 0;
            }
            final int digit = Character.digit(ch, radix);
            if (digit != -1)
            {
                if (inInterger)
                {
                    integer = integer * radix + digit;
                }
                else
                {
                    integer = digit;
                    inInterger = true;
                }
            }
            else
            {
                if (inInterger)
                {
                    stream.write(integer & 0xff);
                    inInterger = false;
                }
                //otherwise keep skipping
            }
        }
        
        return stream.toByteArray();
    }

    public static final String printUByteArray(byte[] value)
    {
        final int length = value.length;
        //estimate the string length as about 4 times as long as
        //the value array
        StringBuilder builder = new StringBuilder(length << 2);
        builder.append(value[0] & 0xff);
        for (int i = 1; i < length; ++i)
        {
            builder.append(' ');
            builder.append(value[i] & 0xff);
        }

        return builder.toString();
    }

    public static final short[] parseUShortArray(String value)
    {
        throw new UnsupportedOperationException();
    }

    public static final String printUShortArray(short[] value)
    {
        throw new UnsupportedOperationException();
    }

    public static final ShortBuffer parseShortBuffer(String value)
    {
        throw new UnsupportedOperationException();
    }

    public static final String printShortBuffer(ShortBuffer value)
    {
        throw new UnsupportedOperationException();
    }
}
