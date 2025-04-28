import view.XOView;
import viewmodel.GameViewModel;
import model.GameModel;

public class Main {
    public static void main(String[] args) {
        XOView view = new XOView();
        GameModel model = new GameModel();
        new GameViewModel(view, model); // إرسال كائنات view و modeـ ViewModel
    }
}
