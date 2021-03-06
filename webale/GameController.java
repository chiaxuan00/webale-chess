// This class is for controlling the game value, setting actionlistener, counting the number of moves by players, tracking the player to move, 
// calling the functions of changing arrow state when needed, saving game in txt file, reading txt file, loading game from txt file, rotating gameboard.
// Observer design pattern is applied here in the way of implementing the Action Listener
// whenever Game Controller receives instruction from player through Listener, it calls the Listener to perform action to make the game run smoothly
package webale;

import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.io.File;

public class GameController {
    private BoardFrame boardFrame = null;
    private HomeFrame homeFrame = null;
    private Game game = null;    
    private FileOperation fileOp;

    public GameController(HomeFrame homeFrame, BoardFrame boardFrame, Game game) {
        this.homeFrame = homeFrame;
        this.boardFrame = boardFrame;
        this.game = game;
        setHomeFrameListener();
        setBoardFrameListener();
    }

    // setting actionlistener for every button in homeframe
    // Denise Lee Chia Xuan
    public void setHomeFrameListener() {
        homeFrame.getStartButton().addActionListener(startBtnListener);
        homeFrame.getContinueButton().addActionListener(continueBtnListener);
        homeFrame.getLoadButton().addActionListener(loadFileBtnListener);
        homeFrame.getInstructionButton().addActionListener(instructionBtnListener);
        homeFrame.getQuitButton().addActionListener(quitBtnListener);
    }

    // setting actionlistener for every button in boardframe
    // Denise Lee Chia Xuan
    public void setBoardFrameListener() {
        boardFrame.getToolbar().getBackButton().addActionListener(backBtnListener);
        boardFrame.getToolbar().getSaveButton().addActionListener(saveBtnListener);
        boardFrame.getToolbar().getHelpButton().addActionListener(instructionBtnListener);
        boardFrame.getToolbar().getDefeatButton().addActionListener(defeatBtnListener);

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 7; x++) {
                boardFrame.getGameBoard().getTileArray()[y][x].addActionListener(chessTileListener);
            }
        }
    }

    // Denise Lee Chia Xuan
    ActionListener startBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.playSound("./sounds/press_button.wav");
            game.setIsRedPlayer(true);
            boardFrame.getGameBoard().setHasFlipped(false);
            boardFrame.getToolbar().setMoveCount(0);
            boardFrame = new BoardFrame();
            setBoardFrameListener();
            game.setBoardFrame(boardFrame);
            boardFrame.setVisible(true);
        }
    };

    // Denise Lee Chia Xuan
    ActionListener continueBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.playSound("./sounds/press_button.wav");
            boardFrame.setVisible(true);
        }
    };

    // Koh Han Yi, Lee Min Xuan
    ActionListener loadFileBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            fileOp = new FileOperation(boardFrame.getToolbar().getMoveCount(), game.getIsRedPlayer(), boardFrame);
            game.playSound("./sounds/press_button.wav");

            boardFrame.setVisible(false);
            File file = homeFrame.openLoadDialogAndGetFileToLoad();
            boardFrame.getGameBoard().resetBoard();
            fileOp.readFile(file);
            //if the player on move is blue, rotate the board.
            if(!game.getIsRedPlayer()){
                boardFrame.getGameBoard().setHasFlipped(false);
                boardFrame.getGameBoard().rotateBoard();
            }
        }
    };

    // Denise Lee Chia Xuan
    ActionListener instructionBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.playSound("./sounds/press_button.wav");
            JLabel instructionLabel = new JLabel(homeFrame.getInstructionImageIcon());
            JOptionPane.showMessageDialog(null, instructionLabel, "Instruction", JOptionPane.PLAIN_MESSAGE, null);
        }
    };

    // Tan Jia Qi
    ActionListener defeatBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.playSound("./sounds/admitdefeat.wav");
            JLabel defeatLabel = new JLabel(homeFrame.getDefeatImageIcon());
            JOptionPane.showMessageDialog(null, defeatLabel, "You have admitted defeat!", JOptionPane.PLAIN_MESSAGE, null);
            homeFrame.getContinueButton().setEnabled(false);
            boardFrame.setVisible(false);
        }
    };

    // Lee Min Xuan
    ActionListener quitBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.playSound("./sounds/press_button.wav");
            System.exit(0);
        }
    };

    // Denise Lee Chia Xuan
    ActionListener backBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.playSound("./sounds/press_button.wav");
            homeFrame.getContinueButton().setEnabled(true);
            boardFrame.setVisible(false);
        }
    };

    // Denise Lee Chia Xuan
    ActionListener saveBtnListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            fileOp = new FileOperation(boardFrame.getToolbar().getMoveCount(), game.getIsRedPlayer(), boardFrame);
            game.playSound("./sounds/press_button.wav");
            File file = boardFrame.getToolbar().openSaveFileDialogAndGetFileToSave();
            fileOp.writeSaveFile(file);
        }
    };
    
    // counting of moves made by players
    // Denise Lee Chia Xuan, Chua Bing Quan
    ActionListener chessTileListener = new ActionListener() {
        int timeClicked = 0;
        boolean isValidMove = false;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (boardFrame.getGameBoard() != null) {
                timeClicked++;
                isValidMove = game.movePiece((ChessTile) e.getSource(), timeClicked);

                //a piece to be moved is selected when timeClicked == 1
                if (timeClicked == 1) {
                    //if the chesstile clicked as startpoint is empty, or, the piece clicked is not the piece of the player on move
                    if(!isValidMove){
                        //reset timeClicked to 0 and reselect the startpoint
                        timeClicked = 0;
                    }
                }

                //a chesstile to move to is selected when timeClicked == 2
                else if (timeClicked == 2) {
                    //reset timeClicked to 0 to select another startpoint at the next click
                    timeClicked = 0;
                    //if the piece movement to the endpoint is valid
                    if (isValidMove) {
                        // change gameboard state
                        boardFrame.getGameBoard().rotateBoard();

                        // change toolbar state
                        game.setIsRedPlayer(!game.getIsRedPlayer());
                        boardFrame.getToolbar().setPlayerToMove(game.getIsRedPlayer() ? "Red" : "Blue");
                        boardFrame.getToolbar().setMoveCount(boardFrame.getToolbar().getMoveCount() + 1);
                        
                        // add state change
                        if (boardFrame.getToolbar().getMoveCount() % 4 == 0) {
                            game.toggleState();
                        }

                        boardFrame.repaint();
                    }
                }
            }
        }
    };
}
