package view.frame;

import app.Application;
import dto.request.FindIdRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindIdFrame extends JFrame implements ActionListener {
    
    private static final Color SAND = new Color(244, 236, 221);
    private static final Color KAKAO_YELLOW = new Color(255, 232, 18);
    private static final Color DEEP_BROWN = new Color(52, 40, 32);
    private static final Font DEFAULT_FONT = new Font("맑은 고딕", Font.PLAIN, 13);
    
    private JTextField nameField = new JTextField();
    private JTextField emailField = new JTextField();
    private JButton findButton = new JButton("아이디 찾기");
    private JButton cancelButton = new JButton("취소");
    
    public FindIdFrame() {
        Application.findIdFrame = this;
        
        setTitle("아이디 찾기");
        setSize(450, 350);
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
        JLabel titleLabel = new JLabel("아이디 찾기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(DEEP_BROWN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // 설명
        JLabel descLabel = new JLabel("가입 시 등록한 이름과 이메일을 입력하세요.");
        descLabel.setFont(DEFAULT_FONT);
        descLabel.setForeground(new Color(70, 70, 70));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        
        // 이름 입력
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setOpaque(false);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        nameLabel.setForeground(DEEP_BROWN);
        nameLabel.setPreferredSize(new Dimension(80, 30));
        nameField.setFont(DEFAULT_FONT);
        styleField(nameField);
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);
        contentPanel.add(namePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // 이메일 입력
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setOpaque(false);
        emailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel emailLabel = new JLabel("이메일");
        emailLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        emailLabel.setForeground(DEEP_BROWN);
        emailLabel.setPreferredSize(new Dimension(80, 30));
        emailField.setFont(DEFAULT_FONT);
        styleField(emailField);
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        contentPanel.add(emailPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        styleButton(findButton);
        findButton.addActionListener(this);
        styleButton(cancelButton);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.addActionListener(this);
        
        buttonPanel.add(findButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);
        
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
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
        if (e.getSource() == findButton) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
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
            
            // 서버에 아이디 찾기 요청
            Application.sender.sendMessage(new FindIdRequest(name, email));
            findButton.setEnabled(false);
            findButton.setText("조회 중...");
        }
        
        if (e.getSource() == cancelButton) {
            dispose();
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    public void handleFindIdSuccess(String userId) {
        findButton.setEnabled(true);
        findButton.setText("아이디 찾기");
        
        JOptionPane.showMessageDialog(this, 
                "회원님의 아이디는 다음과 같습니다.\n\n아이디: " + userId, 
                "아이디 찾기 완료", 
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
    
    public void handleFindIdFailure(String message) {
        findButton.setEnabled(true);
        findButton.setText("아이디 찾기");
        JOptionPane.showMessageDialog(this, message, "아이디 찾기 실패", JOptionPane.ERROR_MESSAGE);
    }
}
