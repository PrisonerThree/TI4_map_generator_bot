package ti4.commands.special;

import java.util.Map;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.commands.units.AddUnits;
import ti4.generator.Mapper;
import ti4.helpers.Constants;
import ti4.helpers.Helper;
import ti4.helpers.Units.UnitKey;
import ti4.helpers.Units.UnitType;
import ti4.map.*;
import ti4.message.MessageHelper;
import ti4.model.UnitModel;

import java.util.ArrayList;
import java.util.List;

public class FighterConscription extends SpecialSubcommandData {
    public FighterConscription() {
        super(Constants.FIGHTER_CONSCRIPTION, "Fighter conscription +1 fighter in each space area");
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER, "Player for which you set stats").setRequired(false));
        addOptions(new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Faction or Color for which you set stats").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game activeGame = getActiveGame();
        Player player = activeGame.getPlayer(getUser().getId());
        player = Helper.getGamePlayer(activeGame, player, event, null);
        player = Helper.getPlayer(activeGame, player, event);
        if (player == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Player could not be found");
            return;
        }
        doFfCon(event, player, activeGame);
    }

    public void doFfCon(GenericInteractionCreateEvent event, Player player, Game activeGame){
        String colorID = Mapper.getColorID(player.getColor());

        List<Tile> tilesAffected = new ArrayList<>();
        for (Tile tile : activeGame.getTileMap().values()) {
            boolean hasSD = false;
            boolean hasCap = false;
            boolean blockaded = false;
            for (UnitHolder unitHolder : tile.getUnitHolders().values()) {
                // player has a space dock in the system
                Integer numSd = unitHolder.getUnitCount(UnitType.Spacedock, colorID);
                numSd += unitHolder.getUnitCount(UnitType.CabalSpacedock, colorID);
                numSd += unitHolder.getUnitCount(UnitType.PlenaryOrbital, colorID);
                if ((numSd != null && numSd > 0)) {
                    hasSD = true;
                }

                // Check if space area contains capacity units or another player's units
                if ("space".equals(unitHolder.getName())) {
                    Map<UnitKey, Integer> units = unitHolder.getUnits();
                    for (Map.Entry<UnitKey, Integer> unit : units.entrySet()) {
                        UnitKey unitKey = unit.getKey();

                        Integer quantity = unit.getValue();

                        if (player.unitBelongsToPlayer(unitKey) && quantity != null && quantity > 0) {
                            UnitModel unitModel = player.getUnitFromUnitKey(unitKey);
                            if (unitModel == null) continue;
                            if (unitModel.getCapacityValue() > 0) {
                                hasCap = true;
                            }
                        } else if (quantity != null && quantity > 0) {
                            blockaded = true;
                            break;
                        }
                    }
                }

                if (blockaded || hasCap) {
                    break;
                }
            }

            if (!blockaded && (hasCap || hasSD)) {
                new AddUnits().unitParsing(event, player.getColor(), tile, "ff", activeGame);
                tilesAffected.add(tile);
            }
        }

        String msg = "Added " + tilesAffected.size() + " fighters.";
        if (tilesAffected.size() > 0) {
            msg += " Please check fleet size and capacity in each of the systems: ";
        }
        boolean first = true;
        StringBuilder msgBuilder = new StringBuilder(msg);
        for (Tile tile : tilesAffected) {
            if (first) {
                msgBuilder.append("\n> **").append(tile.getPosition()).append("**");
                first = false;
            } else {
                msgBuilder.append(", **").append(tile.getPosition()).append("**");
            }
        }
        msg = msgBuilder.toString();
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), msg);
    }

    @Override
    public void reply(SlashCommandInteractionEvent event) {
        SpecialCommand.reply(event);
    }
}
