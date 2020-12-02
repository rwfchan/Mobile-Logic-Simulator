package org.ecs160.a2;

public class InputPin extends Gate {
    private static int id = 0;

    public InputPin(Slot s) {
        super(s);
        super.setName("InputPin");

        label = makeLabel(this.getName(), id++);

        super.offImage = AppMain.theme.getImage("inputpin_0.jpg");
        super.onImage = AppMain.theme.getImage("inputpin_1.jpg");
        super.currentImage = offImage;
        // label = makeLabel();
        name = getLabelName();

        inputs.clear();
        outputs.add(new Output(this));

        //No inputs allowed to an input pin
        inputLimit = 0;
        minInputs = 0;

        gateType = GateType.INPUT_PIN;
        //Unlike other gates, inputs start out at false
        state = State.ZERO;
    }

    @Override
    public void calculate() {
    }

    public void toggle() {
        if (state == State.ZERO) {
            state = State.ONE;
            setImage();
        } else if (state == State.ONE) {
            state = State.ZERO;
            setImage();
        }
    }
}