package nhsdiscord.command;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nhsdiscord.Config;
import nhsdiscord.command.commands.EventWaiterCommand;
import nhsdiscord.command.commands.HelpCommand;
import nhsdiscord.command.commands.PingCommand;
import nhsdiscord.command.commands.music.*;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager(EventWaiter waiter){
//        addCommand(new PingCommand());
        addCommand(new HelpCommand(this));
        addCommand(new JoinCommand());
        addCommand(new PlayCommand());
        addCommand(new StopCommand());
        addCommand(new SkipCommand());
        addCommand(new QueueCommand());
        addCommand(new RepeatCommand());
        addCommand(new LeaveCommand());
        addCommand(new NowPlayingCommand());
        addCommand(new PauseCommand());
//        addCommand(new EventWaiterCommand(waiter));
    }

    private void addCommand(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }
    public List<ICommand> getCommends() {
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search){
        String searchLower = search.toLowerCase();
        for(ICommand cmd : this.commands) {
            if(cmd.getName().equals(searchLower) || cmd.getAliasese().contains(searchLower)){
                return cmd;
            }
        }
        return null;
    }

    public void handle(GuildMessageReceivedEvent event){
        // prefix는 제거, 공백을 기준으로 split하여 보유
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("prefix")), "")
                .split("\\s+");
        // prefix 직후 첫번째 단어를 invoke로 하여 해당 이름 or Alias를 가진 Command를 찾음
        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);

        // 해당 Command에 split한 뒷내용을 담아 실행
        if(cmd != null) {
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1,split.length);

            CommandContext ctx = new CommandContext(event,args);

            cmd.handle(ctx);
        }
    }
}
