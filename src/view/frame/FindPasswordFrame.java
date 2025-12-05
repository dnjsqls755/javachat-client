package view.frame;

import app.Application;
import dto.request.FindPasswordRequest;
import dto.request.ResetPasswordRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindPasswordFrame extends JFrame implements ActionListener {
    
    private static final Color SAND = new Color(244, 236, 221);
    private static final Color KAKAO_YELLOW = new Color(255, 232, 18);
    private static final Color DEEP_BROWN = new Color(52, 40, 32);
    private static final Font DEFAULT_FONT = new Font("맑은 고딕", Font.PLAIN, 13);
    
    private JTextField idField = new JTextField();
    private JTextField emailField = new JTextField();
    private JPasswordField newPasswordField = new JPasswordField();
    private JPasswordField confirmPasswordField = new JPasswordField();
    private JButton verifyButton = new JButton("확인");
    private JButton resetButton = new JButton("비밀번호 재설정");
    private JButton cancelButton = new JButton("취소");
    
    private JPanel verifyPanel;
    private JPanel resetPanel;
    
    private boolean verified = false;
    
    public FindPasswordFrame() {
        Application.findPasswordFrame = this;
        
        setTitle("비밀번호 찾기");
        setSize(450, 450);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SAND);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(KAKAO_YELLOW);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // 제목
        JLabel titleLabel = new JLabel("비밀번호 찾기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(DEEP_BROWN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // 설명
        JLabel descLabel = new JLabel("아이디와 이메일을 입력하여 본인 확인 후");
        descLabel.setFont(DEFAULT_FONT);
        descLabel.setForeground(new Color(70, 70, 70));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descLabel);
        
        JLabel descLabel2 = new JLabel("새로운 비밀번호를 설정할 수 있습니다.");
        descLabel2.setFont(DEFAULT_FONT);
        descLabel2.setForeground(new Color(70, 70, 70));
        descLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descLabel2);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // 본인 확인 패널
        verifyPanel = createVerifyPanel();
        contentPanel.add(verifyPanel);
        
        // 비밀번호 재설정 패널 (처음에는 숨김)
        resetPanel = createResetPanel();
        resetPanel.setVisible(false);
        contentPanel.add(resetPanel);
        
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }
    
    private JPanel createVerifyPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // 아이디 입력
        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.setOpaque(false);
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel idLabel = new JLabel("아이디");
        idLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        idLabel.setForeground(DEEP_BROWN);
        idLabel.setPreferredSize(new Dimension(100, 30));
        idField.setFont(DEFAULT_FONT);
        styleField(idField);
        idPanel.add(idLabel, BorderLayout.WEST);
        idPanel.add(idField, BorderLayout.CENTER);
        panel.add(idPanel);
        panel.add(Box.createVerticalStrut(15));
        
        // 이메일 입력
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setOpaque(false);
        emailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel emailLabel = new JLabel("이메일");
        emailLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        emailLabel.setForeground(DEEP_BROWN);
        emailLabel.setPreferredSize(new Dimension(100, 30));
        emailField.setFont(DEFAULT_FONT);
        styleField(emailField);
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        panel.add(emailPanel);
        panel.add(Box.createVerticalStrut(25));
        
        // 확인 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        styleButton(verifyButton);
        verifyButton.addActionListener(this);
        styleButton(cancelButton);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.addActionListener(this);
        buttonPanel.add(verifyButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createResetPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // 새 비밀번호 입력
        JPanel newPwPanel = new JPanel(new BorderLayout());
        newPwPanel.setOpaque(false);
        newPwPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel newPwLabel = new JLabel("새 비밀번호");
        newPwLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        newPwLabel.setForeground(DEEP_BROWN);
        newPwLabel.setPreferredSize(new Dimension(100, 30));
        newPasswordField.setFont(DEFAULT_FONT);
        styleField(newPasswordField);
        newPwPanel.add(newPwLabel, BorderLayout.WEST);
        newPwPanel.add(newPasswordField, BorderLayout.CENTER);
        panel.add(newPwPanel);
        panel.add(Box.createVerticalStrut(15));
        
        // 비밀번호 확인 입력
        JPanel confirmPwPanel = new JPanel(new BorderLayout());
        confirmPwPanel.setOpaque(false);
        confirmPwPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel confirmPwLabel = new JLabel("비밀번호 확인");
        confirmPwLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        confirmPwLabel.setForeground(DEEP_BROWN);
        confirmPwLabel.setPreferredSize(new Dimension(100, 30));
        confirmPasswordField.setFont(DEFAULT_FONT);
        styleField(confirmPasswordField);
        confirmPwPanel.add(confirmPwLabel, BorderLayout.WEST);
        confirmPwPanel.add(confirmPasswordField, BorderLayout.CENTER);
        panel.add(confirmPwPanel);
        panel.add(Box.createVerticalStrut(25));
        
        // 재설정 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        styleButton(resetButton);
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private void styleField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(200, 35));
    }
    
    private void styleButton(JButton btn) {
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)));
        btn.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == verifyButton) {
            String id = idField.getText().trim();
            String email = emailField.getText().trim();
            
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이메일을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "올바른 이메일 형식을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 서버에 본인 확인 요청
            Application.sender.sendMessage(new FindPasswordRequest(id, email));
            verifyButton.setEnabled(false);
            verifyButton.setText("확인 중...");
        }
        
        if (e.getSource() == resetButton) {
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            
            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "새 비밀번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPassword.length() < 4) {
                JOptionPane.showMessageDialog(this, "비밀번호는 최소 4자 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 서버에 비밀번호 재설정 요청
            String id = idField.getText().trim();
            Application.sender.sendMessage(new ResetPasswordRequest(id, newPassword));
            resetButton.setEnabled(false);
            resetButton.setText("재설정 중...");
        }
        
        if (e.getSource() == cancelButton) {
            dispose();
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    public void handleVerifySuccess() {
        verifyButton.setEnabled(true);
        verifyButton.setText("확인");
        verified = true;
        
        // 본인 확인 패널 숨기고 비밀번호 재설정 패널 표시
        verifyPanel.setVisible(false);
        resetPanel.setVisible(true);
        
        JOptionPane.showMessageDialog(this, 
                "본인 확인이 완료되었습니다.\n새 비밀번호를 설정하세요.", 
                "본인 확인 완료", 
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void handleVerifyFailure(String message) {
        verifyButton.setEnabled(true);
        verifyButton.setText("확인");
        JOptionPane.showMessageDialog(this, message, "본인 확인 실패", JOptionPane.ERROR_MESSAGE);
    }
    
    public void handleResetSuccess() {
        resetButton.setEnabled(true);
        resetButton.setText("비밀번호 재설정");
        
        JOptionPane.showMessageDialog(this, 
                "비밀번호가 성공적으로 재설정되었습니다.\n새 비밀번호로 로그인하세요.", 
                "비밀번호 재설정 완료", 
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
    
    public void handleResetFailure(String message) {
        resetButton.setEnabled(true);
        resetButton.setText("비밀번호 재설정");
        JOptionPane.showMessageDialog(this, message, "비밀번호 재설정 실패", JOptionPane.ERROR_MESSAGE);
    }
}
