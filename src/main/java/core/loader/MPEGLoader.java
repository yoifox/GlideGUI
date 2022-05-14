package core.loader;


import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;

public class MPEGLoader
{
    public static void load(String file)
    {
        try {
            FFprobe ffprobe = new FFprobe("C:/Users/User/Desktop/test/ffmpeg/bin/ffprobe");
            FFmpeg ffmpeg = new FFmpeg("C:/Users/User/Desktop/test/ffmpeg/bin/ffmpeg");

            FFmpegProbeResult probeResult = ffprobe.probe(file);
            FFmpegFormat format = probeResult.getFormat();

            FFmpegStream videoStream, audioStream;
            for (FFmpegStream stream : probeResult.getStreams()) {
                if (stream.codec_type.equals(FFmpegStream.CodecType.VIDEO)) {
                    double duration = format.duration;
                    int width = stream.width;
                    int height = stream.height;
                    float frameRate = stream.avg_frame_rate.floatValue();
                    videoStream = stream;
                } else if (stream.codec_type.equals(FFmpegStream.CodecType.AUDIO)) {
                    audioStream = stream;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
