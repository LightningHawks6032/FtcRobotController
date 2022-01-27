package org.firstinspires.ftc.teamcode6032.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode6032.drive.Pos;
import org.firstinspires.ftc.teamcode6032.drive.PosIntegrator;
import org.firstinspires.ftc.teamcode6032.hardware.CommonHardwareInit;
import org.firstinspires.ftc.teamcode6032.hardware.DuckSpinner;
import org.firstinspires.ftc.teamcode6032.hardware.GrabberArm;
import org.firstinspires.ftc.teamcode6032.hardware.HardwareManager;
import org.firstinspires.ftc.teamcode6032.hardware.MechanamMotors;

@TeleOp(name = "Drive")
public class ManualDriveTeleop extends OpMode {

    HardwareManager hardware;
    MechanamMotors mecha;
    GrabberArm arm;
    DuckSpinner duckSpinner;

    CommonHardwareInit chi;
    PosIntegrator posInt;

    private double grabberHeight = 0;
    private double lastT = 0;
    @Override
    public void init() {
        chi = new CommonHardwareInit(hardwareMap);
        hardware = chi.hardware;
        mecha = chi.mechanam;
        arm = new GrabberArm(hardware);
        duckSpinner = chi.duckSpinner;

        posInt = chi.posIntegrator;
        posInt.setCurrentPos(Pos.ORIGIN);
    }

    @Override
    public void loop() {
        final float fwd = gamepad1.left_stick_y;
        final float strafe = gamepad1.left_stick_x;
        final float rot = gamepad1.right_stick_x;
        final boolean slow = gamepad1.left_bumper;
        final double t = getRuntime();
        final double dt = t-lastT;
        lastT=t;


        final float grabberPosDelta = gamepad2.right_trigger-gamepad2.left_trigger;
        final boolean grabberClosed = gamepad2.a;
        final boolean duckSpinL = gamepad2.left_bumper;
        final boolean duckSpinR = gamepad2.right_bumper;
        final int duckSpin = (duckSpinL?1:0)-(duckSpinR?1:0);

        grabberHeight += grabberPosDelta*dt*0.25;
        grabberHeight = Math.min(Math.max(grabberHeight,0),1.7);

        final Pos vel = Pos.mul(
                new Pos(strafe, fwd, rot, -1),
                slow ? .3 : 1
        );

        mecha.setPower(vel);

        arm.setHeight(grabberHeight);
        arm.setOpen(!grabberClosed);
        duckSpinner.setEnabled(duckSpin);

        posInt.updatePos();
        Pos p = posInt.currentPos;
        telemetry.addLine("("+p.x+", "+p.y+") "+p.r+"rad");
    }
}
