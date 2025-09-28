package view.frame;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class JoinFrame extends JFrame implements ActionListener {

    JTextField idField = new JTextField();
    JButton idCheckBtn = new JButton("중복 확인");

    JPasswordField pwField = new JPasswordField();
    JPasswordField pwConfirmField = new JPasswordField();
    JLabel pwStrengthLabel = new JLabel("0%");

    JTextField nicknameField = new JTextField();
    JButton nicknameCheckBtn = new JButton("중복 확인");

    JTextField emailField = new JTextField();
    JComboBox<String> emailDomainCombo = new JComboBox<>(new String[]{"직접입력", "naver.com", "gmail.com", "daum.net"});

    JTextField nameField = new JTextField();
    JComboBox<String> genderCombo = new JComboBox<>(new String[]{"남", "여", "기타"});

    JDatePickerImpl datePicker;

    JComboBox<String> phonePrefixCombo = new JComboBox<>(new String[]{"010", "011", "016"});
    JTextField phoneField = new JTextField();

    JTextField postalField = new JTextField();
    JButton postalBtn = new JButton("우편번호");

    JTextField addressField = new JTextField();
    JTextField detailAddressField = new JTextField();

    JLabel profileLabel = new JLabel("사진 없음");
    JButton uploadBtn = new JButton("사진 업로드");

    JButton joinBtn = new JButton("회원가입");
    JButton backBtn = new JButton("뒤로 가기");

    File profileImageFile = null;

    public JoinFrame() {
        setTitle("회원가입");
        setSize(600, 800);
        setLayout(null);

        int y = 20, h = 30, gap = 10;

        addRow("아이디", idField, idCheckBtn, y); y += h + gap;
        addRow("비밀번호", pwField, null, y); y += h + gap;
        addRow("비밀번호 확인", pwConfirmField, pwStrengthLabel, y); y += h + gap;
        addRow("닉네임", nicknameField, nicknameCheckBtn, y); y += h + gap;

        addLabel("이메일", y);
        emailField.setBounds(150, y, 150, h); add(emailField);
        JLabel atLabel = new JLabel("@"); atLabel.setBounds(310, y, 20, h); add(atLabel);
        emailDomainCombo.setBounds(340, y, 100, h); add(emailDomainCombo);
        y += h + gap;

        addRow("이름", nameField, null, y); y += h + gap;

        addLabel("성별", y);
        genderCombo.setBounds(150, y, 150, h); add(genderCombo);
        y += h + gap;

        // 생년월일 - JDatePicker 사용
        addLabel("생년월일", y);
        UtilDateModel model = new UtilDateModel();
//        Properties p = new Properties();
//        p.put("text.today", "오늘");
//        p.put("text.month", "월");
//        p.put("text.year", "년");
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setBounds(150, y, 260, h);
        add(datePicker);
        y += h + gap;

        addLabel("전화번호", y);
        phonePrefixCombo.setBounds(150, y, 80, h); add(phonePrefixCombo);
        phoneField.setBounds(240, y, 170, h); add(phoneField);
        y += h + gap;

        addLabel("우편번호", y);
        postalField.setBounds(150, y, 150, h); add(postalField);
        postalBtn.setBounds(310, y, 100, h); add(postalBtn);
        y += h + gap;

        addRow("주소", addressField, null, y); y += h + gap;
        addRow("상세주소", detailAddressField, null, y); y += h + gap;

        addLabel("프로필 사진", y);
        profileLabel.setBounds(150, y, 150, h); add(profileLabel);
        uploadBtn.setBounds(310, y, 100, h); add(uploadBtn);
        y += h + gap + 10;

        joinBtn.setBounds(150, y, 120, 40); add(joinBtn);
        backBtn.setBounds(280, y, 120, 40); add(backBtn);

        // 이벤트 등록
        uploadBtn.addActionListener(this);
        postalBtn.addActionListener(this);
        joinBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setVisible(true);
    }

    private void addLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(50, y, 100, 30);
        add(label);
    }

    private void addRow(String labelText, JComponent field, JComponent right, int y) {
        addLabel(labelText, y);
        field.setBounds(150, y, 150, 30); add(field);
        if (right != null) {
            right.setBounds(310, y, 100, 30);
            add(right);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadBtn) {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                profileImageFile = chooser.getSelectedFile();
                profileLabel.setText(profileImageFile.getName());
            }
        } else if (e.getSource() == postalBtn) {
            JOptionPane.showMessageDialog(this, "우편번호 검색 기능은 구현 필요");
        } else if (e.getSource() == joinBtn) {
            // 생년월일 가져오기
            Calendar selectedDate = (Calendar) datePicker.getModel().getValue();
            String birthDate = "";
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthDate = sdf.format(selectedDate.getTime());
            }

            JOptionPane.showMessageDialog(this, "회원가입 요청 완료\n생년월일: " + birthDate);
        } else if (e.getSource() == backBtn) {
            this.dispose(); // 창 닫기
        }
    }

    // 내부 클래스: 날짜 포맷터
    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
