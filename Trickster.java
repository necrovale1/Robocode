import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

/**
 * Trickster - Um robô que se mantém próximo à borda inferior esquerda e se move para desviar de tiros inimigos.
 */
public class Trickster extends AdvancedRobot {

    private static final double BUFFER = 50; // Distância mínima para evitar paredes
    private static final double EDGE_DISTANCE = 100; // Distância desejada da borda
    private boolean avoidingShots = false; // Controle de evasão de tiros

    @Override
    public void run() {
        // Define as cores como amarelo neon
        setBodyColor(new Color(255, 255, 0)); // Amarelo neon
        setGunColor(new Color(255, 255, 0));  // Amarelo neon
        setRadarColor(new Color(255, 255, 0)); // Amarelo neon
        setBulletColor(new Color(255, 255, 0)); // Amarelo neon

        // Inicialmente, o robô se aproxima da borda inferior esquerda
        moveToEdge();

        while (true) {
            if (avoidingShots) {
                evasiveMovement();
            }
            // Vira o radar para escanear continuamente
            turnRadarRight(360);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        avoidingShots = true; // Ativa a evasão de tiros ao detectar um robô
        double bearing = e.getBearing();
        double distance = e.getDistance();
        double absBearing = getHeading() + bearing;
        double radarTurn = Utils.normalRelativeAngleDegrees(absBearing - getRadarHeading());
        turnRadarRight(radarTurn);
        double gunTurn = Utils.normalRelativeAngleDegrees(absBearing - getGunHeading());
        turnGunRight(gunTurn);

        // Dispara se o alvo estiver dentro do alcance
        if (distance < 400) {
            fire(1);
        }
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        // Se colidir com uma parede, recua e muda a direção
        back(100);
        turnRight(90);
    }

    private void moveToEdge() {
        double x = getX();
        double y = getY();
        double width = getBattleFieldWidth();
        double height = getBattleFieldHeight();
        
        // Ajusta a movimentação para alinhar-se com a borda inferior esquerda
        if (x > EDGE_DISTANCE && y > EDGE_DISTANCE) {
            turnLeft(90);
            ahead(100);
        } else if (x <= EDGE_DISTANCE && y > EDGE_DISTANCE) {
            turnLeft(90);
            ahead(100);
        } else if (x <= EDGE_DISTANCE && y <= EDGE_DISTANCE) {
            turnRight(90);
            ahead(100);
        } else if (x > EDGE_DISTANCE && y <= EDGE_DISTANCE) {
            turnLeft(90);
            ahead(100);
        }
    }

    private void evasiveMovement() {
        double x = getX();
        double y = getY();
        double width = getBattleFieldWidth();
        double height = getBattleFieldHeight();

        // Ajusta a movimentação para evitar as paredes e desviar de tiros
        if (x < BUFFER) {
            // Se muito perto da borda esquerda
            turnRight(90);
            ahead(100);
        } else if (x > width - BUFFER) {
            // Se muito perto da borda direita
            turnLeft(90);
            ahead(100);
        } else if (y < BUFFER) {
            // Se muito perto da borda superior
            turnRight(90);
            ahead(100);
        } else if (y > height - BUFFER) {
            // Se muito perto da borda inferior
            turnLeft(90);
            ahead(100);
        } else {
            // Movimenta o robô em um padrão evasivo se não estiver perto da borda
            setTurnRight(90);
            ahead(100);
        }

        // Após um movimento evasivo, retoma a tentativa de manter-se próximo à borda
        avoidingShots = false;
        moveToEdge();
    }
}
