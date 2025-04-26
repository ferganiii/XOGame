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
            playerName = message.substring(13);
            System.out.println("أنت " + playerName);
        }
        else if (message.startsWith("TURN:")) {
            isMyTurn = message.substring(5).equals(playerName);
            if (isMyTurn) {
                System.out.println("===> الآن دورك!");
                getPlayerMove();
            }
        }
        else if (message.startsWith("MOVE:")) {
            System.out.println("حدث: " + message.substring(5));
        }
        else if (message.startsWith("GAME_OVER:")) {
            System.out.println("نتيجة اللعبة: " + message.substring(10));
            isMyTurn = false;
        }
        else {
            System.out.println("الخادم: " + message);
        }
    }

    private void getPlayerMove() {
        if (!isMyTurn) return;
        
        System.out.print(playerName + "، أدخل موقع التحريك (0-8): ");
        String move = scanner.nextLine();
        
        if (move.matches("[0-8]")) {
            send("MOVE:" + move);
        } else {
            System.out.println("خطأ: يجب إدخال رقم بين 0 و 8 فقط");
            getPlayerMove();
        }
    }

    // ... باقي الدوال كما هي
}