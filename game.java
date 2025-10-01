import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class game {
    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "D:\\Papp\\intlij\\IntelliJ IDEA Community Edition 2025.1\\PICS\\Black Jack\\" + toString() + ".png";
        }
    }

    ArrayList<Card> deck;
    Random random = new Random();


    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;


    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;


    int boardWidth = 600;
    int boardHeight = 700;

    int cardWidth = 110;
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {

                ImageIcon backIcon = new ImageIcon("D:\\Papp\\intlij\\IntelliJ IDEA Community Edition 2025.1\\PICS\\Black Jack\\BACK.png");
                if (stayButton.isEnabled()) {

                    g.drawImage(backIcon.getImage(), 20, 20, cardWidth, cardHeight, null);
                } else {

                    ImageIcon hiddenIcon = new ImageIcon(hiddenCard.getImagePath());
                    g.drawImage(hiddenIcon.getImage(), 20, 20, cardWidth, cardHeight, null);
                }

                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    ImageIcon cardIcon = new ImageIcon(card.getImagePath());
                    g.drawImage(cardIcon.getImage(), cardWidth + 40 + (cardWidth + 10) * i, 20, cardWidth, cardHeight, null);
                }

                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    ImageIcon cardIcon = new ImageIcon(card.getImagePath());
                    g.drawImage(cardIcon.getImage(), 20 + (cardWidth + 10) * i, 300, cardWidth, cardHeight, null);
                }

                if (!stayButton.isEnabled()) {
                    int finalDealerSum = reduceDealerAce();
                    int finalPlayerSum = reducePlayerAce();

                    String message;
                    if (finalPlayerSum > 21) {
                        message = "You Lose!";
                    } else if (finalDealerSum > 21) {
                        message = "You Win!";
                    } else if (finalPlayerSum > finalDealerSum) {
                        message = "You Win!";
                    } else if (finalPlayerSum < finalDealerSum) {
                        message = "You Lose!";
                    } else {
                        message = "Tie!";
                    }

                    g.setFont(new Font("Arial", Font.BOLD, 36));
                    g.setColor(Color.WHITE);
                    g.drawString(message, boardWidth / 2 - 70, 250);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");

    public game() {
        startGame();
        ImageIcon backgraund = new ImageIcon("D:\\Papp\\intlij\\IntelliJ IDEA Community Edition 2025.1\\PICS\\Black Jack\\icons8-blackjack-62.png");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(backgraund.getImage());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        stayButton.setFocusable(false);

        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(e -> {
            if (!deck.isEmpty()) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);

                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(e -> {
            hitButton.setEnabled(false);
            stayButton.setEnabled(false);

            while (reduceDealerAce() < 17) {
                if (deck.isEmpty()) break;
                Card card = deck.remove(deck.size() - 1);
                dealerSum += card.getValue();
                dealerAceCount += card.isAce() ? 1 : 0;
                dealerHand.add(card);
            }

            gamePanel.repaint();
        });

        frame.setVisible(true);
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }

    public void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                deck.add(new Card(value, type));
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }
}
