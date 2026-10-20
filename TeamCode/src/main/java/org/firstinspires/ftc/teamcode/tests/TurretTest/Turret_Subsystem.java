package org.firstinspires.ftc.teamcode.tests.TurretTest;

import static org.firstinspires.ftc.teamcode.subsystems.localization.localizationConstants.BlueCentreHive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.LyonLib.kinematics.Pose2d;
import org.firstinspires.ftc.LyonLib.utils.UtilsRBL;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.localization.localizationConstants;

public class Turret_Subsystem {

    DcMotor turretMotor;
    miniRobot robot;

    ElapsedTime timer;
    Pose2d targetPos;

    Pose2d bluePublicCellePos = BlueCentreHive, blueOppositeCellPos,  redPublicCellePos, redOppositeCellPos;

    private double previousTime = 0.0;
    private double previousError = 0.0;

    private double turretPower;
    public static double turretKP = 0.00012;
    public static double turretKD = 0.0023;
    public static double turretKI = 0.0000000029;
    private double integrative = 0.0;
    private double turretTolerance = 50;

    double targetTurretMotor = 0.0; //in ticks
    double headingDegrees = 0.0;
    double headingEncodeur = 0.0;

    boolean firstIteration = true;
    boolean isInit = true;

    public final static double TICKS_PER_DEGREES = (double) 32768 /405;


    public enum WantedState{
        STAND_BY,
        TRACK_TARGET
    }
    public enum SystemState{
        IDLE,
        GOING_TO_TARGET,
        ALIGNED_WITH_TARGET,
    }
    private WantedState wantedState = WantedState.STAND_BY;
    private SystemState systemState = SystemState.IDLE;

    public void setWantedState(WantedState state){wantedState = state;}
    public SystemState getSystemState(){return systemState;}
    public void setTargetPos(Pose2d target){targetPos = target;}

    public Turret_Subsystem (HardwareMap hmap, miniRobot robot){

        this.robot = robot;

        turretMotor = hmap.get(DcMotor.class, "turret");

        turretMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        timer = new ElapsedTime();
        timer.startTime();
    }

    public void loop(){

        headingEncodeur = turretMotor.getCurrentPosition();
        headingDegrees = headingEncodeur/TICKS_PER_DEGREES;

        if (isInit)
        {
            RunStateMachine();
            switch (systemState)
            {
                case IDLE :
                    break;
                case GOING_TO_TARGET:
                case ALIGNED_WITH_TARGET:
                    setTargetWithDegrees(headingDegrees ); //TODO fix camera problem
                    goToTarget();
                    break;
                default:
                    robot.getTelemetry().addLine("turret system state isn't valid");
                    break;
            }

        }else
            this.init();

        robot.getTelemetry().addData("TargetPos", targetTurretMotor);
        robot.getTelemetry().addData("CurrentHeadingTicks", headingEncodeur);
        robot.getTelemetry().addData("CurrentHeadingDegrees", headingDegrees);
        robot.getTelemetry().addData("TurretMotorPower", turretPower);
        robot.getTelemetry().addData("Turret wanted state", wantedState);
        robot.getTelemetry().addData("Turret system state", systemState);
        robot.getTelemetry().update();
    }

    private void RunStateMachine(){
        switch (wantedState){
            case STAND_BY:
                systemState = SystemState.IDLE;
                break;
            case TRACK_TARGET:
                if (systemState == SystemState.IDLE)
                {
                    systemState = SystemState.GOING_TO_TARGET;
                }
                break;
            default:
                robot.getTelemetry().addLine("Please enter a valid turret Wanted state");
        }

        switch (systemState)
        {
            case IDLE:
                break;
            case GOING_TO_TARGET:
                if (UtilsRBL.isInRange(headingEncodeur, targetTurretMotor, turretTolerance))
                    systemState = SystemState.ALIGNED_WITH_TARGET;
                break;
            case ALIGNED_WITH_TARGET:
                break;
            default:
                robot.getTelemetry().addLine("turret system stae isn't valid");
                break;
        }
    }

    private void goToTarget(){
        double actualError = targetTurretMotor -headingEncodeur;
        double pTerm = actualError * turretKP;
        double actualTime = timer.milliseconds();

        double dTime = actualTime - previousTime;

        if (firstIteration) {
            previousError = actualError;
            firstIteration = false;
        }

        double dTerm = turretKD * (actualError-previousError) / dTime;

        integrative += actualError * dTime;
        double iTerm = turretKI * integrative;

        turretPower = pTerm + dTerm + iTerm;
        turretMotor.setPower(UtilsRBL.clamp(-turretPower, -1, 1));

        previousError = actualError;
        previousTime = actualTime;
    }

    public void init(){
        //TODO find with softeness the hall efect sensor
    }

    public void setTargetWithDegrees(double degrees){
        targetTurretMotor = degrees * TICKS_PER_DEGREES;
    }


}
