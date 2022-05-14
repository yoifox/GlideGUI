package core.body;

import org.lwjgl.openal.AL10;

public class Sound extends Body
{
    private final int bufferId;
    private final int sourceId;
    private boolean isPlaying = false;

    public Sound(int bufferId, int sourceId)
    {
        this.bufferId = bufferId;
        this.sourceId = sourceId;
    }

    public void play()
    {
        int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
        if(state == AL10.AL_STOPPED)
        {
            isPlaying = false;
            AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
        }
        if(!isPlaying)
        {
            AL10.alSourcePlay(sourceId);
            isPlaying = true;
        }
    }

    public void stop()
    {
        if(!isCreated()) return;
        if(isPlaying)
        {
            AL10.alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public int getBufferId() {
        return bufferId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public boolean isPlaying()
    {
        int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
        if(state == AL10.AL_STOPPED)
        {
            isPlaying = false;
        }
        return isPlaying;
    }
}
