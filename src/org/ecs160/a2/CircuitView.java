package org.ecs160.a2;

import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.spinner.Picker;

import java.util.ArrayList;

enum UserMode {
    WIRE,
    DELETE,
    EDIT,
    PDELAY,
    RUNNING
}

public class CircuitView extends Container {
    private Picker pDelayPicker = new Picker();
    String pDelay = "0";


    UserViewForm simulator;

    public static UserMode mode;
    public static Wire wire;
    public final static Container circuitBoardContainer = new Container(new GridLayout(10, 10));
    public static ArrayList<Slot> slots = new ArrayList<Slot>();

    private static Container appLayout = new Container(new BorderLayout());
    private static Container labelLayout = new Container(new LayeredLayout());
    private static Container wireLayout = new Container(new LayeredLayout());

    public CircuitBoard circuitBoard;

    CircuitView(UserViewForm _simulator_, CircuitBoard circuitBoard) {
        super(new BoxLayout(BoxLayout.Y_AXIS));
        simulator = _simulator_;
        this.circuitBoard = circuitBoard;

        initCircuitView();
    }

    private void initCircuitView() {
        mode = UserMode.EDIT;
        wire = new Wire(wireLayout);

        this.setLayout(new LayeredLayout());
        add(labelLayout);
        add(wireLayout);
        add(appLayout);

        // To initialize the circuitBoard, place empty slots to all of them
        for (int i = 0; i < 100; i++) {
            slots.add(new Slot());
            Slot s = slots.get(slots.size()-1).getSlot();
            circuitBoardContainer.addComponent(s);
            s.setId(i);
            s.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    evt.consume();

                    // We only let user edit the wire if there is component in it
                    if (mode == UserMode.WIRE && !s.isEmpty()) {
                        wire.addConnection(s);
                        simulator.show();

                        // Technically we don't need to check if deleting slot is empty but just for consistency
                    } else if (mode == UserMode.DELETE && !s.isEmpty()) {
                        circuitBoard.removeGate(s.getGate());
                        s.emptySlot();
                        simulator.show();
                    } else if (mode == UserMode.RUNNING && !s.isEmpty()) {
                        if (s.getGate().gateType == GateType.INPUT_PIN) {
                            circuitBoard.toggleInput(s.getGate().getLabelName()); //TODO
                            simulator.show();
                        }
                    } else if (mode == UserMode.PDELAY && !s.isEmpty()) {
                        System.out.println("Prompting PDelay choice");
                        System.out.println("Retrieved custome PDelay for user");
                        System.out.println("Modifying PDelay");
                        // TODO: Prompt PDelay choice and switch current gate's PDelay
                        // TODOPDELAY START
                        pDelayPicker.setType(Display.PICKER_TYPE_STRINGS);
                        String [] delay_array = new String[100];
                        for (int a = 0; a < delay_array.length; a++) {
                            delay_array[a] = Integer.toString(a);
                        }
                        pDelayPicker.setStrings(delay_array);
                        pDelayPicker.addActionListener((event1)->{
                            pDelay = pDelayPicker.getSelectedString();
                        });
                        simulator.menuDisplay.removeComponent(pDelayPicker);
                        simulator.menuDisplay.revalidate();
                        simulator.show();
                        // TODOPDELAY END
                    }
                }
            });
        }

        appLayout.addComponent(BorderLayout.CENTER, circuitBoardContainer);
    }

    public Container getLabelLayout() {
        return labelLayout;
    }

    static public void addLabel(LabelComponent name) {
        labelLayout.addComponent(name);
    }
    static public void moveLabel(LabelComponent from, LabelComponent to) {
        labelLayout.replace(from, to, null);
    }

    static public void enableDrag(ArrayList<Slot> slots) {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).turnOnDrag();
        }
        CircuitView.wire.clearConnection();
    }

    static public void disableDrag(ArrayList<Slot> slots) {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).turnOffDrag();
        }
    }

    static public Slot findSlot(ArrayList<Slot> slots, int id) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getId() == id) {
                return slots.get(i);
            }
        }
        return null;
    }

    public Container[] getContainers() {
        return new Container[] {appLayout, labelLayout, wireLayout};
    }

    public void swapView() {
        mode = UserMode.PDELAY;
        disableDrag(slots);
        removeComponent(appLayout);
        removeComponent(labelLayout);
        removeComponent(wireLayout);
    }

    public void toView() {
        mode = UserMode.EDIT;
        enableDrag(slots);
        add(labelLayout);
        add(wireLayout);
        add(appLayout);
        simulator.show();
    }

}
