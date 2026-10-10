package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class TestIntake extends OpMode {
    private DcMotor intakeMotor;
    private double intakePower;

    @Override
    public void init()
    {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop()
    {
        intakePower = gamepad1.right_trigger - gamepad1.left_trigger;
        intakeMotor.setPower(intakePower);
    }
}
