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
            boolean negative = false;
            boolean inInteger = false;
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
                    if (inInteger)
                    {
                        integer = integer * radix + digit;
                    }
                    else
                    {
                        integer = digit;
                        inInteger = true;
                    }
                }
                else
                {
                    if (inInteger)
                    {
                        if (negative)
                        {
                            write(-integer);
                        }
                        else
                        {
                            write(integer);
                        }
                        inInteger = false;
                        negative = false;
                    }
                    else if (ch == '-')
                    {
                        negative = true;
                    }
                    else
                    {
                        //otherwise keep skipping
                        negative = false;
                    }
                    
                }
            }
        }

        abstract void write(int integer);
    }

    public static final ByteBuffer parseByteBuffer(String value)
    {
        throw new UnsupportedOperationException();
    }

    private static class ByteParser extends IntegerParser
    {
        private final ByteArrayOutputStream stream;

        ByteParser(String value)
        {
            super(10);
            //estimate that the stream length is about half of the
            //string length
            stream = new ByteArrayOutputStream(1 + (value.length() >> 1));
        }

        @Override
        void write(int integer)
        {
            stream.write(integer);
        }

        final byte[] toByteArray()
        {
            return stream.toByteArray();
        }
    }

    private static final class UByteParser extends ByteParser
    {
        UByteParser(String value)
        {
            super(value);
        }

        @Override
        final void write(int integer)
        {
            super.write(integer & 0xff);
        }
    }

    public static final byte[] parseByteArray(String value)
    {
        try
        {
            ByteParser parser = new ByteParser(value);
            parser.parse(value);
            return parser.toByteArray();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return null;
        }
    }

    public static final String printByteArray(byte[] value)
    {
        final int length = value.length;
        //estimate the string length as about 4 times as long as
        //the value array
        StringBuilder builder = new StringBuilder(length << 2);
        builder.append(value[0]);
        for (int i = 1; i < length; ++i)
        {
            builder.append(' ');
            builder.append(value[i]);
        }

        return builder.toString();
    }
    
    public static final byte[] parseUByteArray(String value)
    {
        try
        {
            UByteParser parser = new UByteParser(value);
            parser.parse(value);
            return parser.toByteArray();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return null;
        }
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

    private static class ShortParser extends IntegerParser
    {
        private final ShortBuffer buffer;

        ShortParser(String value)
        {
            super(10);
            //estimate that the stream length is at most half of the
            //string length
            buffer = ShortBuffer.allocate(1 + (value.length() >> 1));
        }

        @Override
        void write(int integer)
        {
            buffer.put((short)integer);
        }

        final short[] toShortArray()
        {
            final ShortBuffer sb = (ShortBuffer) buffer.flip();
            final short[] arr = new short[sb.limit()];
            sb.get(arr);
            return arr;
        }
    }

    private static final class UShortParser extends ShortParser
    {
        UShortParser(String value)
        {
            super(value);
        }

        @Override
        final void write(int integer)
        {
            super.write(integer & 0xffff);
        }
    }

    public static final short[] parseShortArray(String value)
    {
        try
        {
            ShortParser parser = new ShortParser(value);
            parser.parse(value);
            return parser.toShortArray();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return null;
        }
    }

    public static final String printShortArray(short[] value)
    {
        throw new UnsupportedOperationException();
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
