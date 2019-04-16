package environments;

import body.Body;
import support.Constants;
import support.LaunchData;
import support.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GraphicalEnvironment extends Environment {
    private int sideLength = 900;
    private JPanel panel;
    private JButton pauseButton;

    private final JTextField zoomField;

    public GraphicalEnvironment(Body ...bodies) { this(new ArrayList<>(Arrays.asList(bodies))); }
    public GraphicalEnvironment(List<Body> bodies) {
        super(bodies);

        for (int i = 0; i < bodies.size(); i++)
            trails.add(new ArrayList<>());

        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBodies((Graphics2D) g);
            }
        };
        panel.setPreferredSize(new Dimension(sideLength, sideLength));
        panel.setBackground(Color.BLACK);

        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setContentPane(panel);

        JLabel zoomLabel = new JLabel("zoom: ");
        zoomLabel.setForeground(Color.WHITE);

        zoomField = new JTextField(Double.toString(zoom));
        zoomField.setPreferredSize(new Dimension(100, 20));

        JButton setZoomButton = new JButton("ok");
        setZoomButton.addActionListener(e -> setZoom(zoomField.getText()));

        JButton increaseZoomButton = new JButton("+");
        increaseZoomButton.addActionListener(e -> setZoom(zoom + 1));

        JButton increaseZoom100Button = new JButton("+100");
        increaseZoom100Button.addActionListener(e -> setZoom(zoom + 100));

        JButton decreaseZoomButton = new JButton("-");
        decreaseZoomButton.addActionListener(e -> setZoom(zoom - 1));

        JButton decreaseZoom100Button = new JButton("-100");
        decreaseZoom100Button.addActionListener(e -> setZoom(zoom - 100));

        pauseButton = new JButton("resume");
        pauseButton.addActionListener(e -> {
            if (running) stopThread();
            else run();
        });

        JCheckBox backwardsCheckBox = new JCheckBox("backwards");
        backwardsCheckBox.setForeground(Color.WHITE);
        backwardsCheckBox.addActionListener(e -> {
            timeStepInSeconds = -timeStepInSeconds;
            if (running) {
                stopThread();
                run();
            }
        });

        JButton clearTrailsButton = new JButton("clear trails");
        clearTrailsButton.addActionListener(e -> clearTrails());

        JButton targetPreviousBodyButton = new JButton("<");
        targetPreviousBodyButton.addActionListener(e -> retarget(targetBodyIndex - 1));

        JButton targetNextBodyButton = new JButton(">");
        targetNextBodyButton.addActionListener(e -> retarget(targetBodyIndex + 1));

        JCheckBox zTextCheckBox = new JCheckBox("z labels");
        zTextCheckBox.setForeground(Color.WHITE);
        zTextCheckBox.setSelected(drawZLabels);
        zTextCheckBox.addActionListener(e -> {
            drawZLabels = zTextCheckBox.isSelected();
            panel.repaint();
        });

        JCheckBox speedTextCheckBox = new JCheckBox("speed");
        speedTextCheckBox.setForeground(Color.WHITE);
        speedTextCheckBox.setSelected(drawSpeeds);
        speedTextCheckBox.addActionListener(e -> {
            drawSpeeds = speedTextCheckBox.isSelected();
            panel.repaint();
        });

        JLabel slowLabel = new JLabel("wait / frame (ms): ");
        slowLabel.setForeground(Color.WHITE);

        JTextField slowTextField = new JTextField();
        slowTextField.setPreferredSize(new Dimension(100, 20));

        JButton slowButton = new JButton("ok");
        slowButton.addActionListener(e -> {
            try {
                waitPerFrame = Integer.parseInt(slowTextField.getText());
            } catch (NumberFormatException e1) {
                slowTextField.setText("invalid");
            }
        });

        JCheckBox slowCheckBox = new JCheckBox("slow");
        slowCheckBox.setForeground(Color.WHITE);
        slowCheckBox.setSelected(doWait);
        slowCheckBox.addActionListener(e -> {
            doWait = slowCheckBox.isSelected();
        });

        frame.add(zoomLabel);
        frame.add(zoomField);
        frame.add(setZoomButton);
        frame.add(increaseZoomButton);
        frame.add(decreaseZoomButton);
        frame.add(increaseZoom100Button);
        frame.add(decreaseZoom100Button);
        frame.add(pauseButton);
        frame.add(backwardsCheckBox);
        frame.add(clearTrailsButton);
        frame.add(targetPreviousBodyButton);
        frame.add(targetNextBodyButton);
        frame.add(zTextCheckBox);
        frame.add(speedTextCheckBox);
        frame.add(slowLabel);
        frame.add(slowTextField);
        frame.add(slowButton);
        frame.add(slowCheckBox);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                clearTrails();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void launchShipFromEarthPrograde(LaunchData launchData) {
        // stopThread, add and startThread to avoid ConcurrentModificationException from "allBodies"
        // (it happens when iteratePhysics is running at the same time
        stopThread();
        super.launchShipFromEarthPrograde(launchData);
        synchronized (trails) {
            trails.add(new ArrayList<>());
        }
        run();
    }

    private static final double minZoom = 1;
    private static final double maxZoom = Double.POSITIVE_INFINITY;
    private double zoom = 3;


    private void setZoom(double newZoom) {
        if (newZoom == zoom)
            return;

        if (newZoom < minZoom)
            newZoom = minZoom;

        if (newZoom > maxZoom)
            newZoom = maxZoom;

        zoom = newZoom;
        zoomField.setText(Double.toString(zoom));
        clearTrails();
        panel.repaint();
    }

    private void setZoom(String newZoom) {
        try {
            setZoom(Double.parseDouble(newZoom));
        } catch (NumberFormatException e) {
            zoomField.setText("invalid");
        }
    }

    private int targetBodyIndex = 0;

    private void retarget(int newTargetBodyIndex) {
        targetBodyIndex = (newTargetBodyIndex + allBodies.size()) % allBodies.size();
        clearTrails();
        panel.repaint();
    }

    private void clearTrails() {
        for (List<Point> trail : trails)
            trail.clear();
    }

    private int timeStepInSeconds = 10;

    public void setTimeStep(int timeStepInSeconds) {
        this.timeStepInSeconds = timeStepInSeconds;
    }

    private double maxRadius = 1e13; // max distance from center in m

    private final List<List<Point>> trails = new ArrayList<>();

    private double toScreenCoordinate(double bodyCoordinate) {
        return bodyCoordinate / maxRadius * zoom * sideLength;
    }

    private boolean drawZLabels = true;
    private boolean drawSpeeds = true;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    private void drawBodies(Graphics2D g) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // draw time passed text
        g.setColor(Color.WHITE);
        String currentDateString = "currentDate: " + dateFormat.format(currentDate.getTime());
        String timePassedSeconds = String.format("%ds", secondsPassed());
        String timePassedYears = String.format("%.2fy", secondsPassed() / Constants.Time.YEAR);
        g.drawString(currentDateString, 10, panelHeight - 55);
        g.drawString("time passed", 10, panelHeight - 40);
        g.drawString(timePassedSeconds, 20, panelHeight - 25);
        g.drawString(timePassedYears, 20, panelHeight - 10);

        // draw points
        g.setColor(Color.WHITE);
        synchronized (trails) {
            for (List<Point> trail : trails)
                for (Point point : trail)
                    g.fillOval(point.x - 1, point.y - 1, 2, 2);
        }

        Vector referenceBodyPosition = allBodies.get(targetBodyIndex).getPosition();
        double referenceBodyScreenX = toScreenCoordinate(referenceBodyPosition.x());
        double referenceBodyScreenY = toScreenCoordinate(referenceBodyPosition.y());
        Vector referenceBodyVelocity = allBodies.get(targetBodyIndex).getVelocity();

        // draw allBodies
        int i = 0;
        synchronized (allBodies) {
            for (Body body : allBodies) {
                // draw body
                g.setColor(body.getColor());
                //            int size = (int) (body.getRadius() * 1000 / maxRadius * sideLength * zoom);
                double size = toScreenCoordinate(body.getRadius() * 2);

//                if (size > 20) size = 20;

                double screenX = toScreenCoordinate(body.getPosition().x());
                double screenY = toScreenCoordinate(body.getPosition().y());

                screenX += panelWidth / 2.0;
                screenY += panelHeight / 2.0;

                screenX -= referenceBodyScreenX;
                screenY -= referenceBodyScreenY;

                int fillX = (int) (screenX - size / 2);
                int fillY = (int) (screenY - size / 2);
                g.fillOval(fillX, fillY, (int) size, (int) size);

                if (size < 10) {
                    int drawX = (int) (screenX - 5);
                    int drawY = (int) (screenY - 5);
                    g.drawOval(drawX, drawY,10,10);
                }

                // draw name
                g.drawString(body.name, (int) screenX + 10, (int) screenY + 15);

                // draw relative z label
                if (i != targetBodyIndex) {
                    if (drawZLabels) {
                        Vector bodyPosition = body.getPosition();
                        // in km
                        double relativeX = (bodyPosition.x() - referenceBodyPosition.x()) / 1000.0;
                        double relativeY = (bodyPosition.y() - referenceBodyPosition.y()) / 1000.0;
                        double relativeZ = (bodyPosition.z() - referenceBodyPosition.z()) / 1000.0;
                        double distance = bodyPosition.vectorTo(referenceBodyPosition).magnitude();
                        double distance2d = bodyPosition.distance2dTo(referenceBodyPosition);

                        g.drawString(
                                String.format(
                                        Locale.US, "km: (%+.1e, %+.1e, %+.1e) distance 3d/2d: %.1e/%.1e",
                                        relativeX, relativeY, relativeZ, distance, distance2d
                                ),
                                (int) screenX + 10, (int) (screenY - size / 2)
                        );
                    }

                    if (drawSpeeds) {
                        double speed = body.getVelocity().vectorTo(referenceBodyVelocity).magnitude() / 1000.0;
                        // in km/s

                        g.drawString(
                                String.format(Locale.US, "%.1ekm/s", speed),
                                (int) screenX, (int) (screenY - (drawZLabels ? size / 2 + 15 : size / 2))
                        );
                    }
                }

                // add point to trail
                List<Point> trail = trails.get(i);
                trail.add(new Point((int) screenX, (int) screenY));
                if (trail.size() > 2000)
                    trail.remove(0);
                i++;
            }
        }
    }

    private boolean doWait = false;
    private int waitPerFrame = 1;

    private int repaintEvery = 1;

    public void setRepaintEvery(int repaintEvery) {
        this.repaintEvery = repaintEvery < 1 ? 1 : repaintEvery;
    }

    private long iterations = 0;

    @Override
    public void iteratePhysics(int timeStepInSeconds) {
        synchronized (allBodies) {
            super.iteratePhysics(timeStepInSeconds);
        }

        iterations++;
        if (iterations % repaintEvery == 0) {
            panel.repaint();

            if (doWait) {
                try {
                    Thread.sleep(waitPerFrame);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    @Override
    public void startThread(int timeStepInSeconds) {
        super.startThread(timeStepInSeconds);
        pauseButton.setText("pause");
    }

    public void run() { startThread(timeStepInSeconds); }

    @Override
    public void stopThread() {
        super.stopThread();
        pauseButton.setText("resume");
    }
}
