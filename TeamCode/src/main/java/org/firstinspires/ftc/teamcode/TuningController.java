package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BioBuzzV01.Teleop;

@Config
public class TuningController {
    public static double MOTOR_TICKS_PER_REV = 28;
    public static double MOTOR_MAX_RPM = 5400;
    public static double MOTOR_GEAR_RATIO = 1; // output (wheel) speed / input (motor) speed

    public static double TESTING_MAX_SPEED = 0.6 * MOTOR_MAX_RPM;
    public static double TESTING_MIN_SPEED = 0.2 * MOTOR_MAX_RPM;


    // These are prefixed with "STATE1", "STATE2", etc. because Dashboard displays variables in
    // alphabetical order. Thus, we preserve the actual order of the process
    // Then we append Z just because we want it to show below the MOTOR_ and TESTING_ because
    // these settings aren't as important
    public static double ZSTATE1_RAMPING_UP_DURATION = 3.5;
    public static double ZSTATE2_COASTING_1_DURATION = 4;
    public static double ZSTATE3_RAMPING_DOWN_DURATION = 2;
    public static double ZSTATE4_COASTING_2_DURATION = 2;
    public static double ZSTATE5_RANDOM_1_DURATION = 2;
    public static double ZSTATE6_RANDOM_2_DURATION = 2;
    public static double ZSTATE7_RANDOM_3_DURATION = 2;
    public static double ZSTATE8_REST_DURATION = 1;

    public double target = 0;


    enum TestState {
        START,
        RAMPING_UP,
        COASTING_1,
        RAMPING_DOWN,
        COASTING_2,
        RANDOM_1,
        RANDOM_2,
        RANDOM_3,
        REST
    }

//    private StateMachine stateMachine;
    TestState testState = TestState.REST;
    ElapsedTime SM_Timer = new ElapsedTime();
//    private ElapsedTime externalTimer = new ElapsedTime();

