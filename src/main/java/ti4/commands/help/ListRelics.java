package ti4.commands.help;

import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.generator.Mapper;
import ti4.helpers.Constants;
import ti4.message.MessageHelper;
import ti4.model.RelicModel;

public class ListRelics extends HelpSubcommandData {

    public ListRelics() {
        super(Constants.LIST_RELICS, "List all relics the bot can use");
        addOptions(new OptionData(OptionType.STRING, Constants.SEARCH, "Searches the text and limits results to those containing this string.").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String searchString = event.getOption(Constants.SEARCH, null, OptionMapping::getAsString);
        Map<String, RelicModel> relicModelList = Mapper.getRelics();
        List<String> searchedList = relicModelList.entrySet().stream()
            .map(entry -> entry.getKey() + "\n> " + entry.getValue().getSimpleRepresentation())
            .filter(s -> searchString == null || s.toLowerCase().contains(searchString.toLowerCase()))
            .sorted().toList();
        
        String searchDescription = searchString == null ? "" : " search: " + searchString;
        String message = "**__Relic List__**" + searchDescription + "\n" + String.join("\n", searchedList);
        if (searchedList.size() > 3) {
            String threadName = "/help list_relics" + searchDescription;
            MessageHelper.sendMessageToThread(event.getChannel(), threadName, message);
        } else if (searchedList.size() > 0) {
            event.getChannel().sendMessage(message).queue();
        }
    }
}
