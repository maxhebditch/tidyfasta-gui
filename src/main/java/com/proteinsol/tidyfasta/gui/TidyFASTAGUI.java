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
import java.util.logging.Level;
import java.util.logging.Logger;

public class TidyFASTAGUI extends JFrame {
    private JPanel mainPanel;

    private JTextArea inputAreaFASTA;
    private JTextArea outputAreaFASTA;

    private JLabel numSubmitted;
    private JLabel numValid;

    private JButton openButton;
    private JButton submitButton;
    private JButton copyButton;
    private JButton saveButton;

    private JPanel inputButtonPanel;
    private JPanel inputTitlePanel;
    private JSplitPane splitPane;
    private JScrollPane outputScroll;
    private JPanel inputPane;
    private JPanel outputPane;
    private JScrollPane inputFASTAPane;
    private JPanel outputButtonPanel;
    private JPanel outputTitlePanel;

    private String submittedFASTA;
    private String validFASTA;
    private String lastSaveFileName;
    private StringBuilder errorMessage;

    transient Logger logger = Logger.getLogger(TidyFASTAGUI.class.getName());

    public TidyFASTAGUI(String title) {
        super(title);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        logger.log(Level.FINER, "Constructing GUI App");

        openButton.addActionListener(e -> openFileChooser());

        submitButton.addActionListener(e -> {
            clearOutput();
            handleSubmission(inputAreaFASTA.getText());
        });

        copyButton.addActionListener(e -> copyValidFASTAToClipboard());

        saveButton.addActionListener(e -> saveHandler());
    }


    public void handleSubmission(String submittedFASTA) {

        if (submittedFASTA.length() > 0) {
            if (submitSequence(submittedFASTA)) {
                displaySubmissionOutput();
            }
        } else {
            String errMsg = "No sequence submitted";
            logger.log(Level.INFO, errMsg);
            JOptionPane.showMessageDialog(mainPanel, errMsg);
        }
    }

    public void openFileChooser() {

        final JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "FASTA/TEXT FILES", "txt", "text", "fasta", "fa", "FASTA");
        chooser.setFileFilter(filter);
        int chooserResult = chooser.showOpenDialog(mainPanel);

        File fileFASTA;
        if (chooserResult == JFileChooser.APPROVE_OPTION) {
            fileFASTA = chooser.getSelectedFile();
            logger.log(Level.FINER, () -> "Opening file " + fileFASTA.toString());
            inputAreaFASTA.setText("");
            outputAreaFASTA.setText("");
        } else {
            return;
        }

