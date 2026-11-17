package org.firstinspires.ftc.teamcode.Utils;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Utils.Controller;

@TeleOp(name = "Controller Latency Test", group = "z_test")
public class ControllerLatencyTest extends OpMode {
    //========== Configuration =============================================
    //private static final long TELEMETRY_INTERVAL_NS = 100_000_000L;
    private static final long TELEMETRY_INTERVAL_NS = 20_000_000L;
    private static final String[] BUTTON_NAMES = {
            "A / buttonX", "B / buttonCircle", "X / buttonSquare",
            "Y / buttonTriangle", "LB", "RB", "Dpad Up", "Dpad Down",
            "Dpad Left", "Dpad Right"
    };

    private final Controller[] controls = {new Controller(), new Controller()};
    private final GamepadEx[] extended = new GamepadEx[2];
    private final boolean[][] lastRaw = new boolean[2][BUTTON_NAMES.length];
    private final boolean[][] lastWrapped = new boolean[2][BUTTON_NAMES.length];
    private final long[][] rawEdgeTime = new long[2][BUTTON_NAMES.length];
    private final long[][] wrappedEdgeTime = new long[2][BUTTON_NAMES.length];
    private final long[][] pendingSince = new long[2][BUTTON_NAMES.length];
    private final int[] rawEdges = new int[2];
    private final int[] wrappedEdges = new int[2];
    private final int[] mismatchSamples = new int[2];
    private final int[] lagEvents = new int[2];
    private final double[] maxLagMs = new double[2];
    private final double[] lastLagMs = new double[2];
    private final double[] updateMs = new double[2];
    private final double[] maxUpdateMs = new double[2];
    private final int[] activeMismatches = new int[2];
    private final String[] lastEvent = {"None", "None"};
    private long lastLoopStart;
    private long lastTelemetry;
    private double loopPeriodMs;
    private double maxLoopPeriodMs;
    private double loopWorkMs;
    private double maxLoopWorkMs;
    private long loopCount;

    //========== Initialization =============================================
    @Override
    public void init() {
        extended[0] = new GamepadEx(gamepad1);
        extended[1] = new GamepadEx(gamepad2);
        telemetry.setMsTransmissionInterval(20);
        telemetry.addLine("No robot hardware initialized or commanded.");
        telemetry.addLine("Press/release buttons on both controllers after START.");
        telemetry.update();
    }

    @Override
    public void start() {
        resetMeasurements();
    }

