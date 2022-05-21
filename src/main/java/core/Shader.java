package core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import core.err.InvalidUniformLocationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Shader
{
    private final String vertexCode;
    private final String fragmentCode;
    private final int programId;
    private int vertexId;
    private int fragmentId;

    //private final Map<String, Integer> uniforms = new HashMap<>();
    private final Set<Uniform> uniformSet = new HashSet<>();

    public Shader(String vertexCode, String fragmentCode)
    {
        this.vertexCode = vertexCode;
        this.fragmentCode = fragmentCode;
        programId = GL20.glCreateProgram();
        if(programId == 0)
            throw new RuntimeException();
    }

    public void bind()
    {
        GL20.glUseProgram(programId);
    }
    public void unbind()
    {
        GL20.glUseProgram(0);
    }

    public void link()
    {
        compile();
        GL20.glLinkProgram(programId);
        if(GL20.glGetProgrami(programId,GL20.GL_LINK_STATUS) == 0)
            throw new RuntimeException(GL20.glGetProgramInfoLog(programId));
        if(vertexId != 0)
            GL20.glDetachShader(programId, vertexId);
        if(fragmentId != 0)
            GL20.glDetachShader(programId, fragmentId);
        GL20.glValidateProgram(programId);
        if(GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0)
            throw new RuntimeException(GL20.glGetProgramInfoLog(programId));
        GL20.glUseProgram(programId);
    }

    private void compile()
    {
        vertexId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        if(vertexId == 0) throw new RuntimeException();
        GL20.glShaderSource(vertexId, vertexCode);
        GL20.glCompileShader(vertexId);
        if(GL20.glGetShaderi(vertexId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException(GL20.glGetShaderInfoLog(vertexId));
        GL20.glAttachShader(programId, vertexId);

        fragmentId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        if(fragmentId == 0) throw new RuntimeException();
        GL20.glShaderSource(fragmentId, fragmentCode);
        GL20.glCompileShader(fragmentId);
        if(GL20.glGetShaderi(fragmentId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException(GL20.glGetShaderInfoLog(fragmentId));
        GL20.glAttachShader(programId, fragmentId);
    }

    public void createUniform(String uniformName)
    {
        int location = GL20.glGetUniformLocation(programId, uniformName);
        if(location < 0) throw new InvalidUniformLocationException(programId, uniformName, location);
        //uniforms.put(uniformName, location);
        uniformSet.add(new Uniform(uniformName, location, null));
    }

    public void cleanup()
    {
        //uniforms.clear();
        uniformSet.clear();
        if(programId != 0)
            GL20.glDeleteProgram(programId);
        GL20.glUseProgram(0);
    }

    public void setUniform(String uniformName, Object value)
    {
        Uniform uniform = getUniform(uniformName);
        if(uniform == null) return;
        int location = uniform.id;
        if(uniform.value != null)
            if(uniform.value.equals(value))
                return;
        if(value instanceof Integer v)
        {
            GL30.glUniform1i(location, v);
        }
        else if(value instanceof Float v)
        {
            GL30.glUniform1f(location, v);
        }
        else if(value instanceof Vector2f v)
        {
            GL30.glUniform2f(location, v.x, v.y);
        }
        else if(value instanceof Vector3f v)
        {
            GL30.glUniform3f(location, v.x, v.y, v.z);
        }
        else if(value instanceof Vector4f v)
        {
            GL30.glUniform4f(location, v.x, v.y, v.z, v.w);
        }
        else if(value instanceof Matrix4f v)
        {
            try(MemoryStack stack = MemoryStack.stackPush())
            {
                GL30.glUniformMatrix4fv(location, false,
                        v.get(stack.mallocFloat(16)));
            }
        }
        else
        {
            return;
        }
        uniform.value = value;
    }

    private Uniform getUniform(String name)
    {
        for(Uniform uniform : uniformSet)
            if(uniform.name.equals(name))
                return uniform;
        return null;
    }

    private static class Uniform
    {
        public String name;
        public int id;
        public Object value;

        public Uniform(String name, int id, Object value)
        {
            this.name = name;
            this.id = id;
            this.value = value;
        }
    }
}
