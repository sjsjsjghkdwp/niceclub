package nhsdiscord.command;

import java.util.List;

public interface ICommand {
    void handle(CommandContext ctx);

    String getName();

    String getHelp();

    default List<String> getAliasese(){
        return List.of();
    }
}
