import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.Scanner;

public class GameClient extends WebSocketClient {
    private String playerName;
    private boolean isMyTurn = false;
    private Scanner scanner = new Scanner(System.in);

    public GameClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("تم الاتصال بالخادم، في انتظار تعيين الدور...");
    }

    @Override
    public void onMessage(String message) {
        if (message.startsWith("ASSIGN_PLAYER:")) {
            handlePlayerAssignment(message);
        } else if (message.startsWith("TURN:")) {
            handleTurnAssignment(message);
        } else if (message.startsWith("MOVE:")) {
            handleMove(message);
        } else if (message.startsWith("GAME_OVER:")) {
            handleGameOver(message);
        } else {
            System.out.println("الخادم: " + message);
        }
    }

    private void handlePlayerAssignment(String message) {
        playerName = message.substring(13);
        System.out.println("أنت " + playerName);
    }

    private void handleTurnAssignment(String message) {
        isMyTurn = message.substring(5).equals(playerName);
        if (isMyTurn) {
            System.out.println("===> الآن دورك!");
            getPlayerMove();
        } else {
            System.out.println("ليس دورك بعد، انتظر...");
        }
    }

    private void handleMove(String message) {
        System.out.println("حدث: " + message.substring(5));
    }

    private void handleGameOver(String message) {
        System.out.println("نتيجة اللعبة: " + message.substring(10));
        isMyTurn = false;
    }

    private void getPlayerMove() {
        if (!isMyTurn) return;

        String move = "";
        boolean validMove = false;

        while (!validMove) {
            System.out.print(playerName + "، أدخل موقع التحريك (0-8): ");
            move = scanner.nextLine();

            // التحقق من أن المدخل هو رقم بين 0 و 8 فقط
            if (move.matches("[0-8]")) {
                validMove = true;
            } else {
                System.out.println("خطأ: يجب إدخال رقم بين 0 و 8 فقط");
            }
        }

        send("MOVE:" + move);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("تم إغلاق الاتصال: " + reason);
        scanner.close();  // إغلاق Scanner
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("حدث خطأ: " + ex.getMessage());
        ex.printStackTrace();
    }
}
