import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RocketLaunchSimulator {
    private static final Logger logger = Logger.getLogger("RocketLaunchSimulator");

    
    private Stage stage = Stage.PRE_LAUNCH;
    private double fuel = 100.0;          
    private double altitude = 0.0;        
    private double speed = 0.0;           
    private boolean checksDone = false;
    private boolean missionStarted = false;

    public static void main(String[] args) {
        RocketLaunchSimulator sim = new RocketLaunchSimulator();
        sim.run();
    }

    private void run() {
        logger.info("Rocket Launch Simulator started.");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter commands (type 'exit' to quit):");

        while (true) {
            try {
                System.out.print("> ");
                String line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting simulator. Goodbye!");
                    break;
                }
                if (line.isEmpty()) continue;

                processCommand(line);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing command", e);
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
        sc.close();
    }

    private void processCommand(String line) {
        String[] parts = line.split("\\s+");
        String command = parts[0];

        switch (command) {
            case "start_checks":
                startChecks();
                break;
            case "launch":
                launch();
                break;
            case "fast_forward":
                if (parts.length < 2) {
                    System.out.println("Usage: fast_forward <seconds>");
                } else {
                    int seconds = Integer.parseInt(parts[1]);
                    fastForward(seconds);
                }
                break;
            default:
                System.out.println("Unknown command.");
        }
    }

    private void startChecks() {
        if (checksDone) {
            System.out.println("Checks already completed. All systems are 'Go' for launch.");
            return;
        }
        
        checksDone = true;
        System.out.println("All systems are 'Go' for launch.");
        logger.info("Pre-launch checks completed.");
    }

    private void launch() {
        if (!checksDone) {
            System.out.println("You must complete pre-launch checks first (start_checks).");
            return;
        }
        if (missionStarted) {
            System.out.println("Mission already started.");
            return;
        }
        missionStarted = true;
        stage = Stage.STAGE1;
        System.out.println("Launch initiated!");
        logger.info("Launch initiated.");
        
        tick(1); 
    }

    private void fastForward(int seconds) {
        if (!missionStarted) {
            System.out.println("You must launch first.");
            return;
        }
        tick(seconds);
    }

   
    private void tick(int seconds) {
        for (int i = 0; i < seconds; i++) {
            if (stage == Stage.ORBIT || stage == Stage.FAILED) {
                break; 
            }
            advanceOneSecond();
        }
    }

    private void advanceOneSecond() {
        switch (stage) {
            case STAGE1:
                simulateStage1();
                break;
            case STAGE2:
                simulateStage2();
                break;
            default:
                
                break;
        }
    }

    private void simulateStage1() {
        fuel -= 5; 
        altitude += 10; 
        speed += 1000; 
        printStatus();

        if (fuel <= 50) {
            // Stage 1 complete -> separate to stage 2
            stage = Stage.STAGE2;
            System.out.println("Stage 1 complete. Separating stage. Entering Stage 2.");
            logger.info("Stage 1 separation.");
        }

        if (fuel <= 0) {
            failMission();
        }
    }

    private void simulateStage2() {
        fuel -= 2; // slower burn
        altitude += 20; // km
        speed += 2000; // km/h
        printStatus();

        if (altitude >= 200 && speed >= 28000) {
            // Achieved orbit
            stage = Stage.ORBIT;
            System.out.println("Orbit achieved! Mission Successful.");
            logger.info("Mission success.");
        }

        if (fuel <= 0 && stage != Stage.ORBIT) {
            failMission();
        }
    }

    private void printStatus() {
        String stageName = (stage == Stage.STAGE1) ? "1" : (stage == Stage.STAGE2 ? "2" : stage.toString());
        System.out.printf("Stage: %s, Fuel: %.0f%%, Altitude: %.0f km, Speed: %.0f km/h%n",
                stageName, fuel, altitude, speed);
    }

    private void failMission() {
        stage = Stage.FAILED;
        System.out.println("Mission Failed due to insufficient fuel.");
        logger.warning("Mission failed due to insufficient fuel.");
    }

    private enum Stage {
        PRE_LAUNCH,
        STAGE1,
        STAGE2,
        ORBIT,
        FAILED
    }
}
