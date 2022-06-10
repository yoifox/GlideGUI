package core.render;

import core.body.Texture;

public interface PostProcess
{
    void postProcess(Texture color, Texture depth);
}
