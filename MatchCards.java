import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = {
        "darkness", "double", "fairy", "fighting", "fire", 
        "grass", "lightning", "metal", "psychic", "water"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    int player1Score = 0;
    int player2Score = 0;
    int currentPlayer = 1; // 1 for Player 1, 2 for Player 2
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    JButton card1Selected;
    JButton card2Selected;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Player info text
        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        updatePlayerText();

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        // Card game board
        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardBackImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            // Check for a match
                            if (card1Selected.getIcon() == card2Selected.getIcon()) {
                                if (currentPlayer == 1) {
                                    player1Score++;
                                } else {
                                    player2Score++;
                                }
                                card1Selected = null;
                                card2Selected = null;
                                updatePlayerText();
                                checkForWinner();
                            } else {
                                hideCardTimer.start(); // Hide cards if no match
                                switchPlayer(); // Switch player if no match
                            }
                        }

                        
                    }
                }
                
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        // Restart game button
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }

                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
               
                shuffleCards();

                // Reassign buttons with new cards
                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardBackImageIcon);
                }

                errorCount = 0;
                player1Score = 0;
                player2Score = 0;
                currentPlayer = 1;
                updatePlayerText();
                hideCardTimer.start();
            }
        });
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        // Timer to hide cards after a delay
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    void setupCards() {
        cardSet = new ArrayList<>();
        for (String cardName : cardList) {
            Image cardImg = new ImageIcon(getClass().getResource("/image/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet); // Duplicate cards for matching pairs

        Image cardBackImg = new ImageIcon(getClass().getResource("/image/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        } else {
            for (JButton tile : board) {
                tile.setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }
    

    void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        updatePlayerText();
       
        
    }

    void updatePlayerText() {
        textLabel.setText("Player 1 Score: " + player1Score + " | Player 2 Score: " + player2Score );
    }

    void checkForWinner() {
        int totalPairs = cardList.length;
        if (player1Score + player2Score == totalPairs) { // Game ends when all pairs are found
            String message;
            if (player1Score > player2Score) {
                message = "Player 1 wins with " + player1Score + " pairs!";
            } else if (player2Score > player1Score) {
                message = "Player 2 wins with " + player2Score + " pairs!";
            } else {
                message = "It's a tie! Both players scored " + player1Score + " pairs.";
            }
            
            // Create a custom JPanel for the message
            JPanel messagePanel = new JPanel(new BorderLayout());
            JLabel messageLabel = new JLabel(message, JLabel.CENTER);
            messageLabel.setFont(new Font("Arial", Font.BOLD,30));
            messagePanel.add(messageLabel, BorderLayout.CENTER);
    
            // Show message dialog centered on the screen
            JOptionPane optionPane = new JOptionPane(messagePanel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(frame, "Game Over");
            dialog.setLocationRelativeTo(null); // Center on the screen
            dialog.setVisible(true);
    
            
        }
    }

}
