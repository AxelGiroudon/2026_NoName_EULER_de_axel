package org.firstinspires.ftc.teamcode.tests.TurretTest;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.LyonLib.kinematics.Pose2d;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class miniRobot {

    private Turret_Subsystem turret;
    private Pose2d posRobot;
    private Telemetry telemetry;

    public miniRobot (HardwareMap hmap, Telemetry telemetry){

        this.telemetry = telemetry;

        turret = new Turret_Subsystem(hmap, this);

    }

    public Telemetry getTelemetry(){return telemetry;}

    public Pose2d getRobotPos(){return posRobot;}
    public Turret_Subsystem getTurret(){return turret;}

}
