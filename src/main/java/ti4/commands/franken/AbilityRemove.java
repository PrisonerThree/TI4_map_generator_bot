package ti4.commands.franken;

import java.util.List;

import ti4.helpers.Constants;
import ti4.helpers.Helper;
import ti4.map.Player;

public class AbilityRemove extends AbilityAddRemove {
    public AbilityRemove() {
        super(Constants.ABILITY_REMOVE, "Remove an ability from your faction");
    }

    @Override
    public void doAction(Player player, List<String> abilityIDs) {
        StringBuilder sb = new StringBuilder(player.getRepresentation()).append(" removed abilities:\n");
        for (String abilityID : abilityIDs) {
            if (!player.hasAbility(abilityID)) {
                sb.append("> ").append(abilityID).append(" (player did not have this ability)");
            } else {
                sb.append("> ").append(abilityID);
            }
            sb.append("\n");
            player.removeAbility(abilityID);
        }
        sendMessage(sb.toString());
    }
}
