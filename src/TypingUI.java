import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TypingUI extends JFrame implements KeyListener {
    private final Statistic statistic;
    private long lastTime;
    private JLabel textDisplay;
    private JLabel timerDisplay;
    private JLabel wordCountDisplay;
    private JLabel statisticsDisplay;
    private JButton restartButton;
    private static final int numberOfWordsInLine = 13;
    private static String text = "";
    private int position = 0;
    private int charCount = 0;
    private int timeRemaining;
    private int elapsedTime = 0;
    private final int timeToType = 60;
    private Timer timer;
    private TimerTask task;
    private boolean isWaitingToStart = true;

    public TypingUI() {
        super("Touch Typing Practice");
        statistic = new Statistic();
        initializeText();
        createUI();
        restartTest();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(800, 400));
        this.centerFrame();
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);
    }

    private static void initializeText() {
        if (!Objects.equals(text, ""))
            return;
        text = "";
        for (int i = 0; i < numberOfWordsInLine; i++)
            text += TextGenerator.getRandomWord() + " ";
    }

    private void centerFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
    }

    private void createUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.getContentPane().setBackground(new Color(240, 240, 240));
        this.setPadding(this.getContentPane(), 10);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        JPanel centerPanel2 = new JPanel(new GridBagLayout());
        centerPanel2.setOpaque(false);
        textDisplay = new JLabel("<html>" + colorText(text, 0, true) + "</html>");
        textDisplay.setFont(new Font("Arial", Font.PLAIN, 18));

        timerDisplay = new JLabel("Time remaining: " + timeRemaining + "s");
        timerDisplay.setFont(new Font("Arial", Font.PLAIN, 18));
        timerDisplay.setForeground(new Color(60, 60, 60));

        wordCountDisplay = new JLabel("Type to start");
        wordCountDisplay.setFont(new Font("Arial", Font.PLAIN, 18));
        wordCountDisplay.setForeground(new Color(60, 60, 60));

        centerPanel.add(textDisplay, new GridBagConstraints());

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(timerDisplay);
        topPanel.add(wordCountDisplay);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 18));
        restartButton.addActionListener(e -> restartTest());
        topPanel.add(restartButton);

        statisticsDisplay = new JLabel("Letter statistics will be displayed here.");
        statisticsDisplay.setFont(new Font("Arial", Font.PLAIN, 8));
        statisticsDisplay.setForeground(new Color(60, 60, 60));
        centerPanel2.add(statisticsDisplay, new GridBagConstraints());

        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(centerPanel2, BorderLayout.SOUTH);
    }


    private void setPadding(Container container, int padding) {
        if (container.getLayout() instanceof BorderLayout) {
            ((BorderLayout) container.getLayout()).setHgap(padding);
            ((BorderLayout) container.getLayout()).setVgap(padding);
        }
    }

    private void startTimer() {
        lastTime = System.currentTimeMillis();
        timeRemaining = timeToType;
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    elapsedTime++;
                    displayHeader();
                } else {
                    timer.cancel();
                    JOptionPane.showMessageDialog(null, "Time's up! You typed " + calculateWPM(elapsedTime) + " words per minute.");
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    private void restartTest() {
        isWaitingToStart = true;
        if (timer != null)
            timer.cancel();
        charCount = 0;
        position = 0;
        initializeText();
        displayHeader();
        this.requestFocusInWindow();
    }

    private String colorText(String text, int position, boolean isCorrect) {
        String before = text.substring(0, position);
        int currentIndex = Math.min(position + 1, text.length());
        String current = text.substring(position, currentIndex);
        String after = text.substring(currentIndex);
        if (isCorrect) {
            return before + "<span style='background-color:rgb(100, 100, 100); color:white;'>" + current + "</span>" + after;
        }
        return before + "<span style='background-color:rgb(200, 0, 0); color:white;'>" + current + "</span>" + after;
    }

    private void updateWPM() {
        int wpm = calculateWPM(elapsedTime);
        wordCountDisplay.setText("Words per minute: " + wpm);
    }

    private int calculateWPM(int elapsedTime) {
        if (elapsedTime == 0) return 0;
        return (int) (((double) charCount / 5) / elapsedTime * 60);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (isWaitingToStart) {
            isWaitingToStart = false;
            lastTime = 0;
            startTimer();
        }
        if (position < text.length() && timeRemaining > 0) {
            char typedChar = e.getKeyChar();
            long currentTime = System.currentTimeMillis();
            if (typedChar == text.charAt(position)) {
                if (lastTime != 0) {
                    long timeTaken = currentTime - lastTime;
                    statistic.updateStats(typedChar, timeTaken);
                    displayStatistics();
                }
                lastTime = currentTime;
                charCount++;
                if (typedChar == ' ') {
                    text += TextGenerator.getRandomWord() + " ";
                }
                textDisplay.setText("<html>" + colorText(text, position + 1, true) + "</html>");
                if (typedChar == ' ') {
                    text = text.substring(position + 1);
                    position = -1;
                }
                position++;
            } else {
                textDisplay.setText("<html>" + colorText(text, position, false) + "</html>");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // No action needed here
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed here
    }

    public static void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(TypingUI::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayHeader() {
        if (isWaitingToStart) {
            timerDisplay.setText("Time remaining: " + timeRemaining + "s");
            wordCountDisplay.setText("Type to start");
            textDisplay.setText("<html>" + colorText(text, 0, true) + "</html>");
            return;
        }
        timerDisplay.setText("Time remaining: " + timeRemaining + "s");
        updateWPM();
    }

    private void displayStatistics() {
        StringBuilder statsBuilder = new StringBuilder("<html>");
        for (char letter = 'a'; letter <= 'z'; letter++) {
            double avgTime = statistic.getStats(letter);
            if (avgTime > 0) {
                double wpm = (60.0 / (avgTime / 1000.0)) / 5.0;
                statsBuilder.append(letter).append(": ").append(String.format("%.1f", wpm)).append(" ");
            }
        }
        statsBuilder.append("</html>");
        statisticsDisplay.setText(statsBuilder.toString());
    }

    public static void main(String[] args) {
        start();
    }
}