        openFile(fileFASTA);
    }

    public void openFile(File fileFASTA) {
        List<String> lines;
        Path filenameFASTA = fileFASTA.toPath();

        try {
            lines = Files.readAllLines(filenameFASTA, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            String errMsg = filenameFASTA.toString() + " not found";
            logger.log(Level.INFO, errMsg);
            JOptionPane.showMessageDialog(mainPanel, errMsg);
            return;
        }

        assembleLines(lines);
        inputAreaFASTA.setText(submittedFASTA);
    }

    public void assembleLines(List<String> lines) {
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            result.append(line);
            result.append("\n");
        }
        result.setLength(result.length() - 1);

        submittedFASTA = result.toString();
    }

    public boolean submitSequence(String submittedFASTA) {

        ReadFASTAAndFormat objectFASTA = new ReadFASTAAndFormat(submittedFASTA);

        if (continueWithAnalysis(objectFASTA)) {
            numValid.setText("Number of valid sequences: " + objectFASTA.getValidatedNumber());
            numSubmitted.setText("Sequences: " + objectFASTA.getSubmittedNumber());

            logger.log(Level.FINER, () -> objectFASTA.getValidatedNumber() + "/" + objectFASTA.getSubmittedNumber()
                    + " valid");

            StringBuilder outputFASTAText = new StringBuilder();

            for (FASTAObject validFasta : objectFASTA.getArrayFASTA()) {
                outputFASTAText.append(validFasta.id);
                outputFASTAText.append("\n");
                outputFASTAText.append(validFasta.sequence);
                outputFASTAText.append("\n\n");
            }

            if (outputFASTAText.length() > 2) {
                outputFASTAText.setLength(outputFASTAText.length() - 2);
            } else {
                outputFASTAText.setLength(0);
            }

            validFASTA = outputFASTAText.toString();
            return true;

        } else {
            return false;
        }
    }

    private void displaySubmissionOutput() {
        outputAreaFASTA.setText(validFASTA);
    }

    public boolean continueWithAnalysis(ReadFASTAAndFormat objectFASTA) {

        if (objectFASTA.getNumErrors() > 0) {
            logger.log(Level.INFO, () -> "Found " + objectFASTA.getNumErrors() + " Errors\n"
                    + objectFASTA.getErrMsg());
            return testUserDesire(objectFASTA);
        } else {
            logger.log(Level.FINER, "No errors found");
            return true;
        }

    }

    public boolean testUserDesire(ReadFASTAAndFormat objectFASTA) {
        buildErrorMessage(objectFASTA);

        if (objectFASTA.getValidatedNumber() > 0) {
            logger.log(Level.FINER, "Does user want to continue without error sequences?");

            String errorDialog = this.errorMessage.toString() + "\nWould you like to continue?";

            int dialogResult = JOptionPane.showConfirmDialog(mainPanel,
                    errorDialog,
                    "FASTA Errors found",
                    JOptionPane.YES_NO_OPTION);

            return dialogResult != JOptionPane.NO_OPTION;

        } else {
            logger.log(Level.FINER, "Cannot continue as no valid options");
            JOptionPane.showMessageDialog(mainPanel, this.errorMessage.toString());
            return false;
        }
    }

    public void buildErrorMessage(ReadFASTAAndFormat objectFASTA) {

        StringBuilder userErrorMessage = new StringBuilder();

        if (objectFASTA.getValidatedNumber() > 0) {
            userErrorMessage.append(objectFASTA.getNumErrors());
            userErrorMessage.append("/");
            userErrorMessage.append(objectFASTA.getSubmittedNumber());
            userErrorMessage.append(" of the sequences submitted contained errors.\n\n");
        }

        if (objectFASTA.getNumErrors() == 1) {
            userErrorMessage.append("The following error occurred\n");
            userErrorMessage.append(objectFASTA.getErrMsg());
            userErrorMessage.append("\n");
        } else if (objectFASTA.getNumErrors() <= 10) {
            userErrorMessage.append("The following errors occurred\n");
            userErrorMessage.append(objectFASTA.getErrMsg());
            userErrorMessage.append("\n");
        } else {
            if (objectFASTA.getValidatedNumber() == 0) {
                userErrorMessage.append("All ");
                userErrorMessage.append(objectFASTA.getNumErrors());
                userErrorMessage.append(" sequences contained errors, the first 10 errors were:\n\n");
            } else {
                userErrorMessage.append("The first 10 errors were:\n\n");
            }

            String[] errorArray = objectFASTA.getErrMsgArray();
            for (int i = 0; i < 10; i++) {
                userErrorMessage.append(errorArray[i]).append("\n");
            }
            userErrorMessage.append("\n");
        }

        logger.log(Level.FINER, () -> "Showing user error message\n" + userErrorMessage);
        this.errorMessage = userErrorMessage;
    }

    public void clearOutput() {
        numValid.setText("Number of valid sequences: ");
        numSubmitted.setText("Sequences: ");
        outputAreaFASTA.setText("");
    }

    private void saveHandler() {
        if (validFASTA != null) {
            logger.log(Level.FINER, "Writing file");
            saveFileChooser();
        } else {
            String saveMsg = "Submit file before saving.";
            JOptionPane.showMessageDialog(mainPanel,
                    saveMsg);
        }
    }

    private void saveFileChooser() {
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
            if (writeFile(fileToSave)) {
                savePopup();
            }
        } else {
            logger.log(Level.INFO, "No file chosen for saving");
        }
    }

    public boolean writeFile(File fileToSave) {

        String fileString = fileToSave.toString();

        if (!fileString.contains(".")) {
            logger.log(Level.INFO, () -> "Renaming " + fileToSave.toString() + " to "
                    + fileToSave.toString() + ".fasta");
            fileString = fileString + ".fasta";
        }

        Path saveFileName = Paths.get(fileString);

        if (Files.exists(saveFileName)) {

            logger.log(Level.INFO, () -> saveFileName.toString() + " already exists.");

            int dialogResult = JOptionPane.showConfirmDialog(mainPanel,
                    saveFileName.toString() + " already exists." +
                            "\nWould you like to overwrite it?",
                    "FASTA Errors found",
                    JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.NO_OPTION) {
                logger.log(Level.FINER, () -> saveFileName.toString() + " not overwritten.");
                return false;
            } else {
                logger.log(Level.FINER, () -> saveFileName.toString() + " overwritten.");
                return true;
            }
        }

        try {
            Files.write(saveFileName, Collections.singleton(validFASTA));
            lastSaveFileName = saveFileName.toString();
            return true;
        } catch (IOException err) {
            String saveMsg = saveFileName.toString() + " not writable.";
            logger.log(Level.INFO, saveMsg);
            JOptionPane.showMessageDialog(mainPanel, saveMsg);
            return false;
        }
    }

    private void savePopup() {
        String saveMsg = lastSaveFileName + " saved!";
        logger.log(Level.INFO, saveMsg);
        JOptionPane.showMessageDialog(mainPanel, saveMsg);
    }

    public void copyValidFASTAToClipboard() {
        StringSelection validFASTASelect = new StringSelection(validFASTA);
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(validFASTASelect, null);
        logger.log(Level.FINER, "Contents saved to clipboard");
    }

    public static void main(String[] args) {
        JFrame frame = new TidyFASTAGUI("Tidy FASTA");
        frame.setVisible(true);
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
        inputPane = new JPanel();
        inputPane.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane.setLeftComponent(inputPane);
        inputButtonPanel = new JPanel();
        inputButtonPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        inputPane.add(inputButtonPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        openButton.setMnemonic('O');
        openButton.setDisplayedMnemonicIndex(0);
        openButton.setToolTipText("");
        inputButtonPanel.add(openButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        submitButton = new JButton();
        submitButton.setText("Submit");
        submitButton.setMnemonic('S');
        submitButton.setDisplayedMnemonicIndex(0);
        inputButtonPanel.add(submitButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inputTitlePanel = new JPanel();
        inputTitlePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        inputPane.add(inputTitlePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Raw FASTA");
        label1.setDisplayedMnemonic('R');
        label1.setDisplayedMnemonicIndex(0);
        inputTitlePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inputFASTAPane = new JScrollPane();
        inputPane.add(inputFASTAPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        inputAreaFASTA = new JTextArea();
        inputAreaFASTA.setLineWrap(true);
        inputFASTAPane.setViewportView(inputAreaFASTA);
        numSubmitted = new JLabel();
        numSubmitted.setText("Sequences:");
        numSubmitted.setDisplayedMnemonic('E');
        numSubmitted.setDisplayedMnemonicIndex(1);
        inputPane.add(numSubmitted, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputPane = new JPanel();
        outputPane.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane.setRightComponent(outputPane);
        outputButtonPanel = new JPanel();
        outputButtonPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        outputPane.add(outputButtonPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        saveButton.setMnemonic('A');
        saveButton.setDisplayedMnemonicIndex(1);
        outputButtonPanel.add(saveButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setText("Copy");
        copyButton.setMnemonic('C');
        copyButton.setDisplayedMnemonicIndex(0);
        outputButtonPanel.add(copyButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputTitlePanel = new JPanel();
        outputTitlePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        outputPane.add(outputTitlePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Validated FASTA");
        label2.setDisplayedMnemonic('V');
        label2.setDisplayedMnemonicIndex(0);
        outputTitlePanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputScroll = new JScrollPane();
        outputPane.add(outputScroll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        outputAreaFASTA = new JTextArea();
        outputAreaFASTA.setEditable(false);
        outputAreaFASTA.setEnabled(true);
        outputAreaFASTA.setLineWrap(true);
        outputAreaFASTA.setText("");
        outputScroll.setViewportView(outputAreaFASTA);
        numValid = new JLabel();
        numValid.setText("Valid Sequences:");
        numValid.setDisplayedMnemonic('L');
        numValid.setDisplayedMnemonicIndex(2);
        outputPane.add(numValid, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, 5), null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, 5), null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 0, false));
        final Spacer spacer4 = new Spacer();
        mainPanel.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 0, false));
        label1.setLabelFor(inputAreaFASTA);
        numSubmitted.setLabelFor(inputFASTAPane);
        label2.setLabelFor(outputAreaFASTA);
        numValid.setLabelFor(outputScroll);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    public String getValidOutput() {
        return validFASTA;
    }

    public String getSubmittedFASTA() {
        return submittedFASTA;
    }

    public String getErrorMessages() {
        return errorMessage.toString();
    }
}
