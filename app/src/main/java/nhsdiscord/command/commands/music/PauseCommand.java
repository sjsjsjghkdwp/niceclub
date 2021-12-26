package nhsdiscord.command.commands.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nhsdiscord.command.CommandContext;
import nhsdiscord.command.ICommand;
import nhsdiscord.lavaplayer.GuildMusicManager;
import nhsdiscord.lavaplayer.PlayerManager;

public class PauseCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I need to be in a voice channel for this to work").queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }
        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        if (musicManager.scheduler.player.isPaused()){
            musicManager.scheduler.player.setPaused(false);
            channel.sendMessage("The player has been resumed").queue();
        } else {
            musicManager.scheduler.player.setPaused(true);
            channel.sendMessage("The player has been paused").queue();
        }
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getHelp() {
        return "일시정지";
    }
}
