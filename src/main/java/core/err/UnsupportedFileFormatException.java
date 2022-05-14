package core.err;

public class UnsupportedFileFormatException extends RuntimeException
{
    public UnsupportedFileFormatException(String file)
    {
        super("File: " + file);
    }
}
