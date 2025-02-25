package ti4.commands.cardsso;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import ti4.helpers.Constants;
import ti4.helpers.Helper;
import ti4.map.Game;
import ti4.map.Player;
import ti4.message.MessageHelper;

public class UnscoreSO extends SOCardsSubcommandData {
    public UnscoreSO() {
        super(Constants.UNSCORE_SO, "Unscore Secret Objective");
        addOptions(new OptionData(OptionType.INTEGER, Constants.SECRET_OBJECTIVE_ID, "Scored Secret objective ID that is sent between ()").setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game activeGame = getActiveGame();
        Player player = activeGame.getPlayer(getUser().getId());
        player = Helper.getGamePlayer(activeGame, player, event, null);
        if (player == null) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Player could not be found");
            return;
        }
        OptionMapping option = event.getOption(Constants.SECRET_OBJECTIVE_ID);
        if (option == null) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Please select what Secret Objective to unscore");
            return;
        }

        boolean scored = activeGame.unscoreSecretObjective(getUser().getId(), option.getAsInt());
        if (!scored) {
            List<String> scoredSOs = activeGame.getScoredSecretObjective(getUser().getId()).entrySet().stream()
                .map(e -> "> (" + e.getValue() + ") " + SOInfo.getSecretObjectiveRepresentationShort(e.getKey()))
                .toList();
            StringBuilder sb = new StringBuilder("Secret Objective ID found - please retry.\nYour current scored SOs are:\n");
            scoredSOs.forEach(sb::append);
            if (scoredSOs.isEmpty()) sb.append("> None");
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), sb.toString());
            return;
        }

        MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Unscored SO " + option.getAsInt());
        SOInfo.sendSecretObjectiveInfo(activeGame, player, event);
    }
}
