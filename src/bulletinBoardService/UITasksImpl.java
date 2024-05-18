package bulletinBoardService;

import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UITasksImpl implements UITasks {
    private JTextArea textArea;
    private JTextField textField;

    public UITasksImpl(JTextArea textArea, JTextField textField) {
        this.textArea = textArea;
        this.textField = textField;
    }

    @Override
    public String getMessage() {
        String message = textField.getText();
        textField.setText("");
        return message;
    }

    @Override
    public void setText(String txt) {
        SwingUtilities.invokeLater(() -> textArea.append(txt + "\n"));
    }
}