    private double currentTargetVelo = 0.0;

//    public TuningController() {
        public double update() {

//        stateMachine = new StateMachineBuilder<State>()
            switch (testState) {
                case START:
                    SM_Timer.reset();
                    testState = testState.RAMPING_UP;
                case RAMPING_UP:
                    double progress = SM_Timer.seconds() / ZSTATE1_RAMPING_UP_DURATION;
                    double target = progress * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED;
                    currentTargetVelo = rpmToTicksPerSecond(target);
                    if (SM_Timer.seconds() >= ZSTATE1_RAMPING_UP_DURATION) {
                        SM_Timer.reset();
                        testState = testState.COASTING_1;
                    }
                    break;
                case COASTING_1:
                    target = TESTING_MAX_SPEED;
                    currentTargetVelo = rpmToTicksPerSecond(target);
                    if (SM_Timer.seconds() >= ZSTATE2_COASTING_1_DURATION) {
                        SM_Timer.reset();
                        testState = testState.RAMPING_DOWN;
                    }
                    break;
                case RAMPING_DOWN:
                    progress = SM_Timer.seconds() / ZSTATE3_RAMPING_DOWN_DURATION;
                    target = TESTING_MAX_SPEED - progress * (TESTING_MAX_SPEED - TESTING_MIN_SPEED);
                    currentTargetVelo = rpmToTicksPerSecond(target);
                    if (SM_Timer.seconds() >= ZSTATE3_RAMPING_DOWN_DURATION) {
                        SM_Timer.reset();
                        testState = testState.COASTING_2;
                    }
                    break;

                case COASTING_2:
                    currentTargetVelo = rpmToTicksPerSecond(TESTING_MIN_SPEED);
                    if (SM_Timer.seconds() >= ZSTATE4_COASTING_2_DURATION) {
//                        currentTargetVelo = rpmToTicksPerSecond(Math.random() * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED);
                        currentTargetVelo = rpmToTicksPerSecond(0.3 * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED);
                        SM_Timer.reset();
                        testState = testState.RANDOM_1;
                    }
                    break;
                case RANDOM_1:
                    if (SM_Timer.seconds() >= ZSTATE5_RANDOM_1_DURATION) {
//                        currentTargetVelo = rpmToTicksPerSecond(Math.random() * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED);
                        currentTargetVelo = rpmToTicksPerSecond(0.5 * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED);
                        SM_Timer.reset();
                        testState = testState.RANDOM_2;
                    }
                    break;
                case RANDOM_2:
                    if (SM_Timer.seconds() >= ZSTATE6_RANDOM_2_DURATION) {
//                        currentTargetVelo = rpmToTicksPerSecond(Math.random() * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED);
                        currentTargetVelo = rpmToTicksPerSecond(0.7 * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED);
                        SM_Timer.reset();
                        testState = testState.RANDOM_3;
                    }
                    break;
                case RANDOM_3:
                    if (SM_Timer.seconds() >= ZSTATE7_RANDOM_3_DURATION) {
                        currentTargetVelo = 0;
                        SM_Timer.reset();
                        testState = testState.REST;
                    }
                    break;

                case REST:
                    if (SM_Timer.seconds() >= ZSTATE8_REST_DURATION) {
                        SM_Timer.reset();
                        testState = testState.START;
                    }
                    break;

                default:
                    testState = testState.START;
            }
            /*
                .state(State.RAMPING_UP)
                    .transitionTimed(ZSTATE1_RAMPING_UP_DURATION)
                    .onEnter(externalTimer::reset)
                    .loop(() -> {
                        double progress = externalTimer.seconds() / ZSTATE1_RAMPING_UP_DURATION;
                        double target = progress * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED;

                        currentTargetVelo = rpmToTicksPerSecond(target);
                    })

                    .state(State.COASTING_1)
                    .transitionTimed(ZSTATE2_COASTING_1_DURATION)
                    .onEnter(() -> currentTargetVelo = rpmToTicksPerSecond(TESTING_MAX_SPEED))

                    .state(State.RAMPING_DOWN)
                    .transitionTimed(ZSTATE3_RAMPING_DOWN_DURATION)
                    .onEnter(externalTimer::reset)
                    .loop(() -> {
                        double progress = externalTimer.seconds() / ZSTATE3_RAMPING_DOWN_DURATION;
                        double target = TESTING_MAX_SPEED - progress * (TESTING_MAX_SPEED - TESTING_MIN_SPEED);

                        currentTargetVelo = rpmToTicksPerSecond(target);
                    })

                    .state(State.COASTING_2)
                    .transitionTimed(ZSTATE4_COASTING_2_DURATION)
                    .onEnter(() -> currentTargetVelo = rpmToTicksPerSecond(TESTING_MIN_SPEED))

                    .state(State.RANDOM_1)
                    .transitionTimed(ZSTATE5_RANDOM_1_DURATION)
                    .onEnter(() -> currentTargetVelo = rpmToTicksPerSecond(Math.random() * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED))

                    .state(State.RANDOM_2)
                    .transitionTimed(ZSTATE6_RANDOM_2_DURATION)
                    .onEnter(() -> currentTargetVelo = rpmToTicksPerSecond(Math.random() * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED))

                    .state(State.RANDOM_3)
                    .transitionTimed(ZSTATE7_RANDOM_3_DURATION)
                    .onEnter(() -> currentTargetVelo = rpmToTicksPerSecond(Math.random() * (TESTING_MAX_SPEED - TESTING_MIN_SPEED) + TESTING_MIN_SPEED))

                    .state(State.REST)
                    .transitionTimed(ZSTATE8_REST_DURATION)
                    .onEnter(() -> currentTargetVelo = 0)

                    .exit(State.RAMPING_UP)

                    .build();
        }
//        stateMachine.setLooping(true);
             */
            return currentTargetVelo;
    }


    public void start() {
        SM_Timer.reset();

    }

//    public double update() {
//        stateloop();
//
//        return currentTargetVelo;
//    }

    public static double rpmToTicksPerSecond(double rpm) {
        return rpm * MOTOR_TICKS_PER_REV / MOTOR_GEAR_RATIO / 60;
    }
}
