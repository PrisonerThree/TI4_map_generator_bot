package ti4.commands.leaders;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ti4.helpers.ButtonHelper;
import ti4.helpers.CombatModHelper;
import ti4.helpers.Constants;
import ti4.helpers.Emojis;
import ti4.helpers.Helper;
import ti4.map.Game;
import ti4.map.Leader;
import ti4.map.Player;
import ti4.message.MessageHelper;
import ti4.model.LeaderModel;

public class ExhaustLeader extends LeaderAction {
	public ExhaustLeader() {
		super(Constants.EXHAUST_LEADER, "Exhaust leader");
		addOptions(new OptionData(OptionType.INTEGER, Constants.TG, "TG count to add to leader").setRequired(false));
	}

	@Override
	void action(SlashCommandInteractionEvent event, String leaderID, Game activeGame, Player player) {
		Leader playerLeader = player.unsafeGetLeader(leaderID);
		if (playerLeader == null) {
			sendMessage("Leader '" + leaderID + "'' not found");
			return;
		}

		if (playerLeader.isLocked()) {
			sendMessage("Leader '" + playerLeader.getId() + "' is locked");
			return;
		}

		if (playerLeader.isExhausted()) {
			sendMessage("Leader '" + playerLeader.getId() + "' is exhausted already");
			return;
		}

		Integer tgCount = event.getOption(Constants.TG, null, OptionMapping::getAsInt);
		exhaustLeader(event, activeGame, player, playerLeader, tgCount);
	}
	
	public static void exhaustLeader(GenericInteractionCreateEvent event, Game activeGame, Player player, Leader leader, Integer tgCount) {
		leader.setExhausted(true);
		MessageHelper.sendMessageToChannel(event.getMessageChannel(), player.getRepresentation() + " exhausted:");
		LeaderModel leaderModel = leader.getLeaderModel().orElse(null);
		if (leaderModel != null) {
			event.getMessageChannel().sendMessageEmbeds(leaderModel.getRepresentationEmbed()).queue();
		} else {
			MessageHelper.sendMessageToChannel(event.getMessageChannel(), leader.getId());
		}

		if (tgCount != null) {
			StringBuilder sb = new StringBuilder();
			leader.setTgCount(tgCount);
			sb.append("\n").append(tgCount).append(Emojis.getTGorNomadCoinEmoji(activeGame))
					.append(" was placed on top of the leader");
			if (leader.getTgCount() != tgCount) {
				sb.append(" *(").append(tgCount).append(Emojis.getTGorNomadCoinEmoji(activeGame)).append(" total)*\n");
			}
			MessageHelper.sendMessageToChannel(event.getMessageChannel(), sb.toString());
		}
		
		var posssibleCombatMod = CombatModHelper.GetPossibleTempModifier(Constants.LEADER, leader.getId(), player.getNumberTurns());
		if (posssibleCombatMod != null) {
			player.addNewTempCombatMod(posssibleCombatMod);
			MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Combat modifier will be applied next time you push the combat roll button.");
		}
	}
}
