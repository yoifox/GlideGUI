package core.err;

public class InvalidUniformLocationException extends RuntimeException
{
    public InvalidUniformLocationException(int program, String uniform, int location)
    {
        super("\nuniformName: \"" + uniform + "\", location: " + location + " and program: " + program);
    }
}
