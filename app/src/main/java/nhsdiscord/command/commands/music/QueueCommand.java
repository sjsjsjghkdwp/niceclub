package nhsdiscord.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import nhsdiscord.command.CommandContext;
import nhsdiscord.command.ICommand;
import nhsdiscord.lavaplayer.GuildMusicManager;
import nhsdiscord.lavaplayer.PlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            channel.sendMessage("The queue is currently empty").queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 30);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final MessageAction messageAction = channel.sendMessage("**Current Queue**\n");

        if (trackList.size() > trackCount) {
            messageAction.append("And `")
                    .append(String.valueOf(trackList.size() - trackCount))
                    .append("` more...\n");
        }

        for (int i=trackCount-1; i>=0; i--) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            messageAction.append('#')
                    .append(String.valueOf(i+1))
                    .append(" `")
                    .append(info.title)
                    .append("` by `")
                    .append(info.author)
                    .append("` [`")
                    .append(formatTime(track.getDuration()))
                    .append("`]\n");
        }

        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        AudioTrack track = audioPlayer.getPlayingTrack();
        final AudioTrackInfo info = track.getInfo();
        messageAction.appendFormat("nowplaying `%s` by `%s` (Link: <%s>)", info.title, info.author, info.uri);

        messageAction.queue();
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis% TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        if (hours==0L) return String.format("%02d:%02d",minutes,seconds);
        return String.format("%02d:%02d:%02d",hours,minutes,seconds);
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "show the queued up songs";
    }

    @Override
    public List<String> getAliasese() {
        return List.of("q");
    }
}
