package org.firstinspires.ftc.teamcode.tests.TurretTest;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.LyonLib.kinematics.Pose2d;

@Config
@TeleOp
public class TuretTest extends OpMode {

    miniRobot robot;
    Pose2d posRobot;

    @Override
    public void init(){

        robot = new miniRobot(hardwareMap, telemetry);
        posRobot = new Pose2d(0.0,0.0,0.0);


    }

    @Override
    public void loop(){

        robot.getTurret().loop();
        if (gamepad1.aWasPressed()){
            robot.getTurret().setWantedState(Turret_Subsystem.WantedState.TRACK_TARGET);
        }

        telemetry.update();

    }




}
