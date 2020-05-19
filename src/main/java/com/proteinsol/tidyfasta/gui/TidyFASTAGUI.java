package com.proteinsol.tidyfasta.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.proteinsol.tidyfasta.packages.FASTAObject;
import com.proteinsol.tidyfasta.packages.ReadFASTAAndFormat;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class TidyFASTAGUI extends JFrame {
    private JPanel mainPanel;
    private JTextArea FASTAInputArea;
    private JTextArea FASTAOutputArea;
    private JLabel numSubmitted;
    private JLabel numValid;
    private JButton openButton;
    private JButton submitButton;
    private JButton copyButton;
    private JButton saveButton;
    private JPanel InputButtonPanel;
    private JPanel InputTitlePanel;
    private JSplitPane splitPane;
    private JScrollPane OutputScroll;
    private JPanel InputPane;
    private JPanel OutputPane;
    private JScrollPane InputFASTAPane;
    private JPanel OutputButtonPanel;
    private JPanel OutputTitlePanel;
    private static String ValidFASTA;
    private StringBuilder ErrorMessage;

    public TidyFASTAGUI(String title) {
        super(title);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        submitButton.addActionListener(e -> {
            clearOutput();
            SubmitSequence();
        });

        copyButton.addActionListener(e -> CopyValidFASTAToClipboard());

        openButton.addActionListener(e -> openFile());

        saveButton.addActionListener(e -> {
            if (ValidFASTA != null) {
                writeFile();
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                        "Submit file before saving.");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new TidyFASTAGUI("Tidy FASTA");
        frame.setVisible(true);
    }

    public void clearOutput() {
        numValid.setText("Number of valid sequences: ");
        numSubmitted.setText("Sequences: ");
        FASTAOutputArea.setText("");
    }

    public void SubmitSequence() {
        String submittedSequences = FASTAInputArea.getText();

        if (submittedSequences.length() == 0) {
            JOptionPane.showMessageDialog(mainPanel,
                    "No sequence submitted");
            return;
        }

        ReadFASTAAndFormat FASTA = new ReadFASTAAndFormat(submittedSequences);

        if (ContinueWithAnalysis(FASTA)) {
            numValid.setText("Number of valid sequences: " + FASTA.getValidatedNumber());
            numSubmitted.setText("Sequences: " + FASTA.getSubmittedNumber());

            StringBuilder FASTAOutputText = new StringBuilder();

            for (FASTAObject validFasta : FASTA.getArrayFASTA()) {
                FASTAOutputText.append(validFasta.ID);
                FASTAOutputText.append("\n");
                FASTAOutputText.append(validFasta.Sequence);
                FASTAOutputText.append("\n\n");
            }

            if (FASTAOutputText.length() > 2) {
                FASTAOutputText.setLength(FASTAOutputText.length() - 2);
            } else {
                FASTAOutputText.setLength(0);
            }

            ValidFASTA = FASTAOutputText.toString();
            FASTAOutputArea.setText(ValidFASTA);
        }
        ;
    }

    public boolean ContinueWithAnalysis(ReadFASTAAndFormat FASTA) {

        if (FASTA.getNumErrors() > 0) {
            BuildErrorMessage(FASTA);
            return TestUserDesire(FASTA);
        } else {
            return true;
        }

    }

    public void CopyValidFASTAToClipboard() {
        StringSelection ValidFASTASelect = new StringSelection(ValidFASTA);
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(ValidFASTASelect, null);
    }

    public void openFile() {

        final JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "FASTA/TEXT FILES", "txt", "text", "fasta", "fa", "FASTA");
        chooser.setFileFilter(filter);
        int chooserResult = chooser.showOpenDialog(mainPanel);

        File FASTAFile;
        if (chooserResult == JFileChooser.APPROVE_OPTION) {
            FASTAFile = chooser.getSelectedFile();
            FASTAInputArea.setText("");
            FASTAOutputArea.setText("");
        } else {
            return;
        }

        List<String> lines = Collections.emptyList();
        Path FASTAFileName = FASTAFile.toPath();

        //refactor
        try {
            lines = Files.readAllLines(FASTAFileName, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(mainPanel,
                    FASTAFileName.toString() + " not found");
        }

        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            result.append(line);
            result.append("\n");
        }
        result.setLength(result.length() - 1);

        FASTAInputArea.setText(result.toString());
    }

    public void writeFile() {
        JFileChooser saveChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "FASTA/TEXT FILES", "txt", "text", "fasta", "fa", "FASTA");
        saveChooser.setFileFilter(filter);
        saveChooser.setDialogTitle("Specify a location to save");
        saveChooser.setSelectedFile(new File("tidyfasta.fasta"));

        int userSelection = saveChooser.showSaveDialog(mainPanel);

        File fileToSave;
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            fileToSave = saveChooser.getSelectedFile();
        } else {
            return;
        }

        String fileString = fileToSave.toString();

        if (!fileString.contains(".")) {
            fileString = fileString + ".fasta";
        }

        Path saveFileName = Paths.get(fileString);

        if (Files.exists(saveFileName)) {

            int dialogResult = JOptionPane.showConfirmDialog(mainPanel,
                    saveFileName.toString() + " already exists." +
                            "\nWould you like to overwrite it?",
                    "FASTA Errors found",
                    JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.NO_OPTION) {
                return;
            }
        }

        try {
            Files.write(saveFileName, Collections.singleton(ValidFASTA));
            JOptionPane.showMessageDialog(mainPanel,
                    saveFileName.toString() + " saved!");
        } catch (IOException err) {
            JOptionPane.showMessageDialog(mainPanel,
                    saveFileName.toString() + " not writable.");
        }
    }

    public void BuildErrorMessage(ReadFASTAAndFormat FASTA) {

        StringBuilder userErrorMessage = new StringBuilder();

        if (FASTA.getValidatedNumber() > 0) {
            userErrorMessage.append(FASTA.getNumErrors());
            userErrorMessage.append("/");
            userErrorMessage.append(FASTA.getSubmittedNumber());
            userErrorMessage.append(" of the sequences submitted contained errors.\n\n");
        }

        if (FASTA.getNumErrors() == 1) {
            userErrorMessage.append("The following error occurred\n");
            userErrorMessage.append(FASTA.getErrMsg());
            userErrorMessage.append("\n");
        } else if (FASTA.getNumErrors() <= 10) {
            userErrorMessage.append("The following errors occurred\n");
            userErrorMessage.append(FASTA.getErrMsg());
            userErrorMessage.append("\n");
        } else {
            if (FASTA.getValidatedNumber() == 0) {
                userErrorMessage.append("All ");
                userErrorMessage.append(FASTA.getNumErrors());
                userErrorMessage.append(" sequences contained errors, the first 10 errors were:\n\n");
            } else {
                userErrorMessage.append("The first 10 errors were:\n\n");
            }

            String[] errorArray = FASTA.getErrMsgArray();
            for (int i = 0; i < 10; i++) {
                userErrorMessage.append(errorArray[i]).append("\n");
            }
            userErrorMessage.append("\n");
        }

        this.ErrorMessage = userErrorMessage;
    }

    public boolean TestUserDesire(ReadFASTAAndFormat FASTA) {
        if (FASTA.getValidatedNumber() > 0) {

            String ErrorDialog = this.ErrorMessage.toString() + "\nWould you like to continue?";

            int dialogResult = JOptionPane.showConfirmDialog(mainPanel,
                    ErrorDialog,
                    "FASTA Errors found",
                    JOptionPane.YES_NO_OPTION);

            return dialogResult != JOptionPane.NO_OPTION;

        } else {
            JOptionPane.showMessageDialog(mainPanel, this.ErrorMessage.toString());
            return false;
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        splitPane = new JSplitPane();
        splitPane.setEnabled(true);
        splitPane.setResizeWeight(0.5);
        mainPanel.add(splitPane, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        InputPane = new JPanel();
        InputPane.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane.setLeftComponent(InputPane);
        InputButtonPanel = new JPanel();
        InputButtonPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        InputPane.add(InputButtonPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        openButton.setMnemonic('O');
        openButton.setDisplayedMnemonicIndex(0);
        openButton.setToolTipText("");
        InputButtonPanel.add(openButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        submitButton = new JButton();
        submitButton.setText("Submit");
        submitButton.setMnemonic('S');
        submitButton.setDisplayedMnemonicIndex(0);
        InputButtonPanel.add(submitButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        InputTitlePanel = new JPanel();
        InputTitlePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        InputPane.add(InputTitlePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Raw FASTA");
        label1.setDisplayedMnemonic('R');
        label1.setDisplayedMnemonicIndex(0);
        InputTitlePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        InputFASTAPane = new JScrollPane();
        InputPane.add(InputFASTAPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        FASTAInputArea = new JTextArea();
        FASTAInputArea.setLineWrap(true);
        InputFASTAPane.setViewportView(FASTAInputArea);
        numSubmitted = new JLabel();
        numSubmitted.setText("Sequences:");
        numSubmitted.setDisplayedMnemonic('E');
        numSubmitted.setDisplayedMnemonicIndex(1);
        InputPane.add(numSubmitted, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OutputPane = new JPanel();
        OutputPane.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane.setRightComponent(OutputPane);
        OutputButtonPanel = new JPanel();
        OutputButtonPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        OutputPane.add(OutputButtonPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        saveButton.setMnemonic('A');
        saveButton.setDisplayedMnemonicIndex(1);
        OutputButtonPanel.add(saveButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setText("Copy");
        copyButton.setMnemonic('C');
        copyButton.setDisplayedMnemonicIndex(0);
        OutputButtonPanel.add(copyButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OutputTitlePanel = new JPanel();
        OutputTitlePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        OutputPane.add(OutputTitlePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Validated FASTA");
        label2.setDisplayedMnemonic('V');
        label2.setDisplayedMnemonicIndex(0);
        OutputTitlePanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OutputScroll = new JScrollPane();
        OutputPane.add(OutputScroll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        FASTAOutputArea = new JTextArea();
        FASTAOutputArea.setEditable(false);
        FASTAOutputArea.setEnabled(true);
        FASTAOutputArea.setLineWrap(true);
        FASTAOutputArea.setText("");
        OutputScroll.setViewportView(FASTAOutputArea);
        numValid = new JLabel();
        numValid.setText("Valid Sequences:");
        numValid.setDisplayedMnemonic('L');
        numValid.setDisplayedMnemonicIndex(2);
        OutputPane.add(numValid, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, 5), null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 0, false));
        final Spacer spacer4 = new Spacer();
        mainPanel.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 0, false));
        label1.setLabelFor(FASTAInputArea);
        numSubmitted.setLabelFor(InputFASTAPane);
        label2.setLabelFor(FASTAOutputArea);
        numValid.setLabelFor(OutputScroll);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
