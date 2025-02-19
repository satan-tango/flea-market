package by.fleamarket.cash;

import by.fleamarket.utilis.enums.BotState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BotStateCash {

    private final Map<Long, List<BotState>> botStateMap = new HashMap<>();

    public void saveBotState(long userId, BotState botState) {
        if (botStateMap.get(userId) != null) {
            List<BotState> states = botStateMap.get(userId);
            if (states.get(states.size() - 1) != botState) {
                //Чтобы количество состояние не превышало 15
                //Удаляю первое состояние
                if (states.size() > 15) {
                    states.remove(0);
                }
                states.add(botState);
                botStateMap.put(userId, states);
            }
        } else {
            if (botState == BotState.START) {
                botStateMap.put(userId, new ArrayList<>(Arrays.asList(BotState.START)));
            } else {
                botStateMap.put(userId, new ArrayList<>(Arrays.asList(BotState.START, botState)));
            }
        }
    }

    public void cleaningBotStateCash(long userId) {
        botStateMap.put(userId, new ArrayList<>(Arrays.asList(BotState.START)));
    }

    public BotState getCurrentBotState(long userId) {
        if (botStateMap.get(userId) == null) {
            //Best practice??
            return null;
        } else {
            return botStateMap.get(userId).get(botStateMap.get(userId).size() - 1);
        }
    }

    public List<BotState> getListOfBotStates(long userId) {
        return botStateMap.get(userId);
    }

    public void stepBackToOneState(long userId) {
        if (botStateMap.get(userId) != null) {
            List<BotState> states = botStateMap.get(userId);
            if (states.size() == 1) {
                return;
            } else {
                states.remove(states.size() - 1);
                botStateMap.put(userId, states);
            }
        } else {
            botStateMap.put(userId, new ArrayList<>(Arrays.asList(BotState.START)));
        }
    }
}
