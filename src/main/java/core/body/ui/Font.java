package core.body.ui;

import org.lwjgl.stb.STBTTFontinfo;

public class Font
{
    public STBTTFontinfo stbttFontinfo;
    private String name;

    public Font(STBTTFontinfo stbttFontinfo)
    {
        this.stbttFontinfo = stbttFontinfo;
    }
    public Font(STBTTFontinfo stbttFontinfo, String name)
    {
        this.stbttFontinfo = stbttFontinfo;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
