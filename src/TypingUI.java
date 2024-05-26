import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class TypingUI extends JFrame implements KeyListener {
    private JLabel textDisplay;
    private JLabel timerDisplay;
    private JLabel wordCountDisplay;
    private JButton restartButton;
    private static final int numberOfWordsInLine = 13;
    private static String text = "";
    private int position = 0;
    private int charCount = 0;
    private int timeRemaining = 60; // 60 seconds
    private Timer timer;
    private TimerTask task;

    public TypingUI() {
        super("Touch Typing Practice");
        initializeText();
        createUI();
        startTimer();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(800, 400));
        this.centerFrame();
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);
    }

    private static void initializeText() {
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
        textDisplay = new JLabel("<html>" + colorText(text, 0, true) + "</html>");
        textDisplay.setFont(new Font("Arial", Font.PLAIN, 18));

        timerDisplay = new JLabel("Time remaining: " + timeRemaining + "s");
        timerDisplay.setFont(new Font("Arial", Font.PLAIN, 18));
        timerDisplay.setForeground(new Color(60, 60, 60));

        wordCountDisplay = new JLabel("Words per minute: 0");
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

        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    private void setPadding(Container container, int padding) {
        if (container.getLayout() instanceof BorderLayout) {
            ((BorderLayout) container.getLayout()).setHgap(padding);
            ((BorderLayout) container.getLayout()).setVgap(padding);
        }
    }

    private void startTimer() {
        timer = new Timer();
        task = new TimerTask() {
            private int elapsedTime = 0;

            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    elapsedTime++;
                    timerDisplay.setText("Time remaining: " + timeRemaining + "s");
                    updateWPM(elapsedTime);
                } else {
                    timer.cancel();
                    JOptionPane.showMessageDialog(null, "Time's up! You typed " + calculateWPM(elapsedTime) + " words per minute.");
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    private void restartTest() {
        timer.cancel();
        timeRemaining = 60;
        charCount = 0;
        position = 0;
        initializeText();
        timerDisplay.setText("Time remaining: " + timeRemaining + "s");
        wordCountDisplay.setText("Words per minute: 0");
        textDisplay.setText("<html>" + colorText(text, 0, true) + "</html>");
        startTimer();
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

    private void updateWPM(int elapsedTime) {
        int wpm = calculateWPM(elapsedTime);
        wordCountDisplay.setText("Words per minute: " + wpm);
    }

    private int calculateWPM(int elapsedTime) {
        if (elapsedTime == 0) return 0;
        return (int) (((double) charCount / 5) / elapsedTime * 60);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed here
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // No action needed here
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (position < text.length() && timeRemaining > 0) {
            char typedChar = e.getKeyChar();
            if (typedChar == text.charAt(position)) {
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

    public static void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(TypingUI::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        start();
    }
}
