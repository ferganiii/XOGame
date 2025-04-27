package viewmodel;

import view.XOView;
import model.GameModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameViewModel {
    private XOView view;
    private GameModel model;

    // تم تعديل المُنشئ ليقبل كلا الكائنين view و model
    public GameViewModel(XOView view, GameModel model) {
        this.view = view;
        this.model = model;
        initListeners();
        view.showUI();
    }

    private void initListeners() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int r = row;
                final int c = col;
                view.addButtonListener(r, c, e -> {
                    if (model.getBoardCell(r, c).equals("")) {
                        model.setBoardCell(r, c, model.getCurrentPlayer());
                        view.setButtonText(r, c, model.getCurrentPlayer());

                        if (model.checkWin(model.getCurrentPlayer())) {
                            view.setStatusText("Player " + model.getCurrentPlayer() + " Wins!");
                            view.enableRestartButton();
                        } else if (model.isDraw()) {
                            view.setStatusText("It's a Draw!");
                            view.enableRestartButton();
                        } else {
                            model.togglePlayer();
                            view.setStatusText("Player " + model.getCurrentPlayer() + "'s Turn");
                        }
                    }
                });
            }
        }

        //  Listener لزر Restart
        view.addRestartListener(e -> restartGame());
    }

    public void restartGame() {
        model.resetBoard();
        view.resetBoard();
        view.setStatusText("Player X's Turn");
        view.disableRestartButton();
    }
}
