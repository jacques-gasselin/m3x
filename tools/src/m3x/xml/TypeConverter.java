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

    private static abstract class IntegerParser
    {
        private final int digitRadix;
        
        IntegerParser(int radix)
        {
            this.digitRadix = radix;
        }

        void parse(String value)
        {
            final int length = value.length();
            int integer = 0;
            boolean inInterger = false;
            final int radix = this.digitRadix;
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
                        write(integer);
                        inInterger = false;
                    }
                    //otherwise keep skipping
                }
            }
        }

        abstract void write(int integer);
    }

    public static final ByteBuffer parseByteBuffer(String value)
    {
        throw new UnsupportedOperationException();
    }

    private static final class UByteParser extends IntegerParser
    {
        private final ByteArrayOutputStream stream;

        UByteParser(String value)
        {
            super(10);
            //estimate that the stream length is about half of the
            //string length
            stream = new ByteArrayOutputStream(1 + (value.length() >> 1));
        }

        @Override
        final void write(int integer)
        {
            stream.write(integer & 0xff);
        }

        final byte[] toByteArray()
        {
            return stream.toByteArray();
        }
    }

    public static final byte[] parseUByteArray(String value)
    {
        UByteParser parser = new UByteParser(value);
        parser.parse(value);
        return parser.toByteArray();
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

    private static final class UShortParser extends IntegerParser
    {
        private final ShortBuffer buffer;

        UShortParser(String value)
        {
            super(10);
            //estimate that the stream length is at most half of the
            //string length
            buffer = ShortBuffer.allocate(1 + (value.length() >> 1));
        }

        @Override
        final void write(int integer)
        {
            buffer.put((short)(integer & 0xffff));
        }

        final short[] toShortArray()
        {
            final ShortBuffer sb = (ShortBuffer) buffer.flip();
            final short[] arr = new short[sb.limit()];
            sb.get(arr);
            return arr;
        }
    }

    public static final short[] parseUShortArray(String value)
    {
        try
        {
            UShortParser parser = new UShortParser(value);
            parser.parse(value);
            return parser.toShortArray();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return null;
        }
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
