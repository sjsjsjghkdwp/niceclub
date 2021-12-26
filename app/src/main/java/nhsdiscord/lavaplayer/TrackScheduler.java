package nhsdiscord.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    public boolean repeatAll = false;
    public boolean repeatOne = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if(!this.player.startTrack(track,true)) {
            this.queue.offer(track);
        }
    }

    public void skipTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }
    public void nextTrack(AudioTrack track) {
        if(this.repeatAll){
            this.queue.add(track.makeClone());
        }
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext){
            if(this.repeatOne){
                this.player.startTrack(track.makeClone(), false);
                return;
            }
            nextTrack(track);
        }
        super.onTrackEnd(player, track, endReason);
    }
}
