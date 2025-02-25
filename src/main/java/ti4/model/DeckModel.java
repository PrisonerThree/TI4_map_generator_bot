package ti4.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import ti4.helpers.Emojis;


public class DeckModel implements ModelInterface, EmbeddableModel {

  private String alias;
  private String name;
  private String type;
  private String description;
  private List<String> cardIDs;

  public boolean isValid() {
        return alias != null
            && name != null
            && type != null
            && description != null
            && cardIDs != null;
    }

    public String getAlias() {
        return alias;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getNewDeck() {
        return new ArrayList<>(cardIDs);
    }

    public List<String> getNewShuffledDeck() {
        List<String> cardList = new ArrayList<>(cardIDs);
        Collections.shuffle(cardList);
        return cardList;
    }

    public int getCardCount() {
        return cardIDs.size();
    }

    private void setCardIDs(List<String> cardIDs) { // This method is for Jackson
      this.cardIDs = Collections.unmodifiableList(cardIDs);
    }

    @Override
    public MessageEmbed getRepresentationEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        //TITLE
        StringBuilder title = new StringBuilder();
        title.append(getTypeEmoji());
        title.append("__**").append(getName()).append("**__");
        eb.setTitle(title.toString());

        //DESCRIPTION
        StringBuilder description = new StringBuilder();
        description.append(getDescription());
        eb.setDescription(description.toString());

        // // FIELDS
        // String cardList = getNewDeck().stream().collect(Collectors.joining("\n"));
        // if (cardList.length() <= 1024) {
        //     eb.addField("Card IDs:", cardList, true);
        // } else {
        //     while (true) {
        //         if (cardList.length() > 1024) {
        //             String firstCardList = StringUtils.left(StringUtils.substringBeforeLast(cardList, "\n"), 1024);
        //             eb.addField("Card IDs:", firstCardList, true);
        //             cardList = cardList.replace(firstCardList, "");
        //         } else {
        //             eb.addField("Card IDs:", cardList, true);
        //             break;
        //         }
        //     }
        // }

        
        //FOOTER
        StringBuilder footer = new StringBuilder();
        footer.append("ID: ").append(getAlias());
        eb.setFooter(footer.toString());
        
        eb.setColor(Color.BLACK);
        return eb.build();
    }

    @Override
    public boolean search(String searchString) {
        return getAlias().contains(searchString) || getName().contains(searchString) || getType().contains(searchString) || getDescription().contains(searchString);
    }

    @Override
    public String getAutoCompleteName() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getType()).append("] ").append(getName()).append(" --> ").append(getDescription());
        return StringUtils.left(StringUtils.substringBefore(sb.toString(), "\n"), 100);
    }

    private String getTypeEmoji() {
        return switch (getType()) {
            case "technology" -> Emojis.NonUnitTechSkip;
            case "agenda" -> Emojis.Agenda;
            case "event" -> "";
            case "action_card" -> Emojis.ActionCard;
            case "public_stage_1_objective" -> Emojis.Public1;
            case "public_stage_2_objective" -> Emojis.Public2;
            case "secret_objective" -> Emojis.SecretObjective;
            case "relic" -> Emojis.RelicCard;
            case "explore" -> Emojis.FrontierCard + Emojis.CulturalCard + Emojis.IndustrialCard + Emojis.HazardousCard;
            default -> "";
        };
    }
}
