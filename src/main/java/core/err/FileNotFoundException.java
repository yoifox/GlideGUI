package core.err;

public class FileNotFoundException extends RuntimeException
{
    public FileNotFoundException(String file)
    {
        super("File: " + file);
    }
}