    //========== Read Inputs and Measure ====================================
    @Override
    public void loop() {
        long loopStart = System.nanoTime();
        if (lastLoopStart != 0) {
            loopPeriodMs = (loopStart - lastLoopStart) / 1_000_000.0;
            maxLoopPeriodMs = Math.max(maxLoopPeriodMs, loopPeriodMs);
        }
        lastLoopStart = loopStart;
        loopCount++;

        for (int pad = 0; pad < 2; pad++) {
            boolean[] raw = rawButtons(pad);
            long rawTime = System.nanoTime();

            long updateStart = System.nanoTime();
            controls[pad].updateController(extended[pad]);
            long updateEnd = System.nanoTime();
            updateMs[pad] = (updateEnd - updateStart) / 1_000_000.0;
            maxUpdateMs[pad] = Math.max(maxUpdateMs[pad], updateMs[pad]);

            boolean[] wrapped = wrappedButtons(pad);
            long wrappedTime = System.nanoTime();
            activeMismatches[pad] = 0;

            for (int b = 0; b < BUTTON_NAMES.length; b++) {
                if (raw[b] != lastRaw[pad][b]) {
                    rawEdges[pad]++;
                    rawEdgeTime[pad][b] = rawTime;
                }
                if (wrapped[b] != lastWrapped[pad][b]) {
                    wrappedEdges[pad]++;
                    wrappedEdgeTime[pad][b] = wrappedTime;
                }
                if (raw[b] != wrapped[b]) {
                    activeMismatches[pad]++;
                    mismatchSamples[pad]++;
                    if (pendingSince[pad][b] == 0) {
                        pendingSince[pad][b] = rawTime;
                        lastEvent[pad] = BUTTON_NAMES[b] + " mismatch";
                    }
                } else if (pendingSince[pad][b] != 0) {
                    // A previously observed mismatch has now resolved.
                    double lag = (wrappedTime - pendingSince[pad][b]) / 1_000_000.0;
                    lastLagMs[pad] = lag;
                    maxLagMs[pad] = Math.max(maxLagMs[pad], lag);
                    lagEvents[pad]++;
                    lastEvent[pad] = BUTTON_NAMES[b] + " resolved";
                    pendingSince[pad][b] = 0;
                }
                lastRaw[pad][b] = raw[b];
                lastWrapped[pad][b] = wrapped[b];
            }
        }

        loopWorkMs = (System.nanoTime() - loopStart) / 1_000_000.0;
        maxLoopWorkMs = Math.max(maxLoopWorkMs, loopWorkMs);

        //========== Publish Telemetry ======================================
        long now = System.nanoTime();
        if (now - lastTelemetry >= TELEMETRY_INTERVAL_NS) {
            telemetry.addData("Loops", loopCount);
            telemetry.addData("Loop period (current/max)", "%.2f / %.2f ms", loopPeriodMs, maxLoopPeriodMs);
            telemetry.addData("Loop work (current/max)", "%.3f / %.3f ms", loopWorkMs, maxLoopWorkMs);
            for (int pad = 0; pad < 2; pad++) {
                telemetry.addLine("--- Gamepad " + (pad + 1) + " ---");
                telemetry.addData("UpdateController (current/max) " + (pad + 1),
                        "%.3f / %.3f ms", updateMs[pad], maxUpdateMs[pad]);
                telemetry.addData("Edges raw/wrapped " + (pad + 1),
                        "%d / %d", rawEdges[pad], wrappedEdges[pad]);
                telemetry.addData("Mismatches active/total " + (pad + 1),
                        "%d / %d", activeMismatches[pad], mismatchSamples[pad]);
                telemetry.addData("Resolved mismatches " + (pad + 1), lagEvents[pad]);
                telemetry.addData("Mismatch duration last/max " + (pad + 1),
                        "%.2f / %.2f ms", lastLagMs[pad], maxLagMs[pad]);
                telemetry.addData("Last event " + (pad + 1), lastEvent[pad]);
                telemetry.addData("A raw/wrapped " + (pad + 1),
                        "%b / %b", (pad == 0 ? gamepad1 : gamepad2).a, controls[pad].buttonX);
            }
            telemetry.addLine("Mismatch duration is sampling-based, NOT physical press latency.");
            telemetry.update();
            lastTelemetry = now;
        }
    }

    //========== Button Mappings ============================================
    private boolean[] rawButtons(int pad) {
        com.qualcomm.robotcore.hardware.Gamepad g = pad == 0 ? gamepad1 : gamepad2;
        return new boolean[]{g.a, g.b, g.x, g.y, g.left_bumper,
                g.right_bumper, g.dpad_up, g.dpad_down, g.dpad_left, g.dpad_right};
    }

    private boolean[] wrappedButtons(int pad) {
        Controller c = controls[pad];
        return new boolean[]{c.buttonX, c.buttonCircle, c.buttonSquare,
                c.buttonTriangle, c.bummerLeft, c.bummerRight,
                c.dpadUp, c.dpadDown, c.dpadLeft, c.dpadRight};
    }

    private void resetMeasurements() {
        lastLoopStart = 0;
        lastTelemetry = 0;
        loopCount = 0;
        maxLoopPeriodMs = 0;
        maxLoopWorkMs = 0;
        for (int p = 0; p < 2; p++) {
            rawEdges[p] = wrappedEdges[p] = mismatchSamples[p] = lagEvents[p] = 0;
            maxLagMs[p] = lastLagMs[p] = maxUpdateMs[p] = 0;
            lastEvent[p] = "None";
            boolean[] raw = rawButtons(p);
            controls[p].updateController(extended[p]);
            boolean[] wrapped = wrappedButtons(p);
            for (int b = 0; b < BUTTON_NAMES.length; b++) {
                lastRaw[p][b] = raw[b];
                lastWrapped[p][b] = wrapped[b];
                pendingSince[p][b] = 0;
            }
        }
    }
}
